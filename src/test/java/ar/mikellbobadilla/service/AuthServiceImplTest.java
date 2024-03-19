package ar.mikellbobadilla.service;

import ar.mikellbobadilla.dto.AuthRequest;
import ar.mikellbobadilla.dto.AuthResponse;
import ar.mikellbobadilla.exception.AccountException;
import ar.mikellbobadilla.model.Account;
import ar.mikellbobadilla.repository.AccountRepository;
import ar.mikellbobadilla.service.interfaces.JwtService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthServiceImplTest {

    @Mock
    private AuthenticationManager manager;
    @Mock
    private JwtService jwtService;
    @Mock
    private AccountRepository repository;

    @InjectMocks
    private AuthServiceImpl service;

    @BeforeEach
    void setup() {
        service = new AuthServiceImpl(manager, jwtService, repository);
    }

    @Test
    void testAuthenticate() throws AccountException {

        AuthRequest request = new AuthRequest("username", "password");

        Authentication auth = new UsernamePasswordAuthenticationToken(request.username(), request.password());

        when(manager.authenticate(auth)).thenReturn(auth);

        when(repository.findByUsername(request.username())).thenReturn(Optional.of(new Account(1L, "username", "password")));

        AuthResponse res = service.authenticate(request);

        assertNotNull(res);
    }

    @Test
    void testAuthenticationAccountNotFound() {
        AuthRequest request = new AuthRequest("username", "password");
        Authentication auth = new UsernamePasswordAuthenticationToken(request.username(), request.password());

        when(manager.authenticate(auth)).thenReturn(auth);

        when(repository.findByUsername(anyString())).thenReturn(Optional.empty());

        assertThrows(
                AccountException.class,
                () -> service.authenticate(request)
        );
    }

    @Test
    void testAuthenticationFails() {
        AuthRequest request = new AuthRequest("username", "password");
        Authentication auth = new UsernamePasswordAuthenticationToken(request.username(), request.password());

        when(manager.authenticate(auth)).thenThrow(BadCredentialsException.class);

        assertThrows(
                BadCredentialsException.class,
                () -> service.authenticate(request)
        );
    }
}