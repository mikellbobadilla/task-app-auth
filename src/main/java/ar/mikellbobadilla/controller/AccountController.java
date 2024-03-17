package ar.mikellbobadilla.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import ar.mikellbobadilla.dto.AccountRequest;
import ar.mikellbobadilla.dto.AccountResponse;
import ar.mikellbobadilla.dto.UpdatePasswordRequest;
import ar.mikellbobadilla.dto.UpdateUsernameRequest;
import ar.mikellbobadilla.exception.AccountException;
import ar.mikellbobadilla.service.AccountService;
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

    @PatchMapping("/{username}")
    ResponseEntity<Void> updateUsernameAccount(@PathVariable String username, @RequestBody UpdateUsernameRequest request) {
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @PatchMapping
    ResponseEntity<Void> updatePasswordAccount(UpdatePasswordRequest request) {
        
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

}
