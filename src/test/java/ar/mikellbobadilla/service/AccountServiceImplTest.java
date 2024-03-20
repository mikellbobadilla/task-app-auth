package ar.mikellbobadilla.service;

import ar.mikellbobadilla.dto.AccountRequest;
import ar.mikellbobadilla.dto.AccountResponse;
import ar.mikellbobadilla.dto.ChangePasswordRequest;
import ar.mikellbobadilla.dto.ChangeUsernameRequest;
import ar.mikellbobadilla.exception.AccountException;
import ar.mikellbobadilla.exception.AccountNotFoundException;
import ar.mikellbobadilla.model.Account;
import ar.mikellbobadilla.repository.AccountRepository;
import ar.mikellbobadilla.utils.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AccountServiceImplTest {

    @Mock
    private PasswordEncoder encoder;
    @Mock
    private AccountRepository repository;
    @Mock
    private ObjectMapper mapper;

    @InjectMocks
    private AccountServiceImpl service;

    @BeforeEach
    void setup() {
        service = new AccountServiceImpl(repository, encoder, mapper);
    }

    @Test
    void testCreateAccount_Success() throws AccountException {

        when(repository.existsByUsername(anyString())).thenReturn(false);
        when(repository.save(any())).thenReturn(createAccount());

        when(mapper.mapData(AccountResponse.class, createAccount()))
                .thenReturn(new AccountResponse(1L, "username"));
        AccountResponse res = service.createAccount(createAccountRequest());
        assertNotNull(res);
    }

    @Test
    void testCreateAccount_AccountExists() {
        when(repository.existsByUsername(anyString())).thenReturn(true);

        AccountRequest request = new AccountRequest("username", "password", "password");

        assertThrows(
                AccountException.class,
                () -> service.createAccount(request));
    }

    @Test
    void testCreateAccount_PasswordMismatch() {
        when(repository.existsByUsername(anyString())).thenReturn(false);
        AccountRequest request = new AccountRequest("username", "password", "username");
        assertThrows(
                AccountException.class,
                () -> service.createAccount(request));
    }

    @Test
    void testGetAccountById_Success() throws AccountNotFoundException {
        Account account = createAccount();
        AccountResponse accountResponse = new AccountResponse(account.getId(), account.getUsername());

        when(repository.findById(account.getId())).thenReturn(Optional.of(account));
        when(mapper.mapData(AccountResponse.class, account)).thenReturn(accountResponse);

        AccountResponse res = service.getAccount(1L);

        assertNotNull(res);
    }

    @Test
    void testGetAccount_NotFound() {

        when(repository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(
                AccountNotFoundException.class,
                () -> service.getAccount(1L));
    }

    @Test
    void testUpdateUsername_Success() throws AccountException {

        Account account = createAccount();

        when(repository.findById(account.getId())).thenReturn(Optional.of(account));
        when(encoder.matches(anyString(), anyString())).thenReturn(true);
        when(repository.existsByUsernameAndIdNot(anyString(), anyLong())).thenReturn(false);
        doReturn(new AccountResponse(account.getId(), account.getUsername())).when(mapper).mapData(AccountResponse.class, account);
        when(repository.save(account)).thenReturn(account);

        AccountResponse res = service.updateUsername(account.getId(), new ChangeUsernameRequest("username", "password"));
        assertNotNull(res);
    }

    @Test
    void testUpdateUsername_AccountNotFound() {

        when(repository.findById(anyLong())).thenReturn(Optional.empty());

        ChangeUsernameRequest request = createUsernameRequest();

        assertThrows(
                AccountNotFoundException.class,
                () -> service.updateUsername(1L, request));
    }

    @Test
    void testUpdateUsername_PassNotMatches() {

        when(repository.findById(anyLong())).thenReturn(Optional.of(createAccount()));
        when(encoder.matches(anyString(), anyString())).thenReturn(false);

        ChangeUsernameRequest request = createUsernameRequest();

        assertThrows(
                AccountException.class,
                () -> service.updateUsername(1L, request)
        );
    }

    @Test
    void testUpdateUsername_UsernameExists() {
        when(repository.findById(anyLong())).thenReturn(Optional.of(createAccount()));
        when(encoder.matches(anyString(), anyString())).thenReturn(true);
        when(repository.existsByUsernameAndIdNot(anyString(), anyLong())).thenReturn(true);

        ChangeUsernameRequest request = createUsernameRequest();

        assertThrows(
                AccountException.class,
                () -> service.updateUsername(1L, request)
        );
    }

    @Test
    void testUpdatePassword_Success() {

        when(repository.findById(anyLong())).thenReturn(Optional.of(createAccount()));
        when(encoder.matches(anyString(), anyString())).thenReturn(true);
        when(encoder.encode(anyString())).thenReturn("passwordEncoded");
        ChangePasswordRequest request = createPasswordRequest();

        assertDoesNotThrow(() -> service.updatePassword(1L, request));
    }

    @Test
    void testUpdatePassword_AccountNotFound() {

        when(repository.findById(anyLong())).thenReturn(Optional.empty());

        ChangePasswordRequest request = createPasswordRequest();

        assertThrows(
                AccountNotFoundException.class,
                () -> service.updatePassword(1L, request)
        );
    }

    @Test
    void testUpdatePassword_PasswordNotMatches() {

        when(repository.findById(anyLong())).thenReturn(Optional.of(createAccount()));
        when(encoder.matches(anyString(), anyString())).thenReturn(false);
        ChangePasswordRequest request = createPasswordRequest();
        ChangePasswordRequest finalRequest = request.withPassword("otherPassword");
        assertThrows(
                AccountException.class,
                () -> service.updatePassword(1L, finalRequest)
        );
    }

    @Test
    void testUpdatePassword_PasswordMismatch() {

        when(repository.findById(anyLong())).thenReturn(Optional.of(createAccount()));
        when(encoder.matches(anyString(), anyString())).thenReturn(true);
        ChangePasswordRequest request = createPasswordRequest();
        ChangePasswordRequest finalRequest = request.withNewPasswordAndConfirmNewPassword(
                "newPassword",
                "otherPassword"
        );

        assertThrows(
                AccountException.class,
                () -> service.updatePassword(1L, finalRequest)
        );
    }

    @Test
    void deleteAccount_Success() {

        when(repository.findById(anyLong())).thenReturn(Optional.of(createAccount()));
        when(encoder.matches(anyString(), anyString())).thenReturn(true);

        assertDoesNotThrow(() -> service.deleteAccount(1L, "password"));
    }

    @Test
    void deleteAccount_AccountNotFound() {

        when(repository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(
                AccountNotFoundException.class,
                () -> service.deleteAccount(1L, "password")
        );
    }

    @Test
    void deleteAccount_PasswordNotMatches() {

        when(repository.findById(1L)).thenReturn(Optional.of(createAccount()));
        when(encoder.matches(anyString(), anyString())).thenReturn(false);

        assertThrows(
                AccountException.class,
                () -> service.deleteAccount(1L, "password")
        );
    }

    private Account createAccount() {
        return Account.builder()
                .id(1L)
                .username("username")
                .password("password")
                .build();
    }

    private AccountRequest createAccountRequest() {
        return new AccountRequest("username", "password", "password");
    }

    private ChangePasswordRequest createPasswordRequest() {
        return new ChangePasswordRequest(
                "password",
                "newPassword",
                "newPassword"
        );
    }

    private ChangeUsernameRequest createUsernameRequest() {
        return new ChangeUsernameRequest("username", "password");
    }
}