package ru.practicum.comments.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.additionally.Create;
import ru.practicum.additionally.Update;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CommentDtoNew {

    @NotBlank(groups = Create.class)
    @Size(max = 500, groups = {Create.class, Update.class})
    private String message;
}
