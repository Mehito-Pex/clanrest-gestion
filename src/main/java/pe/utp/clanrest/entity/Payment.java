package pe.utp.clanrest.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name = "payments")
public class Payment {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  public Long id;

  @Version public Long version;

  @ManyToOne(fetch = FetchType.EAGER, optional = false)
  public Event event;

  @Column(precision = 12, scale = 2)
  public BigDecimal amount;

  public LocalDate date;
  public String method;
  public String reference;
  public boolean voided;
  public String voidReason;
  public String actor;
}
