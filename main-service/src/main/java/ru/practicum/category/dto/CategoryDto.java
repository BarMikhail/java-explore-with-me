package ru.practicum.category.dto;

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
public class CategoryDto {

    private Long id;

    @Size(max = 50, groups = {Create.class, Update.class})
    @NotBlank(groups = {Create.class, Update.class})
    private String name;
}
