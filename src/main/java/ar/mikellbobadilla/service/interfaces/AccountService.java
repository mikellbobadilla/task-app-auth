package ar.mikellbobadilla.service.interfaces;

import ar.mikellbobadilla.dto.AccountRequest;
import ar.mikellbobadilla.dto.AccountResponse;
import ar.mikellbobadilla.dto.ChangePasswordRequest;
import ar.mikellbobadilla.dto.ChangeUsernameRequest;
import ar.mikellbobadilla.exception.AccountException;
import ar.mikellbobadilla.exception.AccountNotFoundException;

public interface AccountService {

    public AccountResponse create(AccountRequest request) throws AccountException;

    public AccountResponse updateUsernameAccount(Long id, ChangeUsernameRequest request) throws AccountException, AccountNotFoundException;

    public void updatePasswordAccount(Long id, ChangePasswordRequest request) throws AccountException, AccountNotFoundException;

    public void deleteAccount(Long id, String password) throws AccountException;
}
