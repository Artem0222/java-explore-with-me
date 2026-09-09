package ru.practicum.main.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.main.dto.event.EventFullDto;
import ru.practicum.main.dto.event.EventShortDto;
import ru.practicum.main.dto.event.NewEventDto;
import ru.practicum.main.dto.event.UpdateEventUserRequest;
import ru.practicum.main.exception.BadRequestException;
import ru.practicum.main.exception.ConflictException;
import ru.practicum.main.exception.NotFoundException;
import ru.practicum.main.mapper.EventMapper;
import ru.practicum.main.model.*;
import ru.practicum.main.repository.CategoryRepository;
import ru.practicum.main.repository.EventRepository;
import ru.practicum.main.repository.LocationRepository;
import ru.practicum.main.repository.UserRepository;
import ru.practicum.main.util.PageRequestUtil;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional
public class PrivateEventServiceImpl implements PrivateEventService {

    private final EventRepository eventRepository;
    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;
    private final LocationRepository locationRepository;
    private final EventMapper eventMapper;

    @Override
    @Transactional(readOnly = true)
    public List<EventShortDto> getUserEvents(Long userId, int from, int size) {
        User user = getUserOrThrow(userId);

        Pageable pageable = PageRequestUtil.of(from, size);
        List<Event> events = eventRepository.findAllByInitiatorId(userId, pageable).getContent();

        log.info("Найдены {} ивенты для пользователя {}", events.size(), userId);
        return events.stream()
                .map(eventMapper::toShortDto)
                .collect(Collectors.toList());
    }

    @Override
    public EventFullDto addEvent(Long userId, NewEventDto newEventDto) {
        User user = getUserOrThrow(userId);

        if (newEventDto.getEventDate().isBefore(LocalDateTime.now().plusHours(2))) {
            throw new BadRequestException("Дата события должна быть не ранее чем через 2 часа от текущего момента");
        }

        Category category = categoryRepository.findById(newEventDto.getCategory())
                .orElseThrow(() -> new NotFoundException("Категория с ид= " + newEventDto.getCategory() + " не найдена"));

        Location location = Location.builder()
                .lat(newEventDto.getLocation().getLat())
                .lon(newEventDto.getLocation().getLon())
                .build();
        location = locationRepository.save(location);

        Event event = eventMapper.toEntity(newEventDto, userId, newEventDto.getCategory());
        event.setLocation(location);

        if (event.getPaid() == null) {
            event.setPaid(false);
        }
        if (event.getParticipantLimit() == null) {
            event.setParticipantLimit(0);
        }
        if (event.getRequestModeration() == null) {
            event.setRequestModeration(true);
        }
        event.setState(EventState.PENDING);
        event.setCreatedOn(LocalDateTime.now());
        event.setConfirmedRequests(0L);
        event.setViews(0L);

        event = eventRepository.save(event);
        log.info("User {} added event: {}", userId, event);
        return eventMapper.toFullDto(event);
    }

    @Override
    @Transactional(readOnly = true)
    public EventFullDto getEventById(Long userId, Long eventId) {
        User user = getUserOrThrow(userId);

        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException("Ивент с ид = " + eventId + " не найден"));

        if (!event.getInitiator().getId().equals(userId)) {
            throw new NotFoundException("Мероприятие с ид=" + eventId + " не нпайдено для пользователя " + userId);
        }

        log.info("User {} requested event: {}", userId, event);
        return eventMapper.toFullDto(event);
    }

    @Override
    public EventFullDto updateEvent(Long userId, Long eventId, UpdateEventUserRequest updateRequest) {
        User user = getUserOrThrow(userId);

        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException("Ивент с ид=" + eventId + " не найден"));

        if (!event.getInitiator().getId().equals(userId)) {
            throw new NotFoundException("Ивент с ид=" + eventId + " не найден для пользоавтеля " + userId);
        }

        if (event.getState() == EventState.PUBLISHED) {
            throw new ConflictException("Только ивенты в ожидании или отменённые могут быть изменены");
        }
        if (updateRequest.getEventDate() != null) {
            if (updateRequest.getEventDate().isBefore(LocalDateTime.now().plusHours(2))) {
                throw new BadRequestException("Дата события должна быть не ранее чем через 2 часа от текущего момента");

            }
        }

        if (updateRequest.getStateAction() != null) {
            switch (updateRequest.getStateAction()) {
                case SEND_TO_REVIEW:
                    event.setState(EventState.PENDING);
                    break;
                case CANCEL_REVIEW:
                    event.setState(EventState.CANCELED);
                    break;
            }
        }

        eventMapper.updateEntityFromUserRequest(updateRequest, event);

        if (updateRequest.getCategory() != null) {
            Category category = categoryRepository.findById(updateRequest.getCategory())
                    .orElseThrow(() -> new NotFoundException("Категория с ид=" + updateRequest.getCategory() + " не найдена"));
            event.setCategory(category);
        }

        if (updateRequest.getLocation() != null) {
            Location location = event.getLocation();
            if (location == null) {
                location = new Location();
            }
            location.setLat(updateRequest.getLocation().getLat());
            location.setLon(updateRequest.getLocation().getLon());
            location = locationRepository.save(location);
            event.setLocation(location);
        }

        event = eventRepository.save(event);
        log.info("User {} updated event: {}", userId, event);
        return eventMapper.toFullDto(event);

    }

    private User getUserOrThrow(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь с ид" + userId + " не найден"));
    }
}



























