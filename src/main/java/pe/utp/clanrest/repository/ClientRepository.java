package pe.utp.clanrest.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pe.utp.clanrest.entity.Client;

public interface ClientRepository extends JpaRepository<Client, Long> {}
