package ar.mikellbobadilla.repository;

import ar.mikellbobadilla.model.Account;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@Transactional(Transactional.TxType.NOT_SUPPORTED)
public class AccountRepositoryTest {

    @Autowired
    AccountRepository accountRepository;

    @Test
    void contextLoads() {
        assertNotNull(accountRepository);
    }

    @Test
    void verifyPersistModel() {
        Account account = createAccount();
        assertNull(account.getId());
        accountRepository.save(account);
        assertNotNull(account.getId());
    }

    @Test
    void verifyFindAccountByUsernameAndExists() {
        Account account = createAccount();
        accountRepository.save(account);
        Optional<Account> accountOptional = accountRepository.findByUsername(account.getUsername());
        assertNotNull(accountOptional.get());
    }

    @Test
    void verifyFindAccountByUsernameAndNotExists() {
        Optional<Account> accountOptional = accountRepository.findByUsername("username");
        assertTrue(accountOptional.isEmpty());
    }

    @Test
    void verifyExistsUsernameAccount() {
        Account account = createAccount();
        accountRepository.save(account);
        boolean exists = accountRepository.existsByUsername(account.getUsername());
        assertTrue(exists);
    }

    @Test
    void verifyNoExistsUsernameAccount() {
        boolean exists = accountRepository.existsByUsername("username");
        assertFalse(exists);
    }

    @Test
    void verifyExistsUsernameAccountAndIdNot() {
        Account account = createAccount();
        Account secondAccount = Account.builder()
                .username("username2")
                .password("password")
                .build();
        accountRepository.save(account);
        accountRepository.save(secondAccount);

        boolean exists = accountRepository.existsByUsernameAndIdNot("username", secondAccount.getId());
        assertTrue(exists);
    }

    @Test
    void verifyNoExistsUsernameAccountAndIdNot() {
        Account account = createAccount();
        Account secondAccount = Account.builder()
                .username("username2")
                .password("password")
                .build();
        accountRepository.save(account);
        accountRepository.save(secondAccount);

        boolean exists = accountRepository.existsByUsernameAndIdNot("user", secondAccount.getId());
        assertFalse(exists);
    }

    @Test
    void verifyUpdateAccount() {
        Account account = createAccount();
        accountRepository.save(account);
        assertNotNull(account.getId());
        account.setUsername("user");
        accountRepository.save(account);
        assertEquals("user", account.getUsername());
    }

    @Test
    void verifyRemoveAccount() {
        Account account = createAccount();
        accountRepository.save(account);
        assertNotNull(account.getId());
        accountRepository.deleteById(account.getId());
        boolean exits = accountRepository.existsById(account.getId());
        assertFalse(exits);
    }

    private Account createAccount() {
        Account account = new Account();
        account.setUsername("username");
        account.setPassword("password");
        return account;
    }
}
