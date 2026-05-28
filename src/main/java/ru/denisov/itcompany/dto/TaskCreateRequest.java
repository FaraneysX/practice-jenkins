package ru.denisov.itcompany.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record TaskCreateRequest(
        @NotBlank
        String name,

        @NotNull
        UUID projectId
) {
}
