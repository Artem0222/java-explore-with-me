package ru.practicum.main.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.main.model.Comment;

import java.util.List;


public interface CommentRepository extends JpaRepository<Comment, Long> {
    List<Comment> findAllByEventIdAndHiddenFalse(Long eventId, Pageable pageable);

    List<Comment> findAllByAuthorId(Long authorId, Pageable pageable);

    List<Comment> findAllByEventId(Long eventId, Pageable pageable);
}
