package ru.practicum.main.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.main.dto.event.EventFullDto;
import ru.practicum.main.dto.event.EventShortDto;
import ru.practicum.main.dto.event.NewEventDto;
import ru.practicum.main.dto.event.UpdateEventUserRequest;
import ru.practicum.main.service.PrivateEventService;

import java.util.List;

@RestController
@RequestMapping("/users/{userId}/events")
@Slf4j
@RequiredArgsConstructor
public class PrivateEventController {

    private final PrivateEventService privateEventService;

    @GetMapping
    public List<EventShortDto> getUserEvents(
            @PathVariable Long userId,
            @RequestParam(defaultValue = "0") int from,
            @RequestParam(defaultValue = "10") int size
    ) {
        log.info("GET /users/{}/events: from ={}, size ={}", userId, from, size);
        return privateEventService.getUserEvents(userId, from, size);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public EventFullDto addEvent(
            @PathVariable Long userId,
            @Valid @RequestBody NewEventDto newEventDto
    ) {
        log.info("POST /users/{}/events: {}", userId, newEventDto);
        return privateEventService.addEvent(userId, newEventDto);
    }

    @GetMapping("/{eventId}")
    public EventFullDto getEvent(
            @PathVariable Long userId,
            @PathVariable Long eventId
    ) {
        log.info("GET /users/{}/events/{}", userId, eventId);
        return privateEventService.getEventById(userId, eventId);
    }

    @PatchMapping("/{eventId}")
    public EventFullDto updateEvent(
            @PathVariable Long userId,
            @PathVariable Long eventId,
            @Valid @RequestBody UpdateEventUserRequest updateRequest
    ) {
        log.info("PATCH /users/{}?/events/{}: {}", userId, eventId, updateRequest);
        return privateEventService.updateEvent(userId, eventId, updateRequest);
    }
}
























