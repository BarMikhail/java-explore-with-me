package ru.practicum.event.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.event.dto.EventFullDto;
import ru.practicum.event.dto.EventShortDto;
import ru.practicum.event.service.EventService;

import javax.servlet.http.HttpServletRequest;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/events")
public class EventPublicController {

    public final EventService eventService;

    @GetMapping
    @ResponseStatus(value = HttpStatus.OK)
    public List<EventShortDto> getEventsByPublic(@RequestParam(required = false) String text,
                                                 @RequestParam(required = false) List<Long> categories,
                                                 @RequestParam(defaultValue = "false") Boolean paid,
                                                 @RequestParam(required = false) String rangeStart,
                                                 @RequestParam(required = false) String rangeEnd,
                                                 @RequestParam(defaultValue = "false") Boolean onlyAvailable,
                                                 @RequestParam(required = false) String sort,
                                                 @PositiveOrZero @RequestParam(defaultValue = "0") Integer from,
                                                 @Positive @RequestParam(defaultValue = "10") Integer size,
                                                 HttpServletRequest request) {

        String uri = request.getRequestURI();
        String ip = request.getRemoteAddr();

        log.info("Получение всех событий");
        return eventService.getEventsByPublic(text, categories, paid, rangeStart, rangeEnd, onlyAvailable, sort, from, size, uri, ip);
    }

    @GetMapping("/{id}")
    @ResponseStatus(value = HttpStatus.OK)
    public EventFullDto getEventById(@PathVariable Long id, HttpServletRequest request) {

        String uri = request.getRequestURI();
        String ip = request.getRemoteAddr();

        log.info("Получение определенного события по id {}", id);
        return eventService.getEventById(id, uri, ip);
    }
}