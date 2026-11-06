package ma.rest.benchmark.controllers.mvc;

import ma.rest.benchmark.dto.ItemDTO;
import ma.rest.benchmark.service.CatalogService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/api/mvc/items")
@RequiredArgsConstructor
public class SpringMvcItemController {

    private final CatalogService catalogService;

    @GetMapping
    public ResponseEntity<Page<ItemDTO>> getItems(
            @RequestParam(required = false) Long categoryId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        if (categoryId != null) {
            return ResponseEntity.ok(catalogService.getItemsByCategory(categoryId, page, size));
        }
        return ResponseEntity.ok(catalogService.getItems(page, size));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ItemDTO> getItemById(@PathVariable Long id) {
        return ResponseEntity.ok(catalogService.getItemById(id));
    }

    @PostMapping
    public ResponseEntity<ItemDTO> createItem(@Valid @RequestBody ItemDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(catalogService.createItem(dto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ItemDTO> updateItem(@PathVariable Long id, @Valid @RequestBody ItemDTO dto) {
        return ResponseEntity.ok(catalogService.updateItem(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteItem(@PathVariable Long id) {
        catalogService.deleteItem(id);
        return ResponseEntity.noContent().build();
    }
}
