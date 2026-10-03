package pe.utp.clanrest.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pe.utp.clanrest.entity.QuoteLine;

public interface QuoteLineRepository extends JpaRepository<QuoteLine, Long> {
  java.util.List<QuoteLine> findByQuotationId(Long id);
}
