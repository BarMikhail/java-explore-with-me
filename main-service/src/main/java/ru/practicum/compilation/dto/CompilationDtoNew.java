package ru.practicum.compilation.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.additionally.Create;
import ru.practicum.additionally.Update;
import ru.practicum.event.dto.EventDto;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.util.Set;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CompilationDtoNew {

    private Boolean pinned;

    @NotBlank(groups = Create.class)
    @Size(max = 50, groups = {Create.class, Update.class})
    private String title;

    private Set<EventDto> events;
}
