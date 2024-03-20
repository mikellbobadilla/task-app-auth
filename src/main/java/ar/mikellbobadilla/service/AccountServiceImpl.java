package ar.mikellbobadilla.service;

import ar.mikellbobadilla.dto.AccountRequest;
import ar.mikellbobadilla.dto.AccountResponse;
import ar.mikellbobadilla.dto.ChangePasswordRequest;
import ar.mikellbobadilla.dto.ChangeUsernameRequest;
import ar.mikellbobadilla.exception.AccountException;
import ar.mikellbobadilla.exception.AccountNotFoundException;
import ar.mikellbobadilla.model.Account;
import ar.mikellbobadilla.repository.AccountRepository;
import ar.mikellbobadilla.service.interfaces.AccountService;
import ar.mikellbobadilla.utils.ObjectMapper;
import lombok.AllArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class AccountServiceImpl implements AccountService {

    private final AccountRepository repository;
    private final PasswordEncoder encoder;
    private final ObjectMapper mapper;

    @Override
    public AccountResponse getAccount(Long accountId) throws AccountNotFoundException {
        Account account = repository.findById(accountId)
                .orElseThrow(() -> new AccountNotFoundException("Account not found!"));
        return mapper.mapData(AccountResponse.class, account);
    }

    @Override
    public AccountResponse createAccount(AccountRequest request) throws AccountException {

        boolean usernameExists = repository.existsByUsername(request.username());

        if (usernameExists) throw new AccountException("Account exists");

        boolean passwordMismatch = !request.password().equals(request.confirmPassword());

        if (passwordMismatch) throw new AccountException("Password mismatch");

        Account account = Account.builder()
                .username(request.username())
                .password(encoder.encode(request.password()))
                .build();

        Account newAccount = repository.save(account);

        return mapper.mapData(AccountResponse.class, newAccount);
    }

    @Override
    public AccountResponse updateUsername(Long accountId, ChangeUsernameRequest request) throws AccountException {

        Account account = repository.findById(accountId)
                .orElseThrow(() -> new AccountNotFoundException("Account not found!"));

        boolean passwordNotMatches = !encoder.matches(request.password(), account.getPassword());

        if (passwordNotMatches) throw new AccountException("Password mismatch");

        boolean usernameExists = repository.existsByUsernameAndIdNot(request.username(), account.getId());

        if (usernameExists) throw new AccountException("Username exists");

        account.setUsername(request.username());

        Account newAccount = repository.save(account);

        return mapper.mapData(AccountResponse.class, newAccount);
    }

    @Override
    public void updatePassword(Long accountId, ChangePasswordRequest request) throws AccountException {
        Account account = repository.findById(accountId)
                .orElseThrow(() -> new AccountNotFoundException("Account not found!"));

        boolean passwordNotMatches = !encoder.matches(request.password(), account.getPassword());

        if (passwordNotMatches) throw new AccountException("Password incorrect!");

        boolean passwordMismatch = !request.newPassword().equals(request.confirmNewPassword());

        if (passwordMismatch) throw new AccountException("Password mismatch!");

        account.setPassword(encoder.encode(request.newPassword()));

        repository.save(account);
    }

    @Override
    public void deleteAccount(Long accountId, String password) throws AccountException {
        Account account = repository.findById(accountId)
                .orElseThrow(() -> new AccountNotFoundException("Account not found!"));

        boolean passwordNotMatches = !encoder.matches(password, account.getPassword());

        if (passwordNotMatches) throw new AccountException("Password incorrect!");

        repository.deleteById(accountId);
    }
}
