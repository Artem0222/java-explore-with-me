package ru.practicum.main.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.main.dto.request.EventRequestStatusUpdateRequest;
import ru.practicum.main.dto.request.EventRequestStatusUpdateResult;
import ru.practicum.main.dto.request.ParticipationRequestDto;
import ru.practicum.main.exception.ConflictException;
import ru.practicum.main.exception.NotFoundException;
import ru.practicum.main.mapper.RequestMapper;
import ru.practicum.main.model.*;
import ru.practicum.main.repository.EventRepository;
import ru.practicum.main.repository.ParticipationRequestRepository;
import ru.practicum.main.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional
public class PrivateRequestServiceImpl implements PrivateRequestService {

    private final ParticipationRequestRepository requestRepository;
    private final EventRepository eventRepository;
    private final UserRepository userRepository;
    private final RequestMapper requestMapper;

    @Override
    public ParticipationRequestDto addRequest(Long userId, Long eventId) {
        User user = getUserOrThrow(userId);
        Event event = getEventOrThrow(eventId);

        if (event.getInitiator().getId().equals(userId)) {
            throw new ConflictException("Вы не можете учавстсововать в своём собственном ивенте");
        }

        if (event.getState() != EventState.PUBLISHED) {
            throw new ConflictException("Вы не можете учавствовать в неопубликованном ивенте");
        }

        if (requestRepository.findByEventIdAndRequesterId(eventId, userId).isPresent()) {
            throw new ConflictException("Вы уже подтвкрлдили участие в этом ивенте");
        }

        Long confirmedRequests = requestRepository.countByEventIdAndStatus(eventId, RequestStatus.CONFIRMED);
        if (event.getParticipantLimit() > 0 && confirmedRequests >= event.getParticipantLimit()) {
            throw new ConflictException("Достигнут лимит участников");
        }

        ParticipationRequest request = ParticipationRequest.builder()
                .created(LocalDateTime.now())
                .event(event)
                .requester(user)
                .build();

        if (!event.getRequestModeration() || event.getParticipantLimit() == 0) {
            request.setStatus(RequestStatus.CONFIRMED);
            event.setConfirmedRequests(confirmedRequests + 1);
            eventRepository.save(event);
        } else {
            request.setStatus(RequestStatus.PENDING);
        }

        request = requestRepository.save(request);
        log.info("User {} added request to event {}", userId, eventId);
        return requestMapper.toDto(request);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ParticipationRequestDto> getUserRequests(Long userId) {
        getUserOrThrow(userId);

        List<ParticipationRequest> requests = requestRepository.findAllByRequesterId(userId);
        log.info("Найдено {} запросов для польозвателья {}", requests.size(), userId);
        return requests.stream()
                .map(requestMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public ParticipationRequestDto cancelRequest(Long userId, Long requestId) {
        getUserOrThrow(userId);

        ParticipationRequest request = requestRepository.findById(requestId)
                .orElseThrow(() -> new NotFoundException("Зарос с ид=" + requestId + " не найден"));

        if (!request.getRequester().getId().equals(userId)) {
            throw new NotFoundException("Запрос с ид=" + requestId + " не найден для пользователя " + userId);
        }

        request.setStatus(RequestStatus.CANCELED);
        request = requestRepository.save(request);
        log.info("User {} canceled request {}", userId, requestId);
        return requestMapper.toDto(request);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ParticipationRequestDto> getEventRequests(Long userId, Long eventId) {
        getUserOrThrow(userId);
        Event event = getEventOrThrow(eventId);

        if (!event.getInitiator().getId().equals(userId)) {
            throw new NotFoundException("Ивент с ид = " + eventId + " не найдено для пользователя " + userId);
        }

        List<ParticipationRequest> requests = requestRepository.findAllByEventId(eventId);
        log.info("FOund {} requests for event {}", requests.size(), eventId);
        return requests.stream()
                .map(requestMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public EventRequestStatusUpdateResult updateRequestStatus(
            Long userId,
            Long eventId,
            EventRequestStatusUpdateRequest updateRequest
    ) {
        getUserOrThrow(userId);
        Event event = getEventOrThrow(eventId);

        if (!event.getInitiator().getId().equals(userId)) {
            throw new NotFoundException("Событие с ид=" + eventId + " не найдено для пользователя " + userId);
        }

        Long confirmedRequests = requestRepository.countByEventIdAndStatus(eventId, RequestStatus.CONFIRMED);
        if (event.getParticipantLimit() > 0 && confirmedRequests >= event.getParticipantLimit()) {
            throw new ConflictException("Достигнут лимит участников");
        }

        List<ParticipationRequest> requests = requestRepository.findAllById(updateRequest.getRequestIds());
        List<ParticipationRequestDto> confirmedList = new ArrayList<>();
        List<ParticipationRequestDto> rejectedList = new ArrayList<>();

        for (ParticipationRequest request : requests) {
            if (request.getStatus() != RequestStatus.PENDING) {
                throw new ConflictException("Запрос должен быть в статусе ОЖИДАНИЕ");
            }

            if (updateRequest.getStatus() == RequestStatus.CONFIRMED) {
                if (event.getParticipantLimit() > 0 && confirmedRequests >= event.getParticipantLimit()) {
                    request.setStatus(RequestStatus.REJECTED);
                    rejectedList.add(requestMapper.toDto(request));
                    continue;
                }

                request.setStatus(RequestStatus.CONFIRMED);
                confirmedRequests++;
                confirmedList.add(requestMapper.toDto(request));
            } else {
                request.setStatus(RequestStatus.REJECTED);
                rejectedList.add(requestMapper.toDto(request));
            }
        }
        event.setConfirmedRequests(confirmedRequests);
        eventRepository.save(event);

        requestRepository.saveAll(requests);
        log.info("User {} updated requests statuses for event {}", userId, eventId);

        return EventRequestStatusUpdateResult.builder()
                .confirmedRequests(confirmedList)
                .rejectedRequests(rejectedList)
                .build();
    }

    private User getUserOrThrow(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь с ид = " + userId + " не найден"));
    }

    private Event getEventOrThrow(Long eventId) {
        return eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException("Ивент с ид = " + eventId + " не найлден"));
    }

}























