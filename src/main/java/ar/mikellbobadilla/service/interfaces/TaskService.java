package ar.mikellbobadilla.service.interfaces;

import ar.mikellbobadilla.dto.PageResponse;
import ar.mikellbobadilla.dto.TaskRequest;
import ar.mikellbobadilla.dto.TaskResponse;
import ar.mikellbobadilla.exception.TaskException;
import ar.mikellbobadilla.exception.TaskNotFoundException;

public interface TaskService {

    PageResponse<TaskResponse> getTasks(int page, int size, Long accountId) throws TaskException;

    TaskResponse getTaskById(Long id, Long accountId) throws TaskNotFoundException;

    TaskResponse createTask(TaskRequest request, Long accountId) throws TaskException;

    void updateTaskFull(Long id, TaskRequest request, Long accountId) throws TaskException, IllegalAccessException;

    TaskResponse updateTaskPartial(Long id, TaskRequest request, Long accountId) throws TaskException, IllegalAccessException;

    void deleteTask(Long id, Long accountId);
}
