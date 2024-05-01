package ru.practicum.user.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.additionally.Create;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserDto {
    private Long id;

    @Size(max = 250, groups = Create.class)
    @NotBlank(groups = Create.class)
    private String name;

    @Email(groups = Create.class)
    @Size(max = 250, groups = Create.class)
    @NotBlank(groups = Create.class)
    private String email;
}
