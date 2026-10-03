package pe.utp.clanrest.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pe.utp.clanrest.entity.StockMovement;

public interface StockMovementRepository extends JpaRepository<StockMovement, Long> {
  java.util.List<StockMovement> findAllByOrderByIdDesc();
}
