package ru.practicum.event.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.additionally.Constants;
import ru.practicum.additionally.Update;
import ru.practicum.event.model.StateAction;
import ru.practicum.location.dto.LocationDto;

import javax.validation.constraints.FutureOrPresent;
import javax.validation.constraints.PositiveOrZero;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class EventDtoUpdate {
    @Size(max = 2000, groups = Update.class)
    private String annotation;

    @Size(max = 120, groups = Update.class)
    private String title;

    @Size(max = 7000, groups = Update.class)
    private String description;

    private Boolean paid;

    private Boolean requestModeration;

    @PositiveOrZero(groups = Update.class)
    private Long participantLimit;

    @FutureOrPresent(groups = Update.class)
    @JsonFormat(pattern = Constants.DATE_FORMAT, shape = JsonFormat.Shape.STRING)
    private LocalDateTime eventDate;

    private Long category;

    private LocationDto location;

    private StateAction stateAction;
}
