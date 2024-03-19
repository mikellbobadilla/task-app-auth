package ar.mikellbobadilla.service.interfaces;

import ar.mikellbobadilla.dto.AccountRequest;
import ar.mikellbobadilla.dto.AccountResponse;
import ar.mikellbobadilla.dto.ChangePasswordRequest;
import ar.mikellbobadilla.dto.ChangeUsernameRequest;
import ar.mikellbobadilla.exception.AccountException;
import ar.mikellbobadilla.exception.AccountNotFoundException;

public interface AccountService {

    public AccountResponse getAccount(Long id) throws AccountNotFoundException;

    public AccountResponse createAccount(AccountRequest request) throws AccountException;

    public AccountResponse updateUsername(Long id, ChangeUsernameRequest request) throws AccountException, AccountNotFoundException;

    public void updatePassword(Long id, ChangePasswordRequest request) throws AccountException, AccountNotFoundException;

    public void deleteAccount(Long id, String password) throws AccountException;
}
