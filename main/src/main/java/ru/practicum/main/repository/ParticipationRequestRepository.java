package ru.practicum.main.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.main.model.ParticipationRequest;
import ru.practicum.main.model.RequestStatus;

import java.util.List;
import java.util.Optional;

public interface ParticipationRequestRepository extends JpaRepository<ParticipationRequest, Long> {
    List<ParticipationRequest> findAllByRequesterId(Long userId);

    List<ParticipationRequest> findAllByEventId(Long eventId);

    Optional<ParticipationRequest> findByEventIdAndRequesterId(Long eventId, Long userId);

    Long countByEventIdAndStatus(Long eventId, RequestStatus status);
}