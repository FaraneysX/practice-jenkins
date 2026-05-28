package ru.denisov.itcompany.dto;

import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record ResponsibilityCreateRequest(
        @NotNull
        UUID taskId,

        @NotNull
        UUID userId
) {
}
