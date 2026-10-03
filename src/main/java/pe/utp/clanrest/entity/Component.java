package pe.utp.clanrest.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name = "components")
public class Component {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  public Long id;

  @Version public Long version;

  @ManyToOne(fetch = FetchType.EAGER, optional = false)
  public CatalogItem parent;

  @ManyToOne(fetch = FetchType.EAGER, optional = false)
  public CatalogItem ingredient;

  @Column(precision = 12, scale = 3, nullable = false)
  public BigDecimal quantity;
}
