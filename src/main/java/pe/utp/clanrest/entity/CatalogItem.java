package pe.utp.clanrest.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name = "catalog_items")
public class CatalogItem {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  public Long id;

  @Version public Long version;

  @Column(nullable = false)
  public String name;

  @Column(nullable = false)
  public String kind;

  public String unit = "unidad";

  @Column(precision = 12, scale = 2, nullable = false)
  public BigDecimal price = BigDecimal.ZERO;

  @Column(precision = 12, scale = 3, nullable = false)
  public BigDecimal stock = BigDecimal.ZERO;

  @Column(precision = 12, scale = 3, nullable = false)
  public BigDecimal minimum = BigDecimal.ZERO;

  @Column(length = 1000)
  public String description;

  public boolean active = true;
}
