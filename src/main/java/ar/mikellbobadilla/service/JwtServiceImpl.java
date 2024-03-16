package ar.mikellbobadilla.service;

import java.util.Date;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.Claim;
import com.auth0.jwt.interfaces.DecodedJWT;

import ar.mikellbobadilla.service.interfaces.JwtService;

@Service
public class JwtServiceImpl implements JwtService {

    @Value("${jwt.secret}")
    private String KEY_SECRET;

    private Algorithm getSignInKey() {
        return Algorithm.HMAC256(KEY_SECRET);
    }

    private DecodedJWT decodeToken(String token) {
        return JWT.require(getSignInKey())
                .build()
                .verify(token);
    }

    private Map<String, Claim> getAllClaims(String token) {
        return decodeToken(token).getClaims();
    }

    @Override
    public String createToken(UserDetails details) {
        return JWT.create()
                .withClaim("username", details.getUsername())
                .withExpiresAt(new Date(System.currentTimeMillis() + TimeUnit.HOURS.toMillis(24)))
                .sign(getSignInKey());
    }

    @Override
    public boolean isTokenValid(String token, UserDetails details) {
        final String username = getSubject(token);
        final Date expiration = getExpiration(token);
        return (username.equals(details.getUsername()) && !expiration.before(new Date()));
    }

    @Override
    public Date getExpiration(String token) {
        return decodeToken(token).getExpiresAt();
    }

    @Override
    public String getSubject(String token) {
        return getAllClaims(token).get("username").asString();
    }

}
