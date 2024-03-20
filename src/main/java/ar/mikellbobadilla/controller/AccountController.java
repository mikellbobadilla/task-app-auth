package ar.mikellbobadilla.controller;

import ar.mikellbobadilla.dto.AccountRequest;
import ar.mikellbobadilla.dto.AccountResponse;
import ar.mikellbobadilla.dto.ChangePasswordRequest;
import ar.mikellbobadilla.dto.ChangeUsernameRequest;
import ar.mikellbobadilla.exception.AccountException;
import ar.mikellbobadilla.model.Account;
import ar.mikellbobadilla.service.interfaces.AccountService;
import ar.mikellbobadilla.utils.AccountUtil;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/accounts")
@AllArgsConstructor
public class AccountController {

    private final AccountService service;

    @GetMapping
    ResponseEntity<AccountResponse> getAccount() throws AccountException {
        Account account = AccountUtil.getAccountFromContext();
        AccountResponse response;
        response = service.getAccount(account.getId());

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PostMapping
    ResponseEntity<AccountResponse> createAccount(@RequestBody AccountRequest request) throws AccountException {
        AccountResponse response;
        response = service.createAccount(request);

        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @PatchMapping("/username")
    ResponseEntity<AccountResponse> updateUsername(@RequestBody ChangeUsernameRequest request) throws AccountException {
        Account account = AccountUtil.getAccountFromContext();
        AccountResponse response;
        response = service.updateUsername(account.getId(), request);

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PatchMapping("/password")
    ResponseEntity<Void> updatePassword(@RequestBody ChangePasswordRequest request) throws AccountException {
        Account account = AccountUtil.getAccountFromContext();
        service.updatePassword(account.getId(), request);

        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @DeleteMapping
    ResponseEntity<Void> deleteAccount(@RequestBody String password) throws AccountException {
        Account account = AccountUtil.getAccountFromContext();
        service.deleteAccount(account.getId(), password);

        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
