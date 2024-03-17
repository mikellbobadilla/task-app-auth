package ar.mikellbobadilla.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import ar.mikellbobadilla.dto.TaskRequest;
import ar.mikellbobadilla.dto.TaskResponse;
import ar.mikellbobadilla.exception.AccountException;
import ar.mikellbobadilla.exception.AccountNotFoundException;
import ar.mikellbobadilla.exception.TaskNotFoundException;
import ar.mikellbobadilla.model.Account;
import ar.mikellbobadilla.model.Task;
import ar.mikellbobadilla.repository.AccountRepository;
import ar.mikellbobadilla.repository.TaskRepository;
import ar.mikellbobadilla.utils.ObjectMapper;
import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class TaskService {

    private final TaskRepository taskRepository;
    private final AccountRepository accountRepository;
    private final ObjectMapper objectMapper;

    public Page<Task> getTasks(int page, int size) throws AccountNotFoundException {

        Account accountAuth = getAccountFromContextHolder();

        boolean accountExists = accountRepository.existsById(accountAuth.getId());

        if (!accountExists) {
            throw new AccountNotFoundException("Cannot find account");
        }

        return taskRepository.findAllByAccountId(accountAuth.getId(), PageRequest.of(page, size));
    }

    public TaskResponse getTaskById(Long id) throws AccountException, TaskNotFoundException {

        Account account = getAccountFromContextHolder();

        if (!accountRepository.existsById(account.getId())) {
            throw new AccountException("Cannot find account");
        }

        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new TaskNotFoundException("Task not found!"));

        boolean taskIsFromAccount = task.getAccount().getId().equals(account.getId());

        if (!taskIsFromAccount) {
            throw new TaskNotFoundException("Task not found!");
        }

        return objectMapper.mapData(TaskResponse.class, task);
    }

    public TaskResponse createTask(TaskRequest request) throws AccountException {

        Task task = objectMapper.mapData(Task.class, request);
        Account account = getAccountFromContextHolder();

        if (!accountRepository.existsById(account.getId())) {
            throw new AccountException("Cannot find account");
        }
        task.setAccount(account);
        return objectMapper.mapData(TaskResponse.class, taskRepository.save(task));
    }

    private Account getAccountFromContextHolder() {
        Authentication aut = (UsernamePasswordAuthenticationToken) SecurityContextHolder.getContext()
                .getAuthentication();
        return (Account) aut.getPrincipal();
    }

    public void updateTask(Long id, TaskRequest request)
            throws TaskNotFoundException, IllegalAccessException {

        Account accountAuth = getAccountFromContextHolder();
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new TaskNotFoundException("Task not found!"));

        if (!task.getAccount().getId().equals(accountAuth.getId())) {
            throw new TaskNotFoundException("Task not found!");
        }

        task = objectMapper.mapData(task, request);
        taskRepository.save(task);
    }

    public TaskResponse updatePartialTask(Long id, TaskRequest request)
            throws TaskNotFoundException, IllegalAccessException {

        Account accountAuth = getAccountFromContextHolder();

        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new TaskNotFoundException("Task not found!"));

        if (!task.getAccount().getId().equals(accountAuth.getId())) {
            throw new TaskNotFoundException("Task not found!");
        }

        task = objectMapper.mapData(task, request);
        return objectMapper.mapData(TaskResponse.class, taskRepository.save(task));
    }

    public void deleteTask(Long id) throws TaskNotFoundException {

        Account accountAuth = getAccountFromContextHolder();

        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new TaskNotFoundException("Task not found!"));

        if (!task.getAccount().getId().equals(accountAuth.getId())) {
            throw new TaskNotFoundException("Task not found!");
        }

        taskRepository.delete(task);
    }

}
