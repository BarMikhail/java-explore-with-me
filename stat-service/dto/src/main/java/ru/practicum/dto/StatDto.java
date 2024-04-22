package ru.practicum.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.additionally.Create;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class StatDto {

    @NotBlank(groups = Create.class)
    @Size(max = 50, groups = Create.class)
    private String app;

    @NotBlank(groups = Create.class)
    @Size(max = 50, groups = Create.class)
    private String uri;

    @NotBlank(groups = Create.class)
    @Size(max = 15, groups = Create.class)
    private String ip;

    @NotNull(groups = Create.class)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", shape = JsonFormat.Shape.STRING)
    private LocalDateTime timestamp;
}
