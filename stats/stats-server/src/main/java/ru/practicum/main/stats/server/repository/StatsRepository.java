package ru.practicum.main.stats.server.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.practicum.main.stats.dto.ViewStats;
import ru.practicum.main.stats.server.model.Hit;

import java.time.LocalDateTime;
import java.util.List;

public interface StatsRepository extends JpaRepository<Hit, Long> {

    @Query(nativeQuery = true, value = """
            SELECT h.app AS app, h.uri AS uri, COUNT(h.ip) AS hits
            FROM hits h
            WHERE h.timestamp BETWEEN :start AND :end
            AND (:uris IS NULL OR h.uri IN (:uris))
            GROUP BY h.app, h.uri
            ORDER BY COUNT(h.ip) DESC
            """)
    List<ViewStats> findStatsWithoutUniqueIp(
            @Param("start") LocalDateTime start,
            @Param("end") LocalDateTime end,
            @Param("uris") List<String> uris
    );

    @Query(nativeQuery = true, value = """
            SELECT h.app AS app, h.uri AS uri, COUNT(DISTINCT h.ip) AS hits
            FROM hits h
            WHERE h.timestamp BETWEEN :start AND :end
            AND (:uris IS NULL OR h.uri IN (:uris))
            GROUP BY h.app, h.uri
            ORDER BY COUNT(DISTINCT h.ip) DESC
            """)
    List<ViewStats> findStatsWithUniqueIp(
            @Param("start") LocalDateTime start,
            @Param("end") LocalDateTime end,
            @Param("uris") List<String> uris
    );
}