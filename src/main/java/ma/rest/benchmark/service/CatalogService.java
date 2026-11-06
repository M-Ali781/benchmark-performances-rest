package ma.rest.benchmark.service;

import ma.rest.benchmark.dto.CategoryDTO;
import ma.rest.benchmark.dto.ItemDTO;
import ma.rest.benchmark.entities.Category;
import ma.rest.benchmark.entities.Item;
import ma.rest.benchmark.repositories.CategoryRepository;
import ma.rest.benchmark.repositories.ItemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class CatalogService {

    private final CategoryRepository categoryRepository;
    private final ItemRepository itemRepository;

    // --- Category Operations ---

    @Transactional(readOnly = true)
    public Page<CategoryDTO> getCategories(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return categoryRepository.findAll(pageable).map(this::toCategoryDTO);
    }

    @Transactional(readOnly = true)
    public CategoryDTO getCategoryById(Long id) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Category not found with id: " + id));
        return toCategoryDTO(category);
    }

    public CategoryDTO createCategory(CategoryDTO dto) {
        Category category = Category.builder()
                .code(dto.getCode())
                .name(dto.getName())
                .updatedAt(LocalDateTime.now())
                .build();
        return toCategoryDTO(categoryRepository.save(category));
    }

    public CategoryDTO updateCategory(Long id, CategoryDTO dto) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Category not found with id: " + id));
        category.setCode(dto.getCode());
        category.setName(dto.getName());
        category.setUpdatedAt(LocalDateTime.now());
        return toCategoryDTO(categoryRepository.save(category));
    }

    public void deleteCategory(Long id) {
        categoryRepository.deleteById(id);
    }

    // --- Item Operations ---

    @Transactional(readOnly = true)
    public Page<ItemDTO> getItems(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return itemRepository.findAllFetchCategory(pageable).map(this::toItemDTO);
    }

    @Transactional(readOnly = true)
    public ItemDTO getItemById(Long id) {
        Item item = itemRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Item not found with id: " + id));
        return toItemDTO(item);
    }

    @Transactional(readOnly = true)
    public Page<ItemDTO> getItemsByCategory(Long categoryId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return itemRepository.findByCategoryId(categoryId, pageable).map(this::toItemDTO);
    }

    @Transactional(readOnly = true)
    public List<ItemDTO> getCategoryItems(Long categoryId) {
        return itemRepository.findByCategoryIdFetchCategory(categoryId)
                .stream()
                .map(this::toItemDTO)
                .collect(Collectors.toList());
    }

    public ItemDTO createItem(ItemDTO dto) {
        Category category = categoryRepository.findById(dto.getCategoryId())
                .orElseThrow(() -> new RuntimeException("Category not found with id: " + dto.getCategoryId()));

        Item item = Item.builder()
                .sku(dto.getSku())
                .name(dto.getName())
                .price(dto.getPrice())
                .stock(dto.getStock())
                .description(dto.getDescription())
                .category(category)
                .updatedAt(LocalDateTime.now())
                .build();
        return toItemDTO(itemRepository.save(item));
    }

    public ItemDTO updateItem(Long id, ItemDTO dto) {
        Item item = itemRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Item not found with id: " + id));

        if (dto.getCategoryId() != null && (item.getCategory() == null || !item.getCategory().getId().equals(dto.getCategoryId()))) {
            Category category = categoryRepository.findById(dto.getCategoryId())
                    .orElseThrow(() -> new RuntimeException("Category not found with id: " + dto.getCategoryId()));
            item.setCategory(category);
        }

        item.setSku(dto.getSku());
        item.setName(dto.getName());
        item.setPrice(dto.getPrice());
        item.setStock(dto.getStock());
        item.setDescription(dto.getDescription());
        item.setUpdatedAt(LocalDateTime.now());
        return toItemDTO(itemRepository.save(item));
    }

    public void deleteItem(Long id) {
        itemRepository.deleteById(id);
    }

    // --- Entity <-> DTO Mappings ---

    public CategoryDTO toCategoryDTO(Category category) {
        if (category == null) return null;
        return CategoryDTO.builder()
                .id(category.getId())
                .code(category.getCode())
                .name(category.getName())
                .updatedAt(category.getUpdatedAt())
                .build();
    }

    public ItemDTO toItemDTO(Item item) {
        if (item == null) return null;
        return ItemDTO.builder()
                .id(item.getId())
                .sku(item.getSku())
                .name(item.getName())
                .price(item.getPrice())
                .stock(item.getStock())
                .description(item.getDescription())
                .categoryId(item.getCategory() != null ? item.getCategory().getId() : null)
                .categoryName(item.getCategory() != null ? item.getCategory().getName() : null)
                .updatedAt(item.getUpdatedAt())
                .build();
    }
}
