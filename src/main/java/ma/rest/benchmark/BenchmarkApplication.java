package ma.rest.benchmark;

import ma.rest.benchmark.entities.Category;
import ma.rest.benchmark.entities.Item;
import ma.rest.benchmark.repositories.CategoryRepository;
import ma.rest.benchmark.repositories.ItemRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@SpringBootApplication
public class BenchmarkApplication {

    public static void main(String[] args) {
        SpringApplication.run(BenchmarkApplication.class, args);
    }

    @Bean
    CommandLineRunner initData(CategoryRepository categoryRepository, ItemRepository itemRepository) {
        return args -> {
            System.out.println("=== Initialisation du jeu de donnees de demonstration ===");
            List<Category> categories = new ArrayList<>();
            for (int i = 1; i <= 20; i++) {
                Category cat = Category.builder()
                        .code("CAT-" + String.format("%04d", i))
                        .name("Category " + i)
                        .updatedAt(LocalDateTime.now())
                        .build();
                categories.add(categoryRepository.save(cat));
            }

            List<Item> items = new ArrayList<>();
            for (int i = 1; i <= 200; i++) {
                Category cat = categories.get((i - 1) % categories.size());
                Item item = Item.builder()
                        .sku("SKU-" + String.format("%06d", i))
                        .name("Item Product " + i)
                        .price(BigDecimal.valueOf(10.0 + (i * 1.5)))
                        .stock(100 + (i % 50))
                        .category(cat)
                        .description("High-performance benchmark product " + i + " standard payload")
                        .updatedAt(LocalDateTime.now())
                        .build();
                items.add(item);
            }
            itemRepository.saveAll(items);

            System.out.println("Dataset initialise avec succes : " + categoryRepository.count() + " categories, " + itemRepository.count() + " items.");
            System.out.println("Endpoints disponibles :");
            System.out.println(" - Variante A (Jersey JAX-RS)       : http://localhost:8080/api/jersey/items");
            System.out.println(" - Variante C (Spring RestController): http://localhost:8080/api/mvc/items");
            System.out.println(" - Variante D (Spring Data REST)    : http://localhost:8080/api/data-rest/items");
            System.out.println(" - Prometheus Metrics               : http://localhost:8080/actuator/prometheus");
        };
    }
}
