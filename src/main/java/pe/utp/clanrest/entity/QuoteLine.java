package pe.utp.clanrest.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name = "quote_lines")
public class QuoteLine {
  public BigDecimal getSubtotal() {
    return unitPrice.multiply(BigDecimal.valueOf(quantity));
  }

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  public Long id;

  @Version public Long version;

  @ManyToOne(fetch = FetchType.EAGER, optional = false)
  public Quotation quotation;

  @ManyToOne(fetch = FetchType.EAGER, optional = false)
  public CatalogItem item;

  public String description;
  public int quantity;

  @Column(precision = 12, scale = 2)
  public BigDecimal unitPrice;
}
