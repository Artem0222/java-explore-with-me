package ru.practicum.stats.server.mapper;

import ru.practicum.stats.dto.EndpointHit;
import ru.practicum.stats.server.model.Hit;

public class HitMapper {

    public static Hit toEntity(EndpointHit endpointHit) {
        return Hit.builder()
                .app(endpointHit.getApp())
                .uri(endpointHit.getUri())
                .ip(endpointHit.getIp())
                .timestamp(endpointHit.getTimestamp())
                .build();
    }

    public static EndpointHit toDto(Hit hit) {
        return EndpointHit.builder()
                .id(hit.getId())
                .app(hit.getApp())
                .uri(hit.getUri())
                .ip(hit.getIp())
                .timestamp(hit.getTimestamp())
                .build();
    }
}