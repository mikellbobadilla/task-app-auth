package ar.mikellbobadilla.repository;

import ar.mikellbobadilla.model.Task;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TaskRepository extends JpaRepository<Task, Long> {

    Page<Task> findAllByAccountId(Long id, Pageable pageable);

}
