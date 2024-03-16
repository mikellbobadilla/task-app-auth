package ar.mikellbobadilla.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import ar.mikellbobadilla.model.Account;

@Repository
public interface AccountRepository extends JpaRepository<Account, Long>{
    
    Optional<Account> findByUsername(String username);
    boolean existsByUsername(String username);
    boolean existsByUsernameAndIdNot(String username, Long id);
}
