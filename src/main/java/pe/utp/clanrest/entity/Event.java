package pe.utp.clanrest.entity;

import jakarta.persistence.*;
import java.time.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name = "events")
public class Event {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  public Long id;

  @Version public Long version;

  @OneToOne(fetch = FetchType.EAGER, optional = false)
  @JoinColumn(unique = true)
  public Quotation quotation;

  public LocalDateTime date;
  public String location;
  public String responsible;
  public String status = "CONFIRMADO";

  @Column(length = 1000)
  public String service;

  @Column(length = 1000)
  public String staff;

  @Column(length = 1000)
  public String equipment;

  @Column(length = 2000)
  public String notes;
}
