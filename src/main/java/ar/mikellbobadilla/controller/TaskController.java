package ar.mikellbobadilla.controller;

import ar.mikellbobadilla.dto.PageResponse;
import ar.mikellbobadilla.dto.TaskRequest;
import ar.mikellbobadilla.dto.TaskResponse;
import ar.mikellbobadilla.exception.TaskException;
import ar.mikellbobadilla.exception.TaskNotFoundException;
import ar.mikellbobadilla.model.Account;
import ar.mikellbobadilla.service.interfaces.TaskService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/tasks")
@AllArgsConstructor
public class TaskController {

    private final TaskService service;

    @GetMapping
    ResponseEntity<PageResponse<TaskResponse>> getTasks(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) throws TaskException {

        Account account = getAuthentication();

        var response = service.getTasks(page, size, account.getId());

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    ResponseEntity<TaskResponse> getTaskById(Long id) throws TaskNotFoundException {

        Account account = getAuthentication();

        return new ResponseEntity<>(service.getTaskById(id, account.getId()), HttpStatus.OK);
    }

    @PostMapping
    ResponseEntity<TaskResponse> create(@RequestBody TaskRequest request) throws TaskException {

        Account account = getAuthentication();

        return new ResponseEntity<>(service.createTask(request, account.getId()), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    ResponseEntity<Void> updateTask(@PathVariable Long id, @RequestBody TaskRequest request)
            throws TaskException, IllegalAccessException {

        Account account = getAuthentication();

        service.updateTaskFull(id, request, account.getId());
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @PatchMapping("/{id}")
    ResponseEntity<TaskResponse> updateTaskStatus(@PathVariable Long id, @RequestBody TaskRequest request)
            throws TaskException, IllegalAccessException {

        Account account = getAuthentication();

        return new ResponseEntity<>(service.updateTaskPartial(id, request, account.getId()), HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    ResponseEntity<Void> deleteTask(@PathVariable Long id) {

        Account account = getAuthentication();

        service.deleteTask(id, account.getId());
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    private Account getAuthentication() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return (Account) auth.getPrincipal();
    }
}
