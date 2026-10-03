package pe.utp.clanrest.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name = "quotations")
public class Quotation {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  public Long id;

  @Version public Long version;

  @ManyToOne(fetch = FetchType.EAGER, optional = false)
  public Client client;

  @Column(unique = true)
  public String code;

  public LocalDate created = LocalDate.now();
  public LocalDate validUntil;
  public LocalDateTime eventDate;
  public int attendees;
  public String location;

  @Column(length = 1000)
  public String restrictions;

  public String status = "BORRADOR";

  @Column(precision = 12, scale = 2)
  public BigDecimal discount = BigDecimal.ZERO;

  @Column(precision = 5, scale = 2)
  public BigDecimal taxPercent = BigDecimal.ZERO;

  @Column(precision = 12, scale = 2)
  public BigDecimal total = BigDecimal.ZERO;
}
