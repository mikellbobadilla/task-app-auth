package ar.mikellbobadilla.service;

import ar.mikellbobadilla.dto.AccountRequest;
import ar.mikellbobadilla.dto.AccountResponse;
import ar.mikellbobadilla.dto.ChangePasswordRequest;
import ar.mikellbobadilla.dto.ChangeUsernameRequest;
import ar.mikellbobadilla.exception.AccountException;
import ar.mikellbobadilla.exception.AccountNotFoundException;
import ar.mikellbobadilla.model.Account;
import ar.mikellbobadilla.repository.AccountRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AccountServiceImplTest {

    @Mock
    private PasswordEncoder encoder;
    @Mock
    private AccountRepository repository;

    @InjectMocks
    private AccountServiceImpl service;

    @BeforeEach
    void setup() {
        service = new AccountServiceImpl(repository, encoder);
    }

    @Test
    void testCreateAccount() throws AccountException {
        when(repository.existsByUsername(anyString())).thenReturn(false);
        when(encoder.encode(anyString())).thenReturn("passEncoded");
        Account account = Account.builder()
                .username("username")
                .password("password")
                .build();
        when(repository.save(any(Account.class))).thenReturn(account);
        AccountRequest request = new AccountRequest("username", "password", "password");

        AccountResponse res = service.create(request);
        assertNotNull(res);
    }

    @Test
    void testCreateAccountExists() {
        when(repository.existsByUsername(anyString())).thenReturn(true);

        AccountRequest request = new AccountRequest("username", "password", "password");

        assertThrows(
                AccountException.class,
                () -> service.create(request));
    }

    @Test
    void testCreateAccountPasswordMismatch() {
        when(repository.existsByUsername(anyString())).thenReturn(false);
        AccountRequest request = new AccountRequest("username", "password", "username");
        assertThrows(
                AccountException.class,
                () -> service.create(request));
    }

    @Test
    void testGetAccountById() throws AccountNotFoundException {
        Account account = authenticate();

        when(repository.findById(1L)).thenReturn(Optional.of(account));

        AccountResponse res = service.getAccount(1L);

        assertNotNull(res);
    }

    @Test
    void testGetAccountByIdAndSameAccountNot() {
        authenticate();

        assertThrows(
                AccountNotFoundException.class,
                () -> service.getAccount(2L));

    }

    @Test
    void testGetAccountNotFound() {
        authenticate();

        when(repository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(
                AccountNotFoundException.class,
                () -> service.getAccount(1L));
    }

    @Test
    void testUpdateUsername() throws AccountNotFoundException, AccountException {
        Account account = authenticate();

        when(encoder.matches(anyString(), anyString())).thenReturn(true);
        when(repository.existsByUsernameAndIdNot(anyString(), anyLong())).thenReturn(false);
        when(repository.findById(1L)).thenReturn(Optional.of(account));

        ChangeUsernameRequest request = new ChangeUsernameRequest("newUsername", "password");

        AccountResponse res = service.updateUsernameAccount(1L, request);

        assertNotNull(res);
        assertEquals("newUsername", res.username());
    }

    @Test
    void testUpdateUsernameAdnSameAccountNot() {
        authenticate();

        when(encoder.matches(anyString(), anyString())).thenReturn(true);

        ChangeUsernameRequest request = new ChangeUsernameRequest("username", "password");

        assertThrows(
                AccountException.class,
                () -> service.updateUsernameAccount(1L, request));

    }

    @Test
    void testUpdateUsernameAndPassIncorrect() {
        authenticate();

        ChangeUsernameRequest request = new ChangeUsernameRequest("username", "otherPass");

        assertThrows(
                AccountException.class,
                () -> service.updateUsernameAccount(1L, request));

    }

    @Test
    void testUpdateUsernameNotFound() {

        authenticate();

        when(repository.findById(1L)).thenReturn(Optional.empty());
        when(encoder.matches(anyString(), anyString())).thenReturn(true);
        when(repository.existsByUsernameAndIdNot(anyString(), anyLong())).thenReturn(false);

        ChangeUsernameRequest request = new ChangeUsernameRequest("username", "password");

        assertThrows(
                AccountNotFoundException.class,
                () -> service.updateUsernameAccount(1L, request));
    }

    @Test
    void testUpdatePassword() {
        Account account = authenticate();

        when(encoder.matches(anyString(), anyString())).thenReturn(true);
        when(repository.findById(account.getId())).thenReturn(Optional.of(account));

        ChangePasswordRequest request = new ChangePasswordRequest("password", "newPass", "newPass");
        assertDoesNotThrow(
                () -> service.updatePasswordAccount(account.getId(), request));

    }

    @Test
    void testUpdatePasswordAndSameAccuntNot() {
        authenticate();

        ChangePasswordRequest request = new ChangePasswordRequest("holamundo", "passsword", "password");

        assertThrows(
                AccountException.class,
                () -> service.updatePasswordAccount(2L, request));
    }

    @Test
    void testUpdatePasswordIncorrect() {
        Account account = authenticate();
        when(encoder.matches(anyString(), anyString())).thenReturn(false);

        ChangePasswordRequest request = new ChangePasswordRequest("holamundo", "passsword", "otherPass");

        assertThrows(
                AccountException.class,
                () -> service.updatePasswordAccount(account.getId(), request));

    }

    @Test
    void testUpdateNewPasswordMismatch() {
        Account account = authenticate();

        when(encoder.matches(anyString(), anyString())).thenReturn(true);

        ChangePasswordRequest request = new ChangePasswordRequest("holamundo", "passsword", "otherPass");

        assertThrows(
                AccountException.class,
                () -> service.updatePasswordAccount(account.getId(), request));

    }

    @Test
    void testUpdatePasswordAccountNotFound() {
        Account account = authenticate();

        when(encoder.matches(anyString(), anyString())).thenReturn(true);
        when(repository.findById(account.getId())).thenReturn(Optional.of(account));

        ChangePasswordRequest request = new ChangePasswordRequest("password", "newPass", "newPass");
        assertDoesNotThrow(
                () -> service.updatePasswordAccount(account.getId(), request));

    }

    @Test
    void testDeleteAccount() {
        Account account = authenticate();

        when(encoder.matches(anyString(), anyString())).thenReturn(true);
        assertDoesNotThrow(
                () -> service.deleteAccount(account.getId(), "password"));
    }

    @Test
    void testDeleteAccountAndSameAccountNot() {
        authenticate();

        when(encoder.matches(anyString(), anyString())).thenReturn(true);

        assertThrows(
                AccountException.class,
                () -> service.deleteAccount(2L, "password"));

    }

    @Test
    void testDeleteAccountPasswordMismatch() {
        authenticate();
        when(encoder.matches(anyString(), anyString())).thenReturn(false);

        assertThrows(
                AccountException.class,
                () -> service.deleteAccount(2L, "password"));
    }

    private Account authenticate() {
        Account account = Account.builder()
                .id(1L)
                .username("username")
                .password("password")
                .build();

        UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(account, null);
        SecurityContextHolder.getContext().setAuthentication(auth);
        return account;
    }

}