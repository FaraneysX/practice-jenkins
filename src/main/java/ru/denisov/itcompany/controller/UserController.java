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
import ru.denisov.itcompany.dto.UserCreateRequest;
import ru.denisov.itcompany.dto.UserResponse;
import ru.denisov.itcompany.dto.UserUpdateRequest;
import ru.denisov.itcompany.service.UserService;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/user")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @PostMapping
    public ResponseEntity<UserResponse> create(@Valid @RequestBody UserCreateRequest request) {
        final var response = userService.create(request);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserResponse> getById(@PathVariable UUID id) {
        final var response = userService.getById(id);

        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @GetMapping
    public ResponseEntity<List<UserResponse>> getAll() {
        final var response = userService.getAll();

        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @GetMapping("/project-id/{id}")
    public ResponseEntity<List<UserResponse>> getAllByProjectId(@PathVariable UUID id) {
        final var response = userService.getAllByProjectId(id);

        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<UserResponse> updateById(@PathVariable UUID id, @RequestBody UserUpdateRequest request) {
        final var response = userService.updateById(id, request);

        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Boolean> deleteById(@PathVariable UUID id) {
        final var response = userService.deleteById(id);

        return ResponseEntity.status(HttpStatus.OK).body(response);
    }
}
