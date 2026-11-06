package ma.rest.benchmark.repositories;

import ma.rest.benchmark.entities.Item;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.data.rest.core.annotation.RestResource;

import java.util.List;
import java.util.Optional;

@RepositoryRestResource(path = "items", collectionResourceRel = "items", itemResourceRel = "item")
public interface ItemRepository extends JpaRepository<Item, Long> {

    Optional<Item> findBySku(String sku);

    @RestResource(path = "byCategory")
    Page<Item> findByCategoryId(@Param("categoryId") Long categoryId, Pageable pageable);

    @RestResource(path = "byCategoryList")
    List<Item> findByCategoryId(@Param("categoryId") Long categoryId);

    // Optimized Anti-N+1 fetch join query as specified in Lab indications
    @Query("SELECT i FROM Item i JOIN FETCH i.category WHERE i.category.id = :cid")
    List<Item> findByCategoryIdFetchCategory(@Param("cid") Long categoryId);

    @Query(value = "SELECT i FROM Item i JOIN FETCH i.category",
           countQuery = "SELECT count(i) FROM Item i")
    Page<Item> findAllFetchCategory(Pageable pageable);
}
