package pe.utp.clanrest.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pe.utp.clanrest.entity.AuditEntry;

public interface AuditEntryRepository extends JpaRepository<AuditEntry, Long> {
  java.util.List<AuditEntry> findTop200ByOrderByIdDesc();
}
