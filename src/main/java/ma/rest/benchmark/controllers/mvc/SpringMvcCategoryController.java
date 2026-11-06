package ma.rest.benchmark.controllers.mvc;

import ma.rest.benchmark.dto.CategoryDTO;
import ma.rest.benchmark.dto.ItemDTO;
import ma.rest.benchmark.service.CatalogService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/mvc/categories")
@RequiredArgsConstructor
public class SpringMvcCategoryController {

    private final CatalogService catalogService;

    @GetMapping
    public ResponseEntity<Page<CategoryDTO>> getCategories(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        return ResponseEntity.ok(catalogService.getCategories(page, size));
    }

    @GetMapping("/{id}")
    public ResponseEntity<CategoryDTO> getCategoryById(@PathVariable Long id) {
        return ResponseEntity.ok(catalogService.getCategoryById(id));
    }

    @GetMapping("/{id}/items")
    public ResponseEntity<List<ItemDTO>> getCategoryItems(@PathVariable Long id) {
        return ResponseEntity.ok(catalogService.getCategoryItems(id));
    }

    @PostMapping
    public ResponseEntity<CategoryDTO> createCategory(@Valid @RequestBody CategoryDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(catalogService.createCategory(dto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<CategoryDTO> updateCategory(@PathVariable Long id, @Valid @RequestBody CategoryDTO dto) {
        return ResponseEntity.ok(catalogService.updateCategory(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCategory(@PathVariable Long id) {
        catalogService.deleteCategory(id);
        return ResponseEntity.noContent().build();
    }
}
