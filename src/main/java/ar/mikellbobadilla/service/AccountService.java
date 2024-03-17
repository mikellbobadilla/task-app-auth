package ar.mikellbobadilla.service;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import ar.mikellbobadilla.dto.AccountRequest;
import ar.mikellbobadilla.dto.AccountResponse;
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



    private AccountResponse  parseToAccontResponse(Account account) {
        return new AccountResponse(account.getId(), account.getUsername());
    }
}
