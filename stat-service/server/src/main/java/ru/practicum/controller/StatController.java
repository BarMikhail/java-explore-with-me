package ru.practicum.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.additionally.Constants;
import ru.practicum.additionally.Create;
import ru.practicum.dto.StatDto;
import ru.practicum.dto.StatResponseDto;
import ru.practicum.serice.StatService;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequiredArgsConstructor
@Validated
@Slf4j
public class StatController {

    private final StatService statService;

    @PostMapping("/hit")
    @ResponseStatus(HttpStatus.CREATED)
    public void createStat(@RequestBody @Validated(Create.class) StatDto statDto) {
        statService.createStat(statDto);
    }

    @GetMapping("/stats")
    public List<StatResponseDto> readStat(@RequestParam @DateTimeFormat(pattern = Constants.DATE_FORMAT) LocalDateTime start,
                                          @RequestParam @DateTimeFormat(pattern = Constants.DATE_FORMAT) LocalDateTime end,
                                          @RequestParam(defaultValue = "") List<String> uris,
                                          @RequestParam(defaultValue = "false") Boolean unique) {
        return statService.readStat(start, end, uris, unique);
    }

}
