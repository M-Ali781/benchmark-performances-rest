package ma.rest.benchmark.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ItemDTO {
    private Long id;

    @NotBlank
    private String sku;

    @NotBlank
    private String name;

    @NotNull
    private BigDecimal price;

    @NotNull
    private Integer stock;

    private Long categoryId;
    private String categoryName;
    private String description;
    private LocalDateTime updatedAt;
}
