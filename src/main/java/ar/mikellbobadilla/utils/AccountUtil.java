package ar.mikellbobadilla.utils;

import ar.mikellbobadilla.model.Account;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

public class AccountUtil {
    public static Account getAccountFromContext() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        return (Account) auth.getPrincipal();
    }
}
