package ar.mikellbobadilla.controller;

import javax.security.auth.login.AccountNotFoundException;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import ar.mikellbobadilla.dto.AccountRequest;
import ar.mikellbobadilla.dto.AccountResponse;
import ar.mikellbobadilla.dto.ChangePasswordRequest;
import ar.mikellbobadilla.dto.ChangeUsernameRequest;
import ar.mikellbobadilla.exception.AccountException;
import ar.mikellbobadilla.service.interfaces.AccountService;
import lombok.AllArgsConstructor;

@RestController
@RequestMapping("/api/accounts")
@AllArgsConstructor
public class AccountController {

    private final AccountService service;

    @PostMapping
    ResponseEntity<AccountResponse> createAccount(@RequestBody AccountRequest request) throws AccountException {
        return new ResponseEntity<>(service.create(request), HttpStatus.CREATED);
    }

    @PatchMapping("/{id}/username")
    ResponseEntity<AccountResponse> updateUsername(@PathVariable Long id, @RequestBody ChangeUsernameRequest request) throws AccountNotFoundException, AccountException {
        return new ResponseEntity<>(service.updateUsernameAccount(id, request), HttpStatus.OK);
    }

    @PatchMapping("/{id}/password")
    ResponseEntity<Void> updatePassword(@PathVariable Long id, @RequestBody ChangePasswordRequest request) throws AccountNotFoundException, AccountException {
        service.updatePasswordAccount(id, request);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @DeleteMapping("/{id}")
    ResponseEntity<Void> deleteAccount(@PathVariable Long id, @RequestBody String password) throws AccountNotFoundException, AccountException {
        service.deleteAccount(id, password);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
