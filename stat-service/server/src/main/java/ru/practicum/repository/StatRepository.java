package ru.practicum.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.dto.StatResponseDto;
import ru.practicum.model.Stat;

import java.time.LocalDateTime;
import java.util.List;

public interface StatRepository extends JpaRepository<Stat, Long> {

    @Query("SELECT new ru.practicum.dto.StatResponseDto(s.app, s.uri, COUNT(DISTINCT s.ip)) " +
            "FROM Stat AS s " +
            "WHERE s.timestamp BETWEEN ?1 AND ?2 " +
            "GROUP BY s.app, s.uri " +
            "ORDER BY COUNT(DISTINCT s.ip) DESC")
    List<StatResponseDto> findAllByTimestampBetweenStartAndEndWithUniqueIp(LocalDateTime start, LocalDateTime end);

    @Query("SELECT new ru.practicum.dto.StatResponseDto(s.app, s.uri, COUNT(s.ip)) " +
            "FROM Stat AS s " +
            "WHERE s.timestamp BETWEEN ?1 AND ?2 " +
            "GROUP BY s.app, s.uri " +
            "ORDER BY COUNT(s.ip) DESC")
    List<StatResponseDto> findAllByTimestampBetweenStartAndEndWhereIpNotUnique(LocalDateTime start, LocalDateTime end);

    @Query("SELECT new ru.practicum.dto.StatResponseDto(s.app, s.uri, COUNT(DISTINCT s.ip)) " +
            "FROM Stat AS s " +
            "WHERE s.timestamp BETWEEN ?1 AND ?2 AND s.uri IN ?3 " +
            "GROUP BY s.app, s.uri " +
            "ORDER BY COUNT(DISTINCT s.ip) DESC")
    List<StatResponseDto> findAllByTimestampBetweenStartAndEndWithUrisUniqueIp(LocalDateTime start, LocalDateTime end, List<String> uris);

    @Query("SELECT new ru.practicum.dto.StatResponseDto(s.app, s.uri, COUNT(s.ip)) " +
            "FROM Stat AS s " +
            "WHERE s.timestamp BETWEEN ?1 AND ?2 AND s.uri IN ?3 " +
            "GROUP BY s.app, s.uri " +
            "ORDER BY COUNT(s.ip) DESC")
    List<StatResponseDto> findAllByTimestampBetweenStartAndEndWithUrisIpNotUnique(LocalDateTime start, LocalDateTime end, List<String> uris);
}
