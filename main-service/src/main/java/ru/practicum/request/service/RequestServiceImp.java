package ru.practicum.request.service;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.event.model.Event;
import ru.practicum.event.model.EventState;
import ru.practicum.event.repository.EventRepository;
import ru.practicum.exception.ConflictException;
import ru.practicum.exception.NotFoundException;
import ru.practicum.request.dto.RequestDto;
import ru.practicum.request.mapper.RequestMapper;
import ru.practicum.request.model.Request;
import ru.practicum.request.model.RequestStatus;
import ru.practicum.request.repository.RequestRepository;
import ru.practicum.user.model.User;
import ru.practicum.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;

@Service
@AllArgsConstructor
@Transactional(readOnly = true)
public class RequestServiceImp implements RequestService {

    private final RequestRepository requestRepository;
    private final EventRepository eventRepository;
    private final UserRepository userRepository;

    @Override
    @Transactional
    public RequestDto createRequest(Long userId, Long eventId) {
        User user = checkUser(userId);
        Event event = checkEvent(eventId);

        if (event.getParticipantLimit() <= event.getConfirmedRequests() && event.getParticipantLimit() != 0) {
            throw new ConflictException("Количество запросов привышает лимит");
        }
        if (event.getInitiator().getId().equals(userId)) {
            throw new ConflictException("Пользователь не может отправить запрос на участие в своем мероприятии");
        }
        if (requestRepository.existsByRequesterIdAndEventId(userId, eventId)) {
            throw new ConflictException("Юзер уже участвует в мероприятии");
        }

        if (event.getState() != EventState.PUBLISHED) {
            throw new ConflictException("Событие не опубликовано");
        } else {
            Request request = Request.builder()
                    .created(LocalDateTime.now())
                    .status(RequestStatus.PENDING)
                    .event(event)
                    .requester(user)
                    .build();

            if (!event.getRequestModeration() || event.getParticipantLimit() == 0) {
                request.setStatus(RequestStatus.CONFIRMED);
                request = requestRepository.save(request);
                event.setConfirmedRequests(requestRepository.countAllByEventIdAndStatus(eventId, RequestStatus.CONFIRMED));
                eventRepository.save(event);

                return RequestMapper.toRequestDto(request);
            }

            request = requestRepository.save(request);

            return RequestMapper.toRequestDto(request);
        }
    }

    @Override
    public List<RequestDto> getRequestsByUserId(Long userId) {
        checkUser(userId);
        List<Request> requestList = requestRepository.findByRequesterId(userId);

        return RequestMapper.toRequestDtoList(requestList);
    }

    @Override
    @Transactional
    public RequestDto cancelRequest(Long userId, Long requestId) {
        checkUser(userId);
        Request request = checkRequest(requestId);

        request.setStatus(RequestStatus.CANCELED);

        return RequestMapper.toRequestDto(requestRepository.save(request));
    }

    private Request checkRequest(Long requestId) {
        return requestRepository.findById(requestId).orElseThrow(() -> new NotFoundException("Нет такого события"));
    }

    private User checkUser(Long userId) {
        return userRepository.findById(userId).orElseThrow(() -> new NotFoundException("Нет такого юзера"));
    }

    private Event checkEvent(Long eventId) {
        return eventRepository.findById(eventId).orElseThrow(() -> new NotFoundException("Нет такого события"));
    }

}
