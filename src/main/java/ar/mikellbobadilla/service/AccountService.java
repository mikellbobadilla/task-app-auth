package ar.mikellbobadilla.service;

import javax.security.auth.login.AccountNotFoundException;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import ar.mikellbobadilla.dto.AccountRequest;
import ar.mikellbobadilla.dto.AccountResponse;
import ar.mikellbobadilla.dto.ChangePasswordRequest;
import ar.mikellbobadilla.dto.ChangeUsernameRequest;
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

    public AccountResponse updateUsernameAccount(Long id, ChangeUsernameRequest request) throws AccountNotFoundException, AccountException {

        Account accountAuth = getAccountFromContextHolder();
        
        boolean passIsCorrect = encoder.matches(request.password(), accountAuth.getPassword());

        boolean isSameAccount = accountAuth.getId().equals(id);

        if (!isSameAccount) {
            throw new AccountException("Account not found!");
        }

        if (!passIsCorrect) {
            throw new AccountException("Password incorrect!");
        }

        if (repository.existsByUsernameAndIdNot(request.username(), id)) {
            throw new AccountException("Username exists!");
        }

        Account account = repository.findById(id)
                .orElseThrow(() -> new AccountNotFoundException("Account not found!"));

        account.setUsername(request.username());
        repository.saveAndFlush(account);
        return parseToAccontResponse(account);
    }

    public void updatePasswordAccount(Long id, ChangePasswordRequest request) throws AccountNotFoundException, AccountException {
        Account accountAuth = getAccountFromContextHolder();

        boolean isSameAccount = accountAuth.getId().equals(id);
        boolean passIsCorrect = encoder.matches(request.password(), accountAuth.getPassword());
        boolean isNewPassMatch = request.newPassword().equals(request.confirmNewPassword());

        if (!isSameAccount) {
            throw new AccountException("Account not found!");
        }

        if (!passIsCorrect) {
            throw new AccountException("Password incorrect!");
        }

        if (!isNewPassMatch) {
            throw new AccountException("Password mismatch!");
        }

        Account account = repository.findById(id)
                .orElseThrow(() -> new AccountNotFoundException("Account not found!"));

        account.setPassword(encoder.encode(request.newPassword()));
        repository.saveAndFlush(account);
    }

    public void deleteAccount(Long id) throws AccountException {
        Account accountAuth = getAccountFromContextHolder();

        boolean isSameAccount = accountAuth.getId().equals(id);

        if (!isSameAccount) {
            throw new AccountException("Account not found!");
        }

        repository.deleteById(id);
    }

    private Account getAccountFromContextHolder() {
        Authentication aut = (UsernamePasswordAuthenticationToken) SecurityContextHolder.getContext().getAuthentication();
        return (Account) aut.getPrincipal();
    }

    private AccountResponse parseToAccontResponse(Account account) {
        return new AccountResponse(account.getId(), account.getUsername());
    }
}
