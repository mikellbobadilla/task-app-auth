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
import ar.mikellbobadilla.utils.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TaskServiceImplTest {

    @Mock
    private TaskRepository taskRepository;
    @Mock
    private AccountRepository accountRepository;
    @Mock
    private ObjectMapper mapper;
    @InjectMocks
    private TaskServiceImpl service;

    @BeforeEach
    void setup() {
        service = new TaskServiceImpl(taskRepository, accountRepository, mapper);
    }

    @Test
    void testGetTasks_Success() throws TaskException {
        Pageable pageable = createPageable();
        Page<Task> tasks = createTaskPage(pageable);

        when(taskRepository.findAllByAccountId(1L, pageable)).thenReturn(tasks);

        PageResponse<TaskResponse> response = service.getTasks(1, 10, 1L);
        assertNotNull(response);

        assertInstanceOf(PageResponse.class, response);
    }

    @Test
    void testGetTasks_PageNumberNegative() {
        assertThrows(
                TaskException.class,
                () -> service.getTasks(0, 10, 1L)
        );
    }

    @Test
    void testGetTask_Success() throws TaskNotFoundException {
        Task task = createTask();
        when(taskRepository.findByIdAndAccountId(1L, 1L)).thenReturn(Optional.of(task));
        when(mapper.mapData(TaskResponse.class, task)).thenReturn(createTaskResponse());

        TaskResponse response = service.getTaskById(1L, 1L);
        assertNotNull(response);
    }

    @Test
    void testGetTask_NotFound() {
        when(taskRepository.findByIdAndAccountId(anyLong(), anyLong())).thenReturn(Optional.empty());

        assertThrows(
                TaskNotFoundException.class,
                () -> service.getTaskById(1L, 1L)
        );
    }

    @Test
    void testCreateTask_Success() throws TaskException {

        TaskRequest request = createTaskRequest();
        Task task = createTask();
        TaskResponse response = createTaskResponse();

        when(accountRepository.findById(1L)).thenReturn(Optional.of(createAccount()));
        when(mapper.mapData(Task.class, request)).thenReturn(task);
        when(taskRepository.save(task)).thenReturn(task);
        when(mapper.mapData(TaskResponse.class, task)).thenReturn(response);

        TaskResponse newResponse = service.createTask(request, 1L);
        assertNotNull(newResponse);

    }

    @Test
    void testCreateTask_AccountNotFound() {
        when(accountRepository.findById(anyLong())).thenReturn(Optional.empty());
        TaskRequest request = createTaskRequest();
        assertThrows(
                TaskException.class,
                () -> service.createTask(request, 1L)
        );
    }

    @Test
    void testUpdateTaskFull_Success() throws IllegalAccessException {
        Task task = createTask();
        TaskRequest request = createTaskRequest();

        when(taskRepository.findByIdAndAccountId(anyLong(), anyLong())).thenReturn(Optional.of(task));
        when(mapper.mapData(task, request)).thenReturn(task);
        assertDoesNotThrow(() -> service.updateTaskFull(1L, request, 1L));
    }

    @Test
    void testUpdateTaskFull_TaskNotFound() {
        TaskRequest request = createTaskRequest();
        when(taskRepository.findByIdAndAccountId(anyLong(), anyLong())).thenReturn(Optional.empty());

        assertThrows(
                TaskNotFoundException.class,
                () -> service.updateTaskFull(1L, request, 1L)
        );
    }

    @Test
    void testUpdateTaskPartial_Success() throws IllegalAccessException, TaskException {
        Task task = createTask();
        TaskRequest request = createTaskRequest();
        TaskResponse response = createTaskResponse();

        when(taskRepository.findByIdAndAccountId(anyLong(), anyLong())).thenReturn(Optional.of(task));
        when(mapper.mapData(task, request)).thenReturn(task);
        when(taskRepository.save(task)).thenReturn(task);
        when(mapper.mapData(TaskResponse.class, task)).thenReturn(response);

        TaskResponse newResponse = service.updateTaskPartial(1L, request, 1L);
        assertNotNull(newResponse);
    }

    @Test
    void testUpdateTaskPartial_TaskNotFound() {
        TaskRequest request = createTaskRequest();
        when(taskRepository.findByIdAndAccountId(anyLong(), anyLong())).thenReturn(Optional.empty());

        assertThrows(
                TaskNotFoundException.class,
                () -> service.updateTaskPartial(1L, request, 1L)
        );
    }

    @Test
    void testDeleteTask_Success() {
        doNothing().when(taskRepository).deleteByIdAndAccountId(1L, 1L);
        assertDoesNotThrow(() -> service.deleteTask(1L, 1L));
    }

    private TaskRequest createTaskRequest() {
        return new TaskRequest("title", "description", false, "0000-00-00");
    }

    private TaskResponse createTaskResponse() {
        return new TaskResponse(1L, "title", "description", false, "0000-00-00");
    }

    private Pageable createPageable() {
        return PageRequest.of(0, 10);
    }

    private Page<Task> createTaskPage(Pageable pageable) {
        List<Task> tasks = new ArrayList<>(3);
        tasks.add(createTask());
        return new PageImpl<>(tasks, pageable, 10);
    }

    private Task createTask() {
        return Task.builder()
                .id(1L)
                .title("Title task")
                .description("Description task")
                .targetDate(new Date())
                .isDone(false)
                .account(createAccount())
                .build();
    }

    private Account createAccount() {
        return Account.builder()
                .id(1L)
                .username("username")
                .password("password")
                .build();
    }
}