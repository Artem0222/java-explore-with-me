package ru.practicum.main.stats.server.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.main.stats.dto.EndpointHit;
import ru.practicum.main.stats.dto.ViewStats;
import ru.practicum.main.stats.server.mapper.HitMapper;
import ru.practicum.main.stats.server.model.Hit;
import ru.practicum.main.stats.server.repository.StatsRepository;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class StatsServiceImpl implements StatsService {

    private final StatsRepository statsRepository;

    @Override
    @Transactional
    public void saveHit(EndpointHit endpointHit) {
        Hit hit = HitMapper.toEntity(endpointHit);
        statsRepository.save(hit);
        log.info("Saved hit: app={}, uri={}, ip={}", hit.getApp(), hit.getUri(), hit.getIp());
    }

    @Override
    public List<ViewStats> getStats(LocalDateTime start, LocalDateTime end, List<String> uris, Boolean unique) {
        log.info("Get stats request: start={}, end={}, uris={}, unique={}", start, end, uris, unique);

        if (start != null && end != null && start.isAfter(end)) {
            throw new IllegalArgumentException("Start date cannot be after end date");
        }

        if (uris != null && uris.isEmpty()) {
            uris = null;
        }

        if (Boolean.TRUE.equals(unique)) {
            return statsRepository.findStatsWithUniqueIp(start, end, uris);
        } else {
            return statsRepository.findStatsWithoutUniqueIp(start, end, uris);
        }
    }
}
