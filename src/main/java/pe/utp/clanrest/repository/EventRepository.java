package pe.utp.clanrest.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pe.utp.clanrest.entity.Event;

public interface EventRepository extends JpaRepository<Event, Long> {
  java.util.Optional<Event> findByQuotationId(Long id);

  @org.springframework.data.jpa.repository.Lock(jakarta.persistence.LockModeType.PESSIMISTIC_WRITE)
  @org.springframework.data.jpa.repository.Query("select x from Event x where x.id=:id")
  java.util.Optional<Event> lockById(
      @org.springframework.data.repository.query.Param("id") Long id);
}
