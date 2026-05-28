package ru.denisov.itcompany.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.Instant;
import java.util.UUID;

public record UserCreateRequest(
        @NotBlank
        String name,

        @NotBlank
        String surname,

        String patronymic,

        @NotNull
        Instant birthDate,

        @NotBlank
        @Email
        String email,

        @NotBlank
        String password,

        @NotNull
        UUID projectId
) {
}
