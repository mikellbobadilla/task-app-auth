package ar.mikellbobadilla.repository;

import ar.mikellbobadilla.model.Task;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TaskRepository extends JpaRepository<Task, Long> {

    Page<Task> findAllByAccountId(Long id, Pageable pageable);

    Optional<Task> findByIdAndAccountId(Long id, Long accountId);

    void deleteByIdAndAccountId(Long id, Long accountId);
}
