package ar.mikellbobadilla.service;

import ar.mikellbobadilla.dto.PageResponse;
import ar.mikellbobadilla.dto.TaskRequest;
import ar.mikellbobadilla.dto.TaskResponse;
import ar.mikellbobadilla.exception.TaskException;
import ar.mikellbobadilla.exception.TaskNotFoundException;
import ar.mikellbobadilla.model.Account;
import ar.mikellbobadilla.model.Task;
import ar.mikellbobadilla.repository.AccountRepository;
import ar.mikellbobadilla.repository.TaskRepository;
import ar.mikellbobadilla.service.interfaces.TaskService;
import ar.mikellbobadilla.utils.ObjectMapper;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class TaskServiceImpl implements TaskService {

    private final TaskRepository repository;
    private final AccountRepository accountRepository;
    private final ObjectMapper mapper;

    @Override
    public PageResponse<TaskResponse> getTasks(int page, int size, Long accountId) throws TaskException {
        --page;
        if (page < 0) throw new TaskException("Page number cannot be negative");

        Pageable pageable = PageRequest.of(page, size);
        Page<Task> tasks = repository.findAllByAccountId(accountId, pageable);
        PageResponse<TaskResponse> response;
        response = new PageResponse<>(
                tasks.map(task -> mapper.mapData(TaskResponse.class, task)).stream().toList(),
                tasks.getPageable().getPageNumber(),
                tasks.getPageable().getPageSize(),
                tasks.getTotalPages(),
                tasks.getTotalElements(),
                tasks.hasNext(),
                tasks.hasPrevious()
        );
        return response;
    }

    @Override
    public TaskResponse getTaskById(Long id, Long accountId) throws TaskNotFoundException {
        Task task = repository.findByIdAndAccountId(id, accountId)
                .orElseThrow(() -> new TaskNotFoundException("Task not found!"));
        return mapper.mapData(TaskResponse.class, task);
    }

    @Override
    public TaskResponse createTask(TaskRequest request, Long accountId) throws TaskException {

        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new TaskException("Cannot create task"));

        Task task = mapper.mapData(Task.class, request);
        task.setAccount(account);
        return mapper.mapData(TaskResponse.class, repository.save(task));
    }

    @Override
    public void updateTaskFull(Long id, TaskRequest request, Long accountId) throws TaskException, IllegalAccessException {

        Task task = repository.findByIdAndAccountId(id, accountId)
                .orElseThrow(() -> new TaskNotFoundException("Task not found!"));

        Task newTask = mapper.mapData(task, request);
        repository.save(newTask);
    }

    @Override
    public TaskResponse updateTaskPartial(Long id, TaskRequest request, Long accountId) throws TaskException, IllegalAccessException {
        Task task = repository.findByIdAndAccountId(id, accountId)
                .orElseThrow(() -> new TaskNotFoundException("Task not found!"));

        Task newTask = mapper.mapData(task, request);
        repository.save(newTask);
        return mapper.mapData(TaskResponse.class, newTask);
    }

    @Override
    public void deleteTask(Long id, Long accountId) {
        repository.deleteByIdAndAccountId(id, accountId);
    }
}
