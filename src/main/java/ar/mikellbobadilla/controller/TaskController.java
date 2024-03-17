package ar.mikellbobadilla.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import ar.mikellbobadilla.dto.TaskRequest;
import ar.mikellbobadilla.dto.TaskResponse;
import ar.mikellbobadilla.exception.AccountException;
import ar.mikellbobadilla.service.TaskService;
import lombok.AllArgsConstructor;

@RestController
@RequestMapping("/api/tasks")
@AllArgsConstructor
public class TaskController {
    
    private final TaskService service;

    @PostMapping
    ResponseEntity<TaskResponse> create(@RequestBody TaskRequest request) throws AccountException {
        return new ResponseEntity<>(service.createTask(request), HttpStatus.CREATED);
    }
}
