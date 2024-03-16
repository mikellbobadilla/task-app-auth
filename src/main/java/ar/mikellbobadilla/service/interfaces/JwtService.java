package ar.mikellbobadilla.service.interfaces;

import java.util.Date;

import org.springframework.security.core.userdetails.UserDetails;

public interface JwtService {
    public String createToken(UserDetails details);

    public boolean isTokenValid(String token, UserDetails details);

    public Date getExpiration(String token);

    public String getSubject(String token);
}
