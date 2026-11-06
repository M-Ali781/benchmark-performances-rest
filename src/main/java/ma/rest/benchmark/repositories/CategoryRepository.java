package ma.rest.benchmark.repositories;

import ma.rest.benchmark.entities.Category;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import java.util.Optional;

@RepositoryRestResource(path = "categories", collectionResourceRel = "categories", itemResourceRel = "category")
public interface CategoryRepository extends JpaRepository<Category, Long> {

    Optional<Category> findByCode(String code);

    Page<Category> findAll(Pageable pageable);
}
