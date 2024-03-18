package ar.mikellbobadilla.config;

import ar.mikellbobadilla.repository.AccountRepository;
import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
@AllArgsConstructor
public class AppConfig {

    private final AccountRepository accountRepository;

    @Bean
    UserDetailsService service() {
        return username -> accountRepository.findByUsername(username)
                .orElseThrow(
                        () -> new UsernameNotFoundException("Username not found!"));
    }

    @Bean
    PasswordEncoder encoder() {
        return new BCryptPasswordEncoder(10);
    }

    @Bean
    AuthenticationProvider provider() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider(encoder());
        provider.setUserDetailsService(service());
        return provider;
    }

    @Bean
    AuthenticationManager manager(AuthenticationConfiguration configuration) throws Exception {
        return configuration.getAuthenticationManager();
    }
}
