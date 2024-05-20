package ru.practicum.comments.service;

import ru.practicum.comments.dto.CommentDto;
import ru.practicum.comments.dto.CommentDtoNew;
import ru.practicum.comments.dto.CommentShortDto;

import java.util.List;

public interface CommentService {

    CommentDto createComment(Long userId, Long eventId, CommentDtoNew commentDtoNew);

    CommentDto updateComment(Long userId, Long commentId, CommentDtoNew commentDtoNew);

    void deleteComment(Long userId, Long commentId);

    List<CommentShortDto> getCommentsByUserId(String rangeStart,
                                              String rangeEnd, Long userId, Integer from, Integer size);

    void deleteAdminComment(Long commentId);

    List<CommentShortDto> getCommentsByEventId(String rangeStart,
                                               String rangeEnd, Long eventId, Integer from, Integer size);
}
