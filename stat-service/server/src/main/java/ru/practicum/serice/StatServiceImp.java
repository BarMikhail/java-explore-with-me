package ru.practicum.serice;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.dto.StatDto;
import ru.practicum.dto.StatResponseDto;
import ru.practicum.exception.InvalidDataException;
import ru.practicum.mapper.StatMapper;
import ru.practicum.repository.StatRepository;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class StatServiceImp implements StatService {
    private final StatRepository statRepository;

    @Override
    @Transactional
    public void createStat(StatDto statDto) {
        log.info("Сейчас произойдет сохранение статистики");
        statRepository.save(StatMapper.toStat(statDto));
    }

    @Override
    @Transactional(readOnly = true)
    public List<StatResponseDto> readStat(LocalDateTime start, LocalDateTime end, List<String> uris, boolean unique) {
        if (start.isAfter(end)) {
            log.info("Проверка времени");
            throw new InvalidDataException("Начало не может быть позже конца");
        }
        log.info("Просмотр статистики");
        if (uris.isEmpty()) {
            if (unique) {
                log.info("Чтение при пустом uri, уникальный unique");
                return statRepository.findAllByTimestampBetweenStartAndEndWithUniqueIp(start, end);
            } else {
                log.info("Чтение при пустом uri, не уникальный unique");
                return statRepository.findAllByTimestampBetweenStartAndEndWhereIpNotUnique(start, end);
            }
        } else {
            if (unique) {
                log.info("Чтение когда uri не пустой, уникальный unique");
                return statRepository.findAllByTimestampBetweenStartAndEndWithUrisUniqueIp(start, end, uris);
            } else {
                log.info("Чтение когда uri не пустой, не уникальный unique");
                return statRepository.findAllByTimestampBetweenStartAndEndWithUrisIpNotUnique(start, end, uris);
            }
        }
    }

}
