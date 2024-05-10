package ru.practicum.event.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.additionally.Update;
import ru.practicum.event.dto.*;
import ru.practicum.event.service.EventService;
import ru.practicum.request.dto.RequestDto;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/users/{userId}/events")
public class EventPrivateController {

    private final EventService eventService;


    @PostMapping
    @ResponseStatus(value = HttpStatus.CREATED)
    public EventFullDto createEvent(@Valid @RequestBody EventDtoNew eventDtoNew,
                                    @PathVariable Long userId) {

        log.info("Создание событий");
        return eventService.createEvent(userId, eventDtoNew);
    }

    @GetMapping
    @ResponseStatus(value = HttpStatus.OK)
    public List<EventShortDto> getAllEventsByUserId(@PathVariable Long userId,
                                                    @PositiveOrZero @RequestParam(defaultValue = "0") Integer from,
                                                    @Positive @RequestParam(defaultValue = "10") Integer size) {

        log.info("Получение всех событий пользователя с id {}", userId);
        return eventService.getAllEventsByUserId(userId, from, size);
    }

    @GetMapping("/{eventId}")
    @ResponseStatus(value = HttpStatus.OK)
    public EventFullDto getUserEventById(@PathVariable Long userId,
                                         @PathVariable Long eventId) {

        log.info("Получить событие {} пользователя {}", eventId, userId);
        return eventService.getUserEventById(userId, eventId);
    }

    @PatchMapping("/{eventId}")
    @ResponseStatus(value = HttpStatus.OK)
    public EventFullDto updateEventByUserId(@RequestBody @Validated(Update.class) EventDtoUpdate eventDtoUpdate,
                                            @PathVariable Long userId,
                                            @PathVariable Long eventId) {

        log.info("Обновить событие по id {} пользователя", userId);
        return eventService.updateEventByUserId(eventDtoUpdate, userId, eventId);
    }

    @GetMapping("/{eventId}/requests")
    @ResponseStatus(value = HttpStatus.OK)
    private List<RequestDto> getRequestsForEventIdByUserId(@PathVariable Long userId,
                                                           @PathVariable Long eventId) {

        log.info("Получение запроса на событие {} по id {} пользователя", eventId, userId);
        return eventService.getRequestsForEventIdByUserId(userId, eventId);
    }

    @PatchMapping("/{eventId}/requests")
    @ResponseStatus(value = HttpStatus.OK)
    private RequestUpdateDtoResult updateStatusRequestsForEventIdByUserId(@PathVariable Long userId,
                                                                          @PathVariable Long eventId,
                                                                          @RequestBody RequestUpdateDtoRequest requestDto) {

        log.info("Обновление статуса запроса на событие {} по id {} пользователя", eventId, userId);
        return eventService.updateStatusRequestsForEventIdByUserId(requestDto, userId, eventId);
    }
}
