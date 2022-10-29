package ru.practicum.shareit.server.exception;

import lombok.Data;

@Data
public class ErrorResponse {
    private final String error;
}
