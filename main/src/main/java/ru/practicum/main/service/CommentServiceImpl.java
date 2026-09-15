package ru.practicum.main.service;

import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ru.practicum.main.dto.comment.CommentDto;
import ru.practicum.main.dto.comment.NewCommentDto;
import ru.practicum.main.dto.comment.UpdateCommentDto;
import ru.practicum.main.exception.ConflictException;
import ru.practicum.main.exception.NotFoundException;
import ru.practicum.main.mapper.CommentMapper;
import ru.practicum.main.model.Comment;
import ru.practicum.main.model.Event;
import ru.practicum.main.model.EventState;
import ru.practicum.main.model.User;
import ru.practicum.main.repository.CommentRepository;
import ru.practicum.main.repository.EventRepository;
import ru.practicum.main.repository.UserRepository;
import ru.practicum.main.util.PageRequestUtil;


import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional
public class CommentServiceImpl implements CommentService {

    private final CommentRepository commentRepository;
    private final UserRepository userRepository;
    private final EventRepository eventRepository;
    private final CommentMapper commentMapper;

    @Override
    public CommentDto addComment(Long userId, Long eventId, NewCommentDto newCommentDto) {
        User user = getUserOrThrow(userId);
        Event event = getEventOrThrow(eventId);

        if (event.getState() != EventState.PUBLISHED) {
            throw new ConflictException("Нельзя отсавлять комментарии к неопубликованоому событию");
        }

        Comment comment = commentMapper.toEntity(newCommentDto);
        comment.setAuthor(user);
        comment.setEvent(event);
        comment.setCreated(LocalDateTime.now());
        comment.setHidden(false);

        comment = commentRepository.save(comment);
        log.info("User {} added comment to event {}", userId, eventId);
        return commentMapper.toDto(comment);
    }

    @Override
    public CommentDto updateComment(Long userId, Long commentId, UpdateCommentDto updateCommentDto) {
        Comment comment = getCommentOrThrow(commentId);

        if (!comment.getAuthor().getId().equals(userId)) {
            throw new ConflictException("Можео редактировать только свои комментарии");
        }

        comment.setText(updateCommentDto.getText());
        comment.setEdited(LocalDateTime.now());

        comment = commentRepository.save(comment);
        log.info("User {} updated comment {}", userId, commentId);
        return commentMapper.toDto(comment);
    }

    @Override
    public void deleteComment(Long userId, Long commentID) {
        Comment comment = getCommentOrThrow(commentID);

        if (!comment.getAuthor().getId().equals(userId)) {
            throw new ConflictException("Можно удалять только свои комментарии");
        }

        commentRepository.delete(comment);
        log.info("User {} deleted comment {}", userId, commentID);
    }

    @Override
    @Transactional(readOnly = true)
    public List<CommentDto> getEventComments(Long eventId, int from, int size) {
        getEventOrThrow(eventId);

        Pageable pageable = PageRequestUtil.of(from, size);
        List<Comment> comments = commentRepository.findAllByEventIdAndHiddenFalse(eventId, pageable);

        log.info("FOund {} comments for event", comments.size(), eventId);
        return comments.stream()
                .map(commentMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<CommentDto> getUserComments(Long userId, int from, int size) {
        getUserOrThrow(userId);

        Pageable pageable = PageRequestUtil.of(from, size);
        List<Comment> comments = commentRepository.findAllByAuthorId(userId, pageable);

        log.info("Found {} comments for user {}", comments.size(), userId);
        return comments.stream()
                .map(commentMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public CommentDto hideComment(Long commentId) {
        Comment comment = getCommentOrThrow(commentId);
        comment.setHidden(true);
        comment = commentRepository.save(comment);
        log.info("Comment {} hidden by admin", commentId);
        return commentMapper.toDto(comment);
    }

    @Override
    public CommentDto showComment(Long commentId) {
        Comment comment = getCommentOrThrow(commentId);
        comment.setHidden(false);
        comment = commentRepository.save(comment);
        log.info("Comment {} show by aadmin", commentId);
        return commentMapper.toDto(comment);
    }

    private User getUserOrThrow(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь с ид = " + userId + " не найден"));
    }

    private Event getEventOrThrow(Long eventId) {
        return eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException("Ивент с ид + " + eventId + " не найден"));
    }

    private Comment getCommentOrThrow(Long commentId) {
        return commentRepository.findById(commentId)
                .orElseThrow(() -> new NotFoundException("Комментапрй с ид " + commentId + " не найден"));
    }
}






























