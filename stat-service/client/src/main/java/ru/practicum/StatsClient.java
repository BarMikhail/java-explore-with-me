package ru.practicum;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.util.DefaultUriBuilderFactory;
import ru.practicum.dto.StatDto;

import java.util.List;
import java.util.Map;

@Service
public class StatsClient extends BaseClient {

    @Autowired
    public StatsClient(@Value("${stats-service.url}") String serverUrl, RestTemplateBuilder builder) {
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

    public ResponseEntity<Object> readStat(String start, String end, List<String> uris, boolean unique) {
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