package pe.utp.clanrest.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pe.utp.clanrest.entity.Quotation;

public interface QuotationRepository extends JpaRepository<Quotation, Long> {
  @org.springframework.data.jpa.repository.Lock(jakarta.persistence.LockModeType.PESSIMISTIC_WRITE)
  @org.springframework.data.jpa.repository.Query("select x from Quotation x where x.id=:id")
  java.util.Optional<Quotation> lockById(
      @org.springframework.data.repository.query.Param("id") Long id);
}
