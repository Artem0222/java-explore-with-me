package ru.practicum.main.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.main.dto.comment.CommentDto;
import ru.practicum.main.dto.comment.NewCommentDto;
import ru.practicum.main.dto.comment.UpdateCommentDto;
import ru.practicum.main.service.CommentService;

import java.util.List;

@RestController
@Slf4j
@RequiredArgsConstructor
@Validated
public class CommentController {

    private final CommentService commentService;

    @PostMapping("/users/{userId}/comments")
    @ResponseStatus(HttpStatus.CREATED)
    public CommentDto addComment(
            @PathVariable Long userId,
            @RequestParam Long eventId,
            @Valid @RequestBody NewCommentDto newCommentDto
    ) {
        log.info("POST /users/{}/comments?eventId={}", userId, eventId);
        return commentService.addComment(userId, eventId, newCommentDto);
    }

    @PatchMapping("/users/{userId}/comments/{commentId}")
    public CommentDto updateComment(
            @PathVariable Long userId,
            @PathVariable Long commentId,
            @Valid @RequestBody UpdateCommentDto updateCommentDto
    ) {
        log.info("PATCH /users/{}/comment{}", userId, commentId);
        return commentService.updateComment(userId, commentId, updateCommentDto);
    }

    @DeleteMapping("/users/{userId}/comments/{commentId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteComment(
            @PathVariable Long userId,
            @PathVariable Long commentId
    ) {
        log.info("DELETE /users/{}/comments/{}", userId, commentId);
        commentService.deleteComment(userId, commentId);
    }

    @GetMapping("/users/{userId}/comments")
    public List<CommentDto> getUserComments(
            @PathVariable Long userId,
            @RequestParam(defaultValue = "0") int from,
            @RequestParam(defaultValue = "10") int size
    ) {
        log.info("GET /users/{}/comments", userId);
        return commentService.getUserComments(userId, from, size);
    }

    @GetMapping("/events/{eventId}/comments")
    public List<CommentDto> getEventComments(
            @PathVariable Long eventId,
            @RequestParam(defaultValue = "0") int from,
            @RequestParam(defaultValue = "10") int size
    ) {
        log.info("GET /events/{}/comments", eventId);
        return commentService.getEventComments(eventId, from, size);
    }

    @PatchMapping("/admin/comments/{commentId}/hide")
    public CommentDto hideComment(@PathVariable Long commentId) {
        log.info("PATCH /admin/comments/{}/hide", commentId);
        return commentService.hideComment(commentId);
    }

    @PatchMapping("/admin/comments/{commentId}/show")
    public CommentDto showComment(@PathVariable Long commentId) {
        log.info("PATCH /admin/comments/{}/show", commentId);
        return commentService.showComment(commentId);
    }
}




















