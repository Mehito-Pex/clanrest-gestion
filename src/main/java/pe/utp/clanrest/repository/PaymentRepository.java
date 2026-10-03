package pe.utp.clanrest.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pe.utp.clanrest.entity.Payment;

public interface PaymentRepository extends JpaRepository<Payment, Long> {
  java.util.List<Payment> findByEventId(Long id);
}
