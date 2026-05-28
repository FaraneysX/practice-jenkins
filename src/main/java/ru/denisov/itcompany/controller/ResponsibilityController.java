package ru.denisov.itcompany.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.denisov.itcompany.dto.ResponsibilityCreateRequest;
import ru.denisov.itcompany.dto.ResponsibilityResponse;
import ru.denisov.itcompany.dto.ResponsibilityUpdateRequest;
import ru.denisov.itcompany.service.ResponsibilityService;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/responsibility")
@RequiredArgsConstructor
public class ResponsibilityController {
    private final ResponsibilityService responsibilityService;

    @PostMapping
    public ResponseEntity<ResponsibilityResponse> create(@Valid @RequestBody ResponsibilityCreateRequest request) {
        final var response = responsibilityService.create(request);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ResponsibilityResponse> getById(@PathVariable UUID id) {
        final var response = responsibilityService.getById(id);

        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @GetMapping
    public ResponseEntity<List<ResponsibilityResponse>> getAll() {
        final var response = responsibilityService.getAll();

        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @GetMapping("/task-id/{id}")
    public ResponseEntity<List<ResponsibilityResponse>> getAllByTaskId(@PathVariable UUID id) {
        final var response = responsibilityService.getAllByTaskId(id);

        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @GetMapping("/user-id/{id}")
    public ResponseEntity<List<ResponsibilityResponse>> getAllByUserId(@PathVariable UUID id) {
        final var response = responsibilityService.getAllByUserId(id);

        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<ResponsibilityResponse> updateById(@PathVariable UUID id, @RequestBody ResponsibilityUpdateRequest request) {
        final var response = responsibilityService.updateById(id, request);

        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Boolean> deleteById(@PathVariable UUID id) {
        final var response = responsibilityService.deleteById(id);

        return ResponseEntity.status(HttpStatus.OK).body(response);
    }
}
