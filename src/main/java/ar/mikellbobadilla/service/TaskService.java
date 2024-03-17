package ar.mikellbobadilla.service;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import ar.mikellbobadilla.dto.TaskRequest;
import ar.mikellbobadilla.dto.TaskResponse;
import ar.mikellbobadilla.exception.AccountException;
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

}
