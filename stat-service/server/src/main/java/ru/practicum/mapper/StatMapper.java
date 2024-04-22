package ru.practicum.mapper;

import lombok.experimental.UtilityClass;
import ru.practicum.dto.StatDto;
import ru.practicum.model.Stat;

@UtilityClass
public class StatMapper {
    public static Stat toStat(StatDto statDto) {
        return Stat.builder()
                .ip(statDto.getIp())
                .uri(statDto.getUri())
                .app(statDto.getApp())
                .timestamp(statDto.getTimestamp())
                .build();
    }

    public static StatDto toStatDto(Stat stat) {
        return StatDto.builder()
                .ip(stat.getIp())
                .app(stat.getApp())
                .uri(stat.getUri())
                .timestamp(stat.getTimestamp())
                .build();
    }

}
