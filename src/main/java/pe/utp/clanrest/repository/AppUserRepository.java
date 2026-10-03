package pe.utp.clanrest.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pe.utp.clanrest.entity.AppUser;

public interface AppUserRepository extends JpaRepository<AppUser, Long> {
  java.util.Optional<AppUser> findByUsername(String username);

  long countByRoleAndActiveTrue(String role);
}
