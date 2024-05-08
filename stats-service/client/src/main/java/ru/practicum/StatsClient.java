package ru.practicum;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.util.DefaultUriBuilderFactory;
import ru.practicum.dto.StatDto;

import java.time.LocalDateTime;
import java.util.Map;

@Service
public class StatsClient extends BaseClient {

    @Autowired
//    public StatsClient(@Value("http://localhost:9090") String serverUrl, RestTemplateBuilder builder) {
//    public StatsClient(@Value("http://stats-server:9090") String serverUrl, RestTemplateBuilder builder) {
    public StatsClient(@Value("${stats-server.url}") String serverUrl, RestTemplateBuilder builder) {
        super(
                builder
                        .uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl))
                        .requestFactory(HttpComponentsClientHttpRequestFactory::new)
                        .build()
        );
    }

    public ResponseEntity<Object> createStat(StatDto statDto) {
        return post("/hit", statDto);
    }

    public ResponseEntity<Object> readStat(LocalDateTime start, LocalDateTime end, String uris, boolean unique) {
        Map<String, Object> parameters;
        if (uris == null) {
            parameters = Map.of("start", start,
                    "end", end,
                    "unique", unique);
            return get("/stats?start={start}&end={end}&unique={unique}", parameters);
        }
        parameters = Map.of("start", start,
                "end", end,
                "uris", String.join(",", uris),
                "unique", unique);
        return get("/stats?start={start}&end={end}&unique={unique}&uris={uris}", parameters);
    }
}
