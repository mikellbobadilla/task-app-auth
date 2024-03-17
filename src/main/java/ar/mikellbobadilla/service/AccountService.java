package ar.mikellbobadilla.service;

import java.security.Security;

import javax.security.auth.login.AccountNotFoundException;

import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import ar.mikellbobadilla.dto.AccountRequest;
import ar.mikellbobadilla.dto.AccountResponse;
import ar.mikellbobadilla.dto.UpdateUsernameRequest;
import ar.mikellbobadilla.exception.AccountException;
import ar.mikellbobadilla.model.Account;
import ar.mikellbobadilla.repository.AccountRepository;
import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class AccountService {

    private final AccountRepository repository;
    private final PasswordEncoder encoder;

    public AccountResponse create(AccountRequest request) throws AccountException {

        if (repository.existsByUsername(request.username())) {
            throw new AccountException("Account exists!");
        }

        if (!request.password().equals(request.secondPassword())) {
            throw new AccountException("Password mismatch!");
        }

        Account account = Account.builder()
                .username(request.username())
                .password(encoder.encode(request.password()))
                .build();

        return parseToAccontResponse(repository.saveAndFlush(account));
    }

    public AccountResponse updateUsernameAccount(String username, UpdateUsernameRequest request) throws AccountNotFoundException {

        Account account = repository.findByUsername(username)
                .orElseThrow(() -> new AccountNotFoundException("Account not found!"));

        System.out.println("Account: " + account);
        
        return parseToAccontResponse(account);
    }

    private AccountResponse parseToAccontResponse(Account account) {
        return new AccountResponse(account.getId(), account.getUsername());
    }
}
