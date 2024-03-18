package ar.mikellbobadilla.service.interfaces;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import ar.mikellbobadilla.dto.AccountRequest;
import ar.mikellbobadilla.exception.AccountException;
import ar.mikellbobadilla.model.Account;
import ar.mikellbobadilla.repository.AccountRepository;
import ar.mikellbobadilla.service.AccountServiceImpl;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AccountServiceTest {

    @Mock
    private AccountRepository repository;

    @Mock
    private PasswordEncoder encoder;

    @InjectMocks
    private AccountServiceImpl service;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testCreateAccount() throws AccountException {

        doReturn(false).when(repository).existsByUsername(anyString());
        doReturn("passEncoded").when(encoder).encode(anyString());
        Account account = new Account(null, "username", "password");
        doReturn(account).when(repository).save(account);

        AccountRequest request = new AccountRequest("username", "password", "password");
        service.create(request);

        verify(repository, atLeastOnce()).save(any(Account.class));

    }

}
