package pe.utp.clanrest.entity;

import jakarta.persistence.*;
import java.time.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name = "audit_entries")
public class AuditEntry {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  public Long id;

  @Version public Long version;
  public LocalDateTime timestamp = LocalDateTime.now();
  public String actor;
  public String action;
  public String entityName;
  public Long entityId;

  @Column(length = 2000)
  public String detail;
}
