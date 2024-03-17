package ar.mikellbobadilla.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import ar.mikellbobadilla.dto.AuthRequest;
import ar.mikellbobadilla.dto.AuthResponse;
import ar.mikellbobadilla.exception.AccountException;
import ar.mikellbobadilla.service.interfaces.AuthService;
import lombok.AllArgsConstructor;

@RestController
@RequestMapping("/api/auth")
@AllArgsConstructor
public class AuthController {
    
    private final AuthService service;

    @PostMapping
    ResponseEntity<AuthResponse> authenticate(@RequestBody AuthRequest request) throws AccountException {
        return new ResponseEntity<>(service.authenticate(request), HttpStatus.OK);
    }
}
