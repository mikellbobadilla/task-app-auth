package ar.mikellbobadilla.controller;

import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import ar.mikellbobadilla.dto.TaskRequest;
import ar.mikellbobadilla.dto.TaskResponse;
import ar.mikellbobadilla.exception.AccountException;
import ar.mikellbobadilla.exception.AccountNotFoundException;
import ar.mikellbobadilla.exception.TaskNotFoundException;
import ar.mikellbobadilla.model.Task;
import ar.mikellbobadilla.service.TaskService;
import lombok.AllArgsConstructor;

@RestController
@RequestMapping("/api/tasks")
@AllArgsConstructor
public class TaskController {

    private final TaskService service;

    @GetMapping
    ResponseEntity<Page<Task>> getTasks(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) throws AccountNotFoundException {
        return new ResponseEntity<>(service.getTasks(page, size), HttpStatus.OK);
    }

    @GetMapping("/{id}")
    ResponseEntity<TaskResponse> getTaskById(Long id) throws AccountException, TaskNotFoundException {
        return new ResponseEntity<>(service.getTaskById(id), HttpStatus.OK);
    }

    @PostMapping
    ResponseEntity<TaskResponse> create(@RequestBody TaskRequest request) throws AccountException {
        return new ResponseEntity<>(service.createTask(request), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    ResponseEntity<Void> updateTask(@PathVariable Long id, @RequestBody TaskRequest request)
            throws TaskNotFoundException, IllegalAccessException {
        service.updateTask(id, request);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @PatchMapping("/{id}")
    ResponseEntity<TaskResponse> updateTaskStatus(@PathVariable Long id, @RequestBody TaskRequest request)
            throws TaskNotFoundException, IllegalAccessException {

        return new ResponseEntity<>(service.updatePartialTask(id, request), HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    ResponseEntity<Void> deleteTask(@PathVariable Long id) throws TaskNotFoundException, IllegalAccessException {
        service.deleteTask(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
