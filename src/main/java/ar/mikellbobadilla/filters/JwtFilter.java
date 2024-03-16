package ar.mikellbobadilla.filters;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.fasterxml.jackson.databind.ObjectMapper;

import ar.mikellbobadilla.service.interfaces.JwtService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;

@Component
@AllArgsConstructor
public class JwtFilter extends OncePerRequestFilter {

    private static final Logger log = Logger.getLogger(JwtFilter.class.getName());

    private final JwtService jwtService;
    private final UserDetailsService detailsService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        final String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
        final String token;
        final String username;

        try {

            if (authHeader == null || !authHeader.startsWith("Beader ")) {
                filterChain.doFilter(request, response);
                return;
            }


            token = authHeader.substring(7);
            username = jwtService.getSubject(token);

            if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {

                UserDetails details = detailsService.loadUserByUsername(username);

                if (jwtService.isTokenValid(token, details)) {
                    var authToken = new UsernamePasswordAuthenticationToken(details, null, details.getAuthorities());
                    SecurityContextHolder.getContext().setAuthentication(authToken);
                }
            }

            filterChain.doFilter(request, response);

        }catch(RuntimeException exc) {
            log.severe(exc.getMessage());
            Map<String, Object> res = new HashMap<>();
            res.put("status", HttpStatus.FORBIDDEN.value());
            res.put("error", "Session expired!");
            var obj = new ObjectMapper();
            response.getWriter().write(obj.writeValueAsString(res));
        }
    }

}
