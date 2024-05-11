package ru.practicum.event.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.additionally.Constants;
import ru.practicum.location.dto.LocationDto;

import javax.validation.constraints.*;
import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class EventDtoNew {

    @NotBlank
    @Size(min = 20, max = 2000)
    private String annotation;

    @NotBlank
    @Size(min = 3, max = 120)
    private String title;

    @NotBlank
    @Size(min = 20, max = 7000)
    private String description;

    @NotNull
    private Boolean paid = false;

    private Boolean requestModeration = true;

    @PositiveOrZero
    private Long participantLimit = 0L;

    @NotNull
    @FutureOrPresent
    @JsonFormat(pattern = Constants.DATE_FORMAT, shape = JsonFormat.Shape.STRING)
    private LocalDateTime eventDate;

    @NotNull
    @Positive
    private Long category;

    @NotNull
    private LocationDto location;
}
