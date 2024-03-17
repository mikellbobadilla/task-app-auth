package ar.mikellbobadilla.service.interfaces;

import ar.mikellbobadilla.dto.AuthRequest;
import ar.mikellbobadilla.dto.AuthResponse;
import ar.mikellbobadilla.exception.AccountException;

public interface AuthService {
    AuthResponse authenticate(AuthRequest request) throws AccountException;
}
