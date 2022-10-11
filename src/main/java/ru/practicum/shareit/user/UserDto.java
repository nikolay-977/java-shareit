package ru.practicum.shareit.user;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserDto {
    private Long id;
    @NotNull
    @NotBlank
    private String name;
    @NotNull
    @NotBlank
    @Email
    private String email;
}