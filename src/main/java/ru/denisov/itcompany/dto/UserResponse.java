package ru.denisov.itcompany.dto;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.Instant;
import java.util.UUID;


public record UserResponse(
        UUID id,

        String name,
        String surname,
        String patronymic,

        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
        Instant birthDate,

        String email,

        UUID projectId
) {
}
