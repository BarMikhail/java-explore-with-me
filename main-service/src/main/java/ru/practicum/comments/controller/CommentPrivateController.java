package ru.practicum.comments.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.additionally.Create;
import ru.practicum.additionally.Update;
import ru.practicum.comments.dto.CommentDto;
import ru.practicum.comments.dto.CommentDtoNew;
import ru.practicum.comments.dto.CommentShortDto;
import ru.practicum.comments.service.CommentService;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/users/{userId}/comments")
public class CommentPrivateController {


    private final CommentService commentService;

    @PostMapping("/{eventId}")
    @ResponseStatus(value = HttpStatus.CREATED)
    public CommentDto createComment(@Validated(Create.class) @RequestBody CommentDtoNew commentDtoNew,
                                 @PathVariable Long userId,
                                 @PathVariable Long eventId) {

        log.info("Создание комментария");
        return commentService.createComment(userId, eventId, commentDtoNew);
    }

    @PatchMapping("/{commentId}")
    @ResponseStatus(value = HttpStatus.OK)
    public CommentDto updateComment(@Validated(Update.class) @RequestBody CommentDtoNew commentDtoNew,
                                        @PathVariable Long userId,
                                        @PathVariable Long commentId) {

        log.info("Обновление комментария");
        return commentService.updateComment(userId, commentId, commentDtoNew);
    }

    @DeleteMapping("/{commentId}")
    @ResponseStatus(value = HttpStatus.NO_CONTENT)
    public void deleteComment(@PathVariable Long userId,
                              @PathVariable Long commentId) {

        log.info("Удаление комментария");
        commentService.deleteComment(userId, commentId);
    }

    @GetMapping
    @ResponseStatus(value = HttpStatus.OK)
    public List<CommentShortDto> getCommentsByUserId(@PathVariable Long userId,
                                                     @RequestParam(required = false) String rangeStart,
                                                     @RequestParam(required = false) String rangeEnd,
                                                     @PositiveOrZero @RequestParam( defaultValue = "0") Integer from,
                                                     @Positive @RequestParam( defaultValue = "10") Integer size) {

        log.info("Вывод комментариев");
        return commentService.getCommentsByUserId(rangeStart, rangeEnd, userId, from, size);
    }
}
