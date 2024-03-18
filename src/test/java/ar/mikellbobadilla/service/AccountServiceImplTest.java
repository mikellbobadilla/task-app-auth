package ar.mikellbobadilla.service;

import ar.mikellbobadilla.dto.AccountRequest;
import ar.mikellbobadilla.dto.AccountResponse;
import ar.mikellbobadilla.exception.AccountException;
import ar.mikellbobadilla.model.Account;
import ar.mikellbobadilla.repository.AccountRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

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

}