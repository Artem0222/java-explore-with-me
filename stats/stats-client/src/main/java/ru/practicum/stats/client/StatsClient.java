package ru.practicum.stats.client;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;
import ru.practicum.stats.dto.EndpointHit;
import ru.practicum.stats.dto.ViewStats;

import java.net.URI;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Slf4j
@Component
public class StatsClient {

    private final String serverUrl;
    private final RestTemplate restTemplate;
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");


    public StatsClient(
            @Value("${stats-server.url:http://localhost:9090}") String serverUrl,
            RestTemplate restTemplate
    ) {
        this.serverUrl = serverUrl.replaceAll("/+$", "");
        this.restTemplate = restTemplate;
    }

    public ResponseEntity<Void> saveHit(EndpointHit hit) {
        log.info("Saving hit for uri: {}", hit.getUri());
        return restTemplate.postForEntity(serverUrl + "/hit", hit, Void.class);
    }

    public ResponseEntity<ViewStats[]> getStats(LocalDateTime start, LocalDateTime end) {
        return getStats(start, end, List.of(), false);
    }

    public ResponseEntity<ViewStats[]> getStats(LocalDateTime start, LocalDateTime end, List<String> uris) {
        return getStats(start, end, uris, false);
    }

    public ResponseEntity<ViewStats[]> getStats(LocalDateTime start, LocalDateTime end, List<String> uris, boolean unique) {
        log.info("Requested stats: start={}, end={}, uris={}, unique={}", start, end, uris, unique);

        UriComponentsBuilder builder = UriComponentsBuilder.fromUriString(serverUrl + "/stats")
                .queryParam("start", start.format(formatter))
                .queryParam("end", end.format(formatter))
                .queryParam("unique", unique);

        if (uris != null && !uris.isEmpty()) {
            uris.forEach(uri -> builder.queryParam("uris", uri));
        }

        URI requestUri = builder.build().encode().toUri();
        return restTemplate.getForEntity(requestUri, ViewStats[].class);
    }
}