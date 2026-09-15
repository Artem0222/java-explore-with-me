package ru.practicum.main.service;

import ru.practicum.main.dto.comment.CommentDto;
import ru.practicum.main.dto.comment.NewCommentDto;
import ru.practicum.main.dto.comment.UpdateCommentDto;

import java.util.List;

public interface CommentService {

    CommentDto addComment(Long userId, Long eventId, NewCommentDto newCommentDto);

    CommentDto updateComment(Long userId, Long commentId, UpdateCommentDto updateCommentDto);

    void deleteComment(Long userId, Long commentId);

    List<CommentDto> getEventComments(Long eventId, int from, int size);

    List<CommentDto> getUserComments(Long userId, int from, int size);

    CommentDto hideComment(Long commentId);

    CommentDto showComment(Long commentId);
}
