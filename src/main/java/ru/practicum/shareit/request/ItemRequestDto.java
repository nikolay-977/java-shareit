package ru.practicum.shareit.request;

import lombok.*;
import ru.practicum.shareit.user.UserDto;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ItemRequestDto {
    private Long id;
    private String description;
    private UserDto requestorDto;
    private LocalDateTime created;
}
