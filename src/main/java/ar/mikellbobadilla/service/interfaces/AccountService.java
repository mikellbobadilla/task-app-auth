package ar.mikellbobadilla.service.interfaces;

import ar.mikellbobadilla.dto.AccountRequest;
import ar.mikellbobadilla.dto.AccountResponse;
import ar.mikellbobadilla.dto.ChangePasswordRequest;
import ar.mikellbobadilla.dto.ChangeUsernameRequest;
import ar.mikellbobadilla.exception.AccountException;
import ar.mikellbobadilla.exception.AccountNotFoundException;

public interface AccountService {

    AccountResponse getAccount(Long accountId) throws AccountNotFoundException;

    AccountResponse createAccount(AccountRequest request) throws AccountException;

    AccountResponse updateUsername(Long accountId, ChangeUsernameRequest request) throws AccountException;

    void updatePassword(Long accountId, ChangePasswordRequest request) throws AccountException;

    void deleteAccount(Long accountId, String password) throws AccountException;
}
