package pe.utp.clanrest.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name = "stock_movements")
public class StockMovement {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  public Long id;

  @Version public Long version;

  @ManyToOne(fetch = FetchType.EAGER, optional = false)
  public CatalogItem item;

  @ManyToOne(fetch = FetchType.EAGER)
  public Event event;

  public String kind;

  @Column(precision = 12, scale = 3)
  public BigDecimal quantity;

  @Column(precision = 12, scale = 3)
  public BigDecimal resultingStock;

  public String reason;
  public String actor;
  public LocalDateTime timestamp = LocalDateTime.now();
}
