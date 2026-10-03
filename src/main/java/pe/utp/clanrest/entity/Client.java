package pe.utp.clanrest.entity;

import jakarta.persistence.*;
import java.time.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name = "clients")
public class Client {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  public Long id;

  @Version public Long version;

  @Column(nullable = false, length = 120)
  public String name;

  public String documentNumber;
  public String phone;
  public String email;

  @Column(length = 1000)
  public String preferences;

  public boolean active = true;
}
