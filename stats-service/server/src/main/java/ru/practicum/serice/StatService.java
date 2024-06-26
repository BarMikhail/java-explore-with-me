package ru.practicum.serice;

import ru.practicum.dto.StatDto;
import ru.practicum.dto.StatResponseDto;

import java.time.LocalDateTime;
import java.util.List;

public interface StatService {

    void createStat(StatDto statDto);

    List<StatResponseDto> readStat(LocalDateTime start, LocalDateTime end, List<String> uris, boolean unique);
}
