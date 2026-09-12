package ru.practicum.main.service;


import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.main.dto.event.EventFullDto;
import ru.practicum.main.dto.event.EventShortDto;
import ru.practicum.main.exception.BadRequestException;
import ru.practicum.main.exception.NotFoundException;
import ru.practicum.main.mapper.EventMapper;
import ru.practicum.main.model.Event;
import ru.practicum.main.model.EventState;
import ru.practicum.main.repository.EventRepository;
import ru.practicum.main.stats.client.StatsClient;
import ru.practicum.main.stats.dto.ViewStats;
import ru.practicum.main.util.PageRequestUtil;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PublicEventServiceImpl implements PublicEventService {

    private final EventRepository eventRepository;
    private final EventMapper eventMapper;
    private final StatsClient statsClient;

    @Override
    public List<EventShortDto> getEvents(String text, List<Long> categories, Boolean paid, LocalDateTime rangeStart, LocalDateTime rangeEnd, Boolean onlyAvailable, String sort, int from, int size, HttpServletRequest request) {
        if (rangeStart == null) {
            rangeStart = LocalDateTime.now();
        }
        if (rangeStart != null && rangeEnd != null && rangeStart.isAfter(rangeEnd)) {
            throw new BadRequestException("Range start must be before range end");
        }
        Pageable pageable = PageRequestUtil.of(from, size);

        List<Event> events = eventRepository.findAllPublishedEvents(
                text, categories, paid, rangeStart, rangeEnd, pageable
        );

        statsClient.saveHit(ru.practicum.main.stats.dto.EndpointHit.builder()
                .app("ewm-main-service")
                .uri(request.getRequestURI())
                .ip(request.getRemoteAddr())
                .timestamp(LocalDateTime.now())
                .build());

        List<EventShortDto> eventShortDtos = events.stream()
                .map(event -> {
                    EventShortDto dto = eventMapper.toShortDto(event);
                    return dto;
                })
                .collect(Collectors.toList());

        fillViewsForEvents(eventShortDtos);

        if ("VIEWS".equals(sort)) {
            eventShortDtos.sort(Comparator.comparing(EventShortDto::getViews, Comparator.nullsLast(Comparator.reverseOrder())));
        } else {
            eventShortDtos.sort(Comparator.comparing(EventShortDto::getEventDate).reversed());
        }

        log.info("FOund {} events", eventShortDtos.size());
        return eventShortDtos;
    }

    @Override
    public EventFullDto getEventById(Long id, HttpServletRequest request) {
        Event event = eventRepository.findByIdAndState(id, EventState.PUBLISHED)
                .orElseThrow(() -> new NotFoundException("Event with id=" + id + " was not found"));

        statsClient.saveHit(ru.practicum.main.stats.dto.EndpointHit.builder()
                .app("ewm-main-service")
                .uri(request.getRequestURI())
                .ip(request.getRemoteAddr())
                .timestamp(LocalDateTime.now())
                .build());

        EventFullDto dto = eventMapper.toFullDto(event);

        List<String> uris = List.of("/events/" + id);
        LocalDateTime start = LocalDateTime.now().minusYears(10);
        LocalDateTime end = LocalDateTime.now().plusYears(10);
        var statsResponse = statsClient.getStats(start, end, uris, false);
        if (statsResponse.getBody() != null && statsResponse.getBody().length > 0) {
            dto.setViews(statsResponse.getBody()[0].getHits());
        } else {
            dto.setViews(0L);
        }

        log.info("Returning event: ", dto);
        return dto;
    }

    private void fillViewsForEvents(List<EventShortDto> events) {
        if (events.isEmpty()) {
            return;
        }

        List<String> uris = events.stream()
                .map(e -> "/events/" + e.getId())
                .collect(Collectors.toList());

        LocalDateTime start = LocalDateTime.now().minusYears(10);
        LocalDateTime end = LocalDateTime.now().plusYears(10);

        var statsResponse = statsClient.getStats(start, end, uris, false);
        if (statsResponse.getBody() != null) {
            java.util.Map<String, Long> viewsMap = new java.util.HashMap<>();
            for (ViewStats stat : statsResponse.getBody()) {
                viewsMap.put(stat.getUri(), stat.getHits());
            }

            for (EventShortDto dto : events) {
                String uri = "/events/" + dto.getId();
                dto.setViews(viewsMap.getOrDefault(uri, 0L));
            }
        } else {
            events.forEach(dto -> dto.setViews(0L));
        }
    }
}


















