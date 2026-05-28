package ru.denisov.itcompany.dto;

import jakarta.validation.constraints.NotBlank;

public record ProjectCreateRequest(
        @NotBlank
        String name
) {
}
