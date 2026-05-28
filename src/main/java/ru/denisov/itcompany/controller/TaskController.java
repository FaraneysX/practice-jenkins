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
import ru.denisov.itcompany.dto.TaskCreateRequest;
import ru.denisov.itcompany.dto.TaskResponse;
import ru.denisov.itcompany.dto.TaskUpdateRequest;
import ru.denisov.itcompany.service.TaskService;

import java.util.List;
import java.util.UUID;


@RestController
@RequestMapping("/api/v1/task")
@RequiredArgsConstructor
public class TaskController {
    private final TaskService taskService;

    @PostMapping
    public ResponseEntity<TaskResponse> create(@Valid @RequestBody TaskCreateRequest request) {
        final var response = taskService.create(request);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<TaskResponse> getById(@PathVariable UUID id) {
        final var response = taskService.getById(id);

        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @GetMapping
    public ResponseEntity<List<TaskResponse>> getAll() {
        final var response = taskService.getAll();

        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @GetMapping("/project-id/{id}")
    public ResponseEntity<List<TaskResponse>> getAllByProjectId(@PathVariable UUID id) {
        final var response = taskService.getAllByProjectId(id);

        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<TaskResponse> updateById(@PathVariable UUID id, @RequestBody TaskUpdateRequest request) {
        final var response = taskService.updateById(id, request);

        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Boolean> deleteById(@PathVariable UUID id) {
        final var response = taskService.deleteById(id);

        return ResponseEntity.status(HttpStatus.OK).body(response);
    }
}
