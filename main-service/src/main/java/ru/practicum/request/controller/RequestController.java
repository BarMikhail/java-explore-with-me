package ru.practicum.request.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.request.dto.RequestDto;
import ru.practicum.request.service.RequestService;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/users/{userId}/requests")
public class RequestController {
    private final RequestService requestService;

    @PostMapping
    @ResponseStatus(value = HttpStatus.CREATED)
    public RequestDto createRequest(@PathVariable Long userId,
                                    @RequestParam Long eventId) {
        log.info("Юзер {} создает запрос на событие {}", userId, eventId);
        return requestService.createRequest(userId, eventId);
    }

    @GetMapping
    @ResponseStatus(value = HttpStatus.OK)
    public List<RequestDto> getRequestsByUserId(@PathVariable Long userId) {
        log.info("Все события этого юзера {}", userId);
        return requestService.getRequestsByUserId(userId);
    }

    @PatchMapping("/{requestId}/cancel")
    @ResponseStatus(value = HttpStatus.OK)
    public RequestDto cancelRequest(@PathVariable Long userId,
                                    @PathVariable Long requestId) {
        log.info("Пользователь {} отменил запрос {}", userId, requestId);
        return requestService.cancelRequest(userId, requestId);
    }
}
