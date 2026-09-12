package ru.practicum.main.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.main.dto.event.EventFullDto;
import ru.practicum.main.dto.event.UpdateEventAdminRequest;
import ru.practicum.main.exception.ConflictException;
import ru.practicum.main.exception.NotFoundException;
import ru.practicum.main.mapper.EventMapper;
import ru.practicum.main.model.Event;
import ru.practicum.main.model.EventState;
import ru.practicum.main.repository.EventRepository;
import ru.practicum.main.util.PageRequestUtil;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional
public class AdminEventServiceImpl implements AdminEventService  {

    private final EventRepository eventRepository;
    private final EventMapper eventMapper;

    @Override
    @Transactional(readOnly = true)
    public List<EventFullDto> getEvents(
            List<Long> users,
            List<String> states,
            List<Long> categories,
            LocalDateTime rangeStart,
            LocalDateTime rangeEnd,
            int from,
            int size
    ) {
        Pageable pageable = PageRequestUtil.of(from, size);

        if (rangeStart == null) {
            rangeStart = LocalDateTime.now().minusYears(100);
        }
        if (rangeEnd == null) {
            rangeEnd = LocalDateTime.now().plusYears(100);
        }

        List<Event> events = eventRepository.findAllAdminEvents(
                users, states, categories, rangeStart, rangeEnd, pageable
        );

        log.info("найдены {} ивенты для админа", events.size());
        return events.stream()
                .map(eventMapper::toFullDto)
                .collect(Collectors.toList());
    }

    @Override
    public EventFullDto updateEvent(Long eventId, UpdateEventAdminRequest updateRequest) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(()-> new NotFoundException("Ивент с ид=" + eventId + " не найден"));

        if (updateRequest.getEventDate() != null) {
            LocalDateTime now = LocalDateTime.now();
            if (updateRequest.getEventDate().isBefore(now.plusHours(1))) {
                throw new ConflictException("Дата ивента долдна быть на час позже текущго помента");
            }
        }
        if (updateRequest.getStateAction() != null) {
            switch (updateRequest.getStateAction()) {
                case PUBLISH_EVENT:
                    if (event.getState() != EventState.PENDING) {
                        throw new ConflictException("Нельзя публиковать ивенты в неверном статусе: " + event.getState());
                    }
                    event.setState(EventState.PUBLISHED );
                    event.setPublishedOn(LocalDateTime.now());
                    break;
                case REJECT_EVENT:
                    if (event.getState() == EventState.PUBLISHED) {
                        throw new ConflictException("Нельзя отказаться от опубликованного ивента");
                    }
                    event.setState(EventState.CANCELED);
                    break;
            }
        }
        eventMapper.updateEntityFromAdminRequest(updateRequest, event);

        event = eventRepository.save(event);
        log.info("Ивент обновлённый админом: {}", event);
        return eventMapper.toFullDto(event);
    }
}






















