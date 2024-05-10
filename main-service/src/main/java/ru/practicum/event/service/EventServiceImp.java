package ru.practicum.event.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.StatsClient;
import ru.practicum.additionally.Constants;
import ru.practicum.category.model.Category;
import ru.practicum.category.repository.CategoryRepository;
import ru.practicum.dto.StatDto;
import ru.practicum.dto.StatResponseDto;
import ru.practicum.event.dto.*;
import ru.practicum.event.mapper.EventMapper;
import ru.practicum.event.model.Event;
import ru.practicum.event.model.EventState;
import ru.practicum.event.model.StateAction;
import ru.practicum.event.repository.EventRepository;
import ru.practicum.exception.ConflictException;
import ru.practicum.exception.NotFoundException;
import ru.practicum.exception.ValidationException;
import ru.practicum.location.mapper.LocationMapper;
import ru.practicum.location.model.Location;
import ru.practicum.location.repository.LocationRepository;
import ru.practicum.request.dto.RequestDto;
import ru.practicum.request.mapper.RequestMapper;
import ru.practicum.request.model.Request;
import ru.practicum.request.model.RequestStatus;
import ru.practicum.request.repository.RequestRepository;
import ru.practicum.user.model.User;
import ru.practicum.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@AllArgsConstructor
@Transactional(readOnly = true)
public class EventServiceImp implements EventService {


    private final EventRepository eventRepository;
    private final UserRepository userRepository;

    private final CategoryRepository categoryRepository;

    private final RequestRepository requestRepository;

    private final LocationRepository locationRepository;

    private final StatsClient statsClient;

    private final ObjectMapper objectMapper = new ObjectMapper();


    @Override
    @Transactional
    public EventFullDto createEvent(Long userId, EventDtoNew eventDtoNew) {
        User user = checkUser(userId);
        Category category = checkCategory(eventDtoNew.getCategory());
        Location location = locationRepository.save(LocationMapper.toLocation(eventDtoNew.getLocation()));
        Event event = EventMapper.toEvent(eventDtoNew, category, location, user);
        eventRepository.save(event);

        return EventMapper.toEventFullDto(event);
    }

    @Override
    public List<EventShortDto> getAllEventsByUserId(Long userId, Integer from, Integer size) {

        checkUser(userId);
        PageRequest pageRequest = PageRequest.of(from / size, size);
        List<Event> events = eventRepository.findByInitiatorId(userId, pageRequest);

        return EventMapper.toEventShortDtoList(events);
    }

    @Override
    public EventFullDto getUserEventById(Long userId, Long eventId) {
        checkUser(userId);
        checkEvent(eventId);
        Event event = eventRepository.findByInitiatorIdAndId(userId, eventId);

        return EventMapper.toEventFullDto(event);
    }

    @Override
    @Transactional
    public EventFullDto updateEventByUserId(EventDtoUpdate eventUpdateDto, Long userId, Long eventId) {
        User user = checkUser(userId);
        Event event = checkEvent(eventId);

        if (!user.getId().equals(event.getInitiator().getId())) {
            throw new ConflictException("Юзер не создавал это событие");
        }
        if (event.getState().equals(EventState.PUBLISHED)) {
            throw new ConflictException("Юзер не может обновить событие которое было опубликовано");
        }

        Event updateEvent = baseUpdateEvent(event, eventUpdateDto);

        return EventMapper.toEventFullDto(updateEvent);
    }

    @Override
    public List<RequestDto> getRequestsForEventIdByUserId(Long userId, Long eventId) {

        User user = checkUser(userId);
        Event event = checkEvent(eventId);

        if (!user.getId().equals(event.getInitiator().getId())) {
            throw new ConflictException("Юзер не создавал это событие");
        }

        List<Request> requests = requestRepository.findByEventId(eventId);

        return RequestMapper.toRequestDtoList(requests);
    }

    @Override
    @Transactional
    public RequestUpdateDtoResult updateStatusRequestsForEventIdByUserId(RequestUpdateDtoRequest requestDto, Long userId, Long eventId) {

        User user = checkUser(userId);
        Event event = checkEvent(eventId);

        RequestUpdateDtoResult result = RequestUpdateDtoResult.builder()
                .confirmedRequests(Collections.emptyList())
                .rejectedRequests(Collections.emptyList())
                .build();

        if (!user.getId().equals(event.getInitiator().getId())) {
            throw new ConflictException("Юзер не создавал это событие");
        }

        if (!event.getRequestModeration() || event.getParticipantLimit() == 0) {
            return result;
        }

        if (event.getConfirmedRequests() >= event.getParticipantLimit()) {
            throw new ConflictException("Превышен лимит участников");
        }

        List<Request> confirmedRequests = new ArrayList<>();
        List<Request> rejectedRequests = new ArrayList<>();

        long vacantPlace = event.getParticipantLimit() - event.getConfirmedRequests();

        List<Request> requestsList = requestRepository.findAllById(requestDto.getRequestIds());

        for (Request request : requestsList) {
            if (!request.getStatus().equals(RequestStatus.PENDING)) {
                throw new ConflictException("Запрос должен иметь статус ожидание");
            }

            if (requestDto.getStatus().equals(RequestStatus.CONFIRMED) && vacantPlace > 0) {
                request.setStatus(RequestStatus.CONFIRMED);
                event.setConfirmedRequests(requestRepository.countAllByEventIdAndStatus(eventId, RequestStatus.CONFIRMED));
                confirmedRequests.add(request);
                vacantPlace--;
            } else {
                request.setStatus(RequestStatus.REJECTED);
                rejectedRequests.add(request);
            }
        }

        result.setConfirmedRequests(RequestMapper.toRequestDtoList(confirmedRequests));
        result.setRejectedRequests(RequestMapper.toRequestDtoList(rejectedRequests));

        eventRepository.save(event);
        requestRepository.saveAll(requestsList);

        return result;
    }

    @Override
    @Transactional
    public EventFullDto updateEventByAdmin(EventDtoUpdate eventUpdateDto, Long eventId) {
        Event event = checkEvent(eventId);

        if (eventUpdateDto.getStateAction() != null) {
            if (eventUpdateDto.getStateAction().equals(StateAction.PUBLISH_EVENT)) {

                if (!event.getState().equals(EventState.PENDING)) {
                    throw new ConflictException("Событие не может быть обуликовано повторно");
                }
                event.setPublishedOn(LocalDateTime.now());
                event.setState(EventState.PUBLISHED);

            } else {

                if (!event.getState().equals(EventState.PENDING)) {
                    throw new ConflictException("Событие не может быть отменено");
                }
                event.setState(EventState.CANCELED);
            }
        }

        Event updateEvent = baseUpdateEvent(event, eventUpdateDto);

        return EventMapper.toEventFullDto(updateEvent);
    }

    @Override
    public List<EventFullDto> getEventsByAdmin(List<Long> users, List<String> states, List<Long> categories, String start, String end, Integer from, Integer size) {
        LocalDateTime startTime = parseDate(start);
        LocalDateTime endTime = parseDate(end);

        List<EventState> statesValue = new ArrayList<>();

        if (states != null) {
            for (String state : states) {
                statesValue.add(getStateValue(state));
            }
        }

        if (startTime != null && endTime != null) {
            if (startTime.isAfter(endTime)) {
                throw new ValidationException("Начало должно быть после окончания");
            }
        }

        PageRequest pageRequest = PageRequest.of(from / size, size);
        List<Event> events = eventRepository.findEventsByAdminFromParam(users, statesValue, categories, startTime, endTime, pageRequest);

        return EventMapper.toEventFullDtoList(events);
    }

    @Override
    public EventFullDto getEventById(Long eventId, String uri, String ip) {
        Event event = checkEvent(eventId);
        if (!event.getState().equals(EventState.PUBLISHED)) {
            throw new NotFoundException("События не опубликованы");
        }

        sendInfo(uri, ip);
        event.setViews(getViewsEventById(event.getId()));
        eventRepository.save(event);

        return EventMapper.toEventFullDto(event);
    }

    @Override
    public List<EventShortDto> getEventsByPublic(String text, List<Long> categories, Boolean paid,
                                                 String start, String end, Boolean onlyAvailable, String sort,
                                                 Integer from, Integer size, String uri, String ip) {

        LocalDateTime startTime = parseDate(start);
        LocalDateTime endTime = parseDate(end);

        if (startTime != null && endTime != null) {
            if (startTime.isAfter(endTime)) {
                throw new ValidationException("Стар не может быть позже конца");
            }
        }

        PageRequest pageRequest = PageRequest.of(from / size, size);
        List<Event> events = getEventsWithViews(text, categories, paid, startTime, endTime, onlyAvailable, sort, pageRequest);

        sendInfo(uri, ip);
        return EventMapper.toEventShortDtoList(events);
    }

    private User checkUser(Long userId) {
        return userRepository.findById(userId).orElseThrow(() -> new NotFoundException("Нет такого юзера"));
    }

    private Category checkCategory(Long categoryId) {
        return categoryRepository.findById(categoryId).orElseThrow(() -> new NotFoundException("Нет такой категории"));
    }

    private Event checkEvent(Long eventId) {
        return eventRepository.findById(eventId).orElseThrow(() -> new NotFoundException("Нет такого юзера"));
    }

    private LocalDateTime parseDate(String date) {
        if (date != null) {
            return LocalDateTime.parse(date, Constants.FORMATTER);
        } else {
            return null;
        }
    }

    private EventState getStateValue(String state) {
        try {
            return EventState.valueOf(state);
        } catch (Exception e) {
            throw new ValidationException("Unknown state: " + state);
        }
    }

    private Event baseUpdateEvent(Event event, EventDtoUpdate eventDtoUpdate) {

        if (eventDtoUpdate.getAnnotation() != null && !eventDtoUpdate.getAnnotation().isBlank()) {
            event.setAnnotation(eventDtoUpdate.getAnnotation());
        }
        if (eventDtoUpdate.getCategory() != null) {
            event.setCategory(checkCategory(eventDtoUpdate.getCategory()));
        }
        if (eventDtoUpdate.getDescription() != null && !eventDtoUpdate.getDescription().isBlank()) {
            event.setDescription(eventDtoUpdate.getDescription());
        }
        if (eventDtoUpdate.getEventDate() != null) {
            event.setEventDate(eventDtoUpdate.getEventDate());
        }
        if (eventDtoUpdate.getLocation() != null) {
            event.setLocation(LocationMapper.toLocation(eventDtoUpdate.getLocation()));
        }
        if (eventDtoUpdate.getPaid() != null) {
            event.setPaid(eventDtoUpdate.getPaid());
        }
        if (eventDtoUpdate.getParticipantLimit() != null) {
            event.setParticipantLimit(eventDtoUpdate.getParticipantLimit());
        }
        if (eventDtoUpdate.getRequestModeration() != null) {
            event.setRequestModeration(eventDtoUpdate.getRequestModeration());
        }
        if (eventDtoUpdate.getStateAction() != null) {
            if (eventDtoUpdate.getStateAction() == StateAction.PUBLISH_EVENT) {
                event.setState(EventState.PUBLISHED);
                event.setPublishedOn(LocalDateTime.now());
            } else if (eventDtoUpdate.getStateAction() == StateAction.REJECT_EVENT ||
                    eventDtoUpdate.getStateAction() == StateAction.CANCEL_REVIEW) {
                event.setState(EventState.CANCELED);
            } else if (eventDtoUpdate.getStateAction() == StateAction.SEND_TO_REVIEW) {
                event.setState(EventState.PENDING);
            }
        }
        if (eventDtoUpdate.getTitle() != null && !eventDtoUpdate.getTitle().isBlank()) {
            event.setTitle(eventDtoUpdate.getTitle());
        }

        locationRepository.save(event.getLocation());
        return eventRepository.save(event);
    }

    private void sendInfo(String uri, String ip) {
        StatDto hitDto = StatDto.builder()
                .app("main-service")
                .uri(uri)
                .ip(ip)
                .timestamp(LocalDateTime.now())
                .build();
        statsClient.createStat(hitDto);
    }

    private Long getViewsEventById(Long eventId) {

        String uri = "/events/" + eventId;
        ResponseEntity<Object> response = statsClient.readStat(Constants.START_HISTORY, LocalDateTime.now(), uri, true);
        List<StatResponseDto> result = objectMapper.convertValue(response.getBody(), new TypeReference<>() {
        });

        if (result.isEmpty()) {
            return 0L;
        } else {
            return result.get(0).getHits();
        }
    }

    private List<Event> getEventsWithViews(String text, List<Long> categories, Boolean paid, LocalDateTime startTime, LocalDateTime endTime, Boolean onlyAvailable, String sort, PageRequest pageRequest) {
        List<Event> events = eventRepository.findEventsByPublicFromParam(text, categories, paid, startTime, endTime, onlyAvailable, sort, pageRequest);
        List<Long> eventIds = events.stream().map(Event::getId).collect(Collectors.toList());
        Map<Long, Long> viewsMap = getViewsForEvents(eventIds);
        events.forEach(event -> event.setViews(viewsMap.getOrDefault(event.getId(), 0L)));
        return events;
    }
    private Map<Long, Long> getViewsForEvents(List<Long> eventIds) {
        Map<Long, Long> viewsMap = new HashMap<>();
        if (!eventIds.isEmpty()) {
            List<String> uris = eventIds.stream().map(id -> "/events/" + id).collect(Collectors.toList());
            String urisParam = String.join(",", uris);
            ResponseEntity<Object> response = statsClient.readStat(Constants.START_HISTORY, LocalDateTime.now(), urisParam, true);
            List<StatResponseDto> result = objectMapper.convertValue(response.getBody(), new TypeReference<>() {});
            result.forEach(stat -> viewsMap.put(getEventIdFromUri(stat.getUri()), stat.getHits()));
        }
        return viewsMap;
    }

    private Long getEventIdFromUri(String uri) {
        String[] parts = uri.split("/");
        return Long.parseLong(parts[parts.length - 1]);
    }
}
