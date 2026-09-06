package ru.practicum.main.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.main.model.Event;

import java.util.List;

public interface EventRepository extends JpaRepository<Event, Long> {
    List<Event> findAllByInitiatorId(Long userId);
}