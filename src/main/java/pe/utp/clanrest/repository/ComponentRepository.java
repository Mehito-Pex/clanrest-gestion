package pe.utp.clanrest.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pe.utp.clanrest.entity.Component;

public interface ComponentRepository extends JpaRepository<Component, Long> {
  java.util.List<Component> findByParentId(Long id);
}
