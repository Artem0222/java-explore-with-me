package ru.practicum.main.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.practicum.main.model.Event;
import ru.practicum.main.model.EventState;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface EventRepository extends JpaRepository<Event, Long> {

    List<Event> findAllByInitiatorId(Long userId);

    Optional<Event> findByIdAndState(Long id, EventState state);

    @Query("""
            SELECT e FROM Event e
            WHERE e.state = 'PUBLISHED'
            AND (COALESCE(:text, NULL) IS NULL
                OR LOWER(e.annotation) LIKE LOWER(CONCAT('%', :text, '%'))
                OR LOWER(e.description) LIKE LOWER(CONCAT('%', :text, '%')))
            AND (:categories IS NULL OR e.category.id IN :categories)
            AND (:paid IS NULL OR e.paid = :paid)
            AND e.eventDate >= :rangeStart
            AND e.eventDate <= :rangeEnd
            """)
    List<Event> findAllPublishedEvents(
            @Param("text") String text,
            @Param("categories") List<Long> categories,
            @Param("paid") Boolean paid,
            @Param("rangeStart") LocalDateTime rangeStart,
            @Param("rangeEnd") LocalDateTime rangeEnd,
            Pageable pageable
    );
}