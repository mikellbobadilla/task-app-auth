package ar.mikellbobadilla.service.interfaces;

import org.springframework.security.core.userdetails.UserDetails;

import java.util.Date;

public interface JwtService {
    public String createToken(UserDetails details);

    public boolean isTokenValid(String token, UserDetails details);

    public Date getExpiration(String token);

    public String getSubject(String token);
}
