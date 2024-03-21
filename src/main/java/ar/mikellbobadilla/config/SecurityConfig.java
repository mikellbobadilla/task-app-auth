package ar.mikellbobadilla.config;

import ar.mikellbobadilla.filters.JwtFilter;
import lombok.AllArgsConstructor;

import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

@AllArgsConstructor
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final AuthenticationProvider provider;
    private final JwtFilter jwtFilter;
    @Value("${allowed.origin}")
    private String origin;

    @Bean
    SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        http
                .cors(cors -> Customizer.withDefaults())
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(httpRequest -> {
                    httpRequest.requestMatchers("/api/auth/**").permitAll();
                    httpRequest.requestMatchers(HttpMethod.POST, "/api/accounts").permitAll();
                    httpRequest.requestMatchers(HttpMethod.GET, "/api/accounts/**").authenticated();
                    httpRequest.requestMatchers(HttpMethod.PATCH, "/api/accounts/username").authenticated();
                    httpRequest.requestMatchers(HttpMethod.PATCH, "/api/accounts/password").authenticated();
                    httpRequest.requestMatchers(HttpMethod.DELETE, "/api/accounts/*").authenticated();
                    httpRequest.requestMatchers("/h2-console/**").authenticated();
                    httpRequest.requestMatchers("/api/tasks/**").authenticated();
                    httpRequest.anyRequest().denyAll();
                })
                .sessionManagement(management -> {
                    management.sessionCreationPolicy(SessionCreationPolicy.STATELESS);
                })
                .authenticationProvider(provider)
                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(List.of(origin));
        configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "PATCH", "DELETE", "OPTION"));
        configuration.setAllowedHeaders(List.of("Authorization"));
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

}
