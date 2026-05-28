package ru.denisov.itcompany.dto;

import java.time.Instant;

public record ProjectUpdateRequest(
        String name,
        Instant startedAt,
        Instant endedAt
) {
}
