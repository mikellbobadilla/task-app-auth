package ar.mikellbobadilla.service;

import ar.mikellbobadilla.dto.AuthRequest;
import ar.mikellbobadilla.dto.AuthResponse;
import ar.mikellbobadilla.exception.AccountException;
import ar.mikellbobadilla.model.Account;
import ar.mikellbobadilla.repository.AccountRepository;
import ar.mikellbobadilla.service.interfaces.AuthService;
import ar.mikellbobadilla.service.interfaces.JwtService;
import lombok.AllArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final AuthenticationManager manager;
    private final JwtService jwtService;
    private final AccountRepository accountRepository;

    @Override
    public AuthResponse authenticate(AuthRequest request) throws AccountException {
        Authentication auth = new UsernamePasswordAuthenticationToken(request.username(), request.password());

        manager.authenticate(auth);

        Account account = accountRepository.findByUsername(request.username())
                .orElseThrow(() -> new AccountException("Bad credentials!"));

        return new AuthResponse(account.getUsername(), jwtService.createToken(account));
    }
}
