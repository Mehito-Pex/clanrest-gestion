package pe.utp.clanrest.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pe.utp.clanrest.entity.EventTask;

public interface EventTaskRepository extends JpaRepository<EventTask, Long> {
  java.util.List<EventTask> findByEventId(Long id);
}
