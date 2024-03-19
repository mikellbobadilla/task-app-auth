package ar.mikellbobadilla.service.interfaces;

import ar.mikellbobadilla.dto.TaskRequest;
import ar.mikellbobadilla.dto.TaskResponse;
import ar.mikellbobadilla.exception.TaskException;
import ar.mikellbobadilla.exception.TaskNotFoundException;
import ar.mikellbobadilla.model.Task;
import org.springframework.data.domain.Page;

public interface TaskService {

    Page<Task> getTasks(int page, int size, Long accountId);

    TaskResponse getTaskById(Long id, Long accountId) throws TaskNotFoundException;

    TaskResponse createTask(TaskRequest request, Long accountId) throws TaskException;

    void updateTaskFull(Long id, TaskRequest request, Long accountId) throws TaskException;

    TaskResponse updateTaskPartial(Long id, TaskRequest request, Long accountId) throws TaskException;

    void deleteTask(Long id, Long accountId);
}
