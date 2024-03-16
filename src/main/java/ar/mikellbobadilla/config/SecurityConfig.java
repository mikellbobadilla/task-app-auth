package ar.mikellbobadilla.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import ar.mikellbobadilla.filters.JwtFilter;
import lombok.AllArgsConstructor;

@AllArgsConstructor
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final AuthenticationProvider provider;
    private final JwtFilter jwtFilter;

    SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        http
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(httpRequest -> {
                    httpRequest.requestMatchers("/api/auth/**").permitAll();
                    httpRequest.requestMatchers("/api/todos/**").authenticated();
                    httpRequest.anyRequest().denyAll();
                })
                .sessionManagement(management -> {

                    management.sessionCreationPolicy(SessionCreationPolicy.STATELESS);
                })
                .authenticationProvider(provider)
                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

}
