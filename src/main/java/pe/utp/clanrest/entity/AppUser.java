package pe.utp.clanrest.entity;

import jakarta.persistence.*;
import java.time.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name = "app_users")
public class AppUser {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  public Long id;

  @Version public Long version;

  @Column(unique = true, nullable = false)
  public String username;

  @Column(nullable = false)
  public String password;

  public String fullName;

  @Column(nullable = false)
  public String role;

  public boolean active = true;
}
