package ru.practicum.comments.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.additionally.Constants;
import ru.practicum.comments.dto.CommentDto;
import ru.practicum.comments.dto.CommentDtoNew;
import ru.practicum.comments.dto.CommentShortDto;
import ru.practicum.comments.mapper.CommentMapper;
import ru.practicum.comments.model.Comment;
import ru.practicum.comments.repository.CommentRepository;
import ru.practicum.event.model.Event;
import ru.practicum.event.repository.EventRepository;
import ru.practicum.exception.ConflictException;
import ru.practicum.exception.NotFoundException;
import ru.practicum.exception.ValidationException;
import ru.practicum.user.model.User;
import ru.practicum.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@AllArgsConstructor
@Transactional(readOnly = true)
public class CommentServiceImp implements CommentService {

    private final CommentRepository commentRepository;

    private final UserRepository userRepository;

    private final EventRepository eventRepository;

    @Override
    @Transactional
    public CommentDto createComment(Long userId, Long eventId, CommentDtoNew commentDtoNew) {
        User user = checkUser(userId);
        Event event = checkEvent(eventId);

        Comment comment = CommentMapper.toComment(commentDtoNew, user, event);
        comment = commentRepository.save(comment);

        return CommentMapper.toCommentDto(comment);
    }

    @Override
    @Transactional
    public CommentDto updateComment(Long userId, Long commentId, CommentDtoNew commentDtoNew) {
        Comment comment = checkComment(commentId);

        if (!userId.equals(comment.getUser().getId())) {
            throw new ConflictException("Обновлять комментарии может только автор");
        }
        comment.setMessage(commentDtoNew.getMessage());
        comment = commentRepository.save(comment);

        return CommentMapper.toCommentDto(comment);
    }

    @Override
    @Transactional
    public void deleteComment(Long userId, Long commentId) {
        checkUser(userId);
        Comment comment = checkComment(commentId);

        if (!userId.equals(comment.getUser().getId())) {
            throw new ConflictException("Обновлять комментарии может только автор");
        }

        commentRepository.deleteById(commentId);
    }

    @Override
    public List<CommentShortDto> getCommentsByUserId(String rangeStart, String rangeEnd,
                                                     Long userId, Integer from, Integer size) {
        checkUser(userId);
        PageRequest pageRequest = PageRequest.of(from / size, size);

        LocalDateTime startTime = parseDate(rangeStart);
        LocalDateTime endTime = parseDate(rangeEnd);

        if (startTime != null && endTime != null) {
            if (startTime.isAfter(endTime)) {
                throw new ValidationException("Стар не может быть позже конца");
            }
        }
        List<Comment> commentList = commentRepository.getCommentsByUserId(userId, startTime,
                endTime, pageRequest);
        return CommentMapper.toCommentShortDtoList(commentList);
    }

    @Override
    @Transactional
    public void deleteAdminComment(Long commentId) {
        checkComment(commentId);
        commentRepository.deleteById(commentId);
    }

    @Override
    public List<CommentShortDto> getCommentsByEventId(String rangeStart, String rangeEnd,
                                                      Long eventId, Integer from, Integer size) {
        checkEvent(eventId);

        PageRequest pageRequest = PageRequest.of(from / size, size);
        LocalDateTime startTime = parseDate(rangeStart);
        LocalDateTime endTime = parseDate(rangeEnd);

        if (startTime != null && endTime != null) {
            if (startTime.isAfter(endTime)) {
                throw new ValidationException("Стар не может быть позже конца");
            }
        }

        List<Comment> commentList = commentRepository.getCommentsByEventId(eventId,
                startTime, endTime, pageRequest);
        return CommentMapper.toCommentShortDtoList(commentList);
    }


    private User checkUser(Long userId) {
        return userRepository.findById(userId).orElseThrow(() -> new NotFoundException("Нет такого юзера"));
    }

    private Comment checkComment(Long categoryId) {
        return commentRepository.findById(categoryId).orElseThrow(() -> new NotFoundException("Нет такого комментария"));
    }

    private Event checkEvent(Long eventId) {
        return eventRepository.findById(eventId).orElseThrow(() -> new NotFoundException("Нет такого события"));
    }

    private LocalDateTime parseDate(String date) {
        if (date != null) {
            return LocalDateTime.parse(date, Constants.FORMATTER);
        } else {
            return null;
        }
    }
}
