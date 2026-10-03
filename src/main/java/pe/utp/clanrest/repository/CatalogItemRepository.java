package pe.utp.clanrest.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pe.utp.clanrest.entity.CatalogItem;

public interface CatalogItemRepository extends JpaRepository<CatalogItem, Long> {
  java.util.List<CatalogItem> findByKindAndActiveTrue(String kind);

  @org.springframework.data.jpa.repository.Lock(jakarta.persistence.LockModeType.PESSIMISTIC_WRITE)
  @org.springframework.data.jpa.repository.Query("select x from CatalogItem x where x.id=:id")
  java.util.Optional<CatalogItem> lockById(
      @org.springframework.data.repository.query.Param("id") Long id);
}
