package com.project.shopapp.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.project.shopapp.models.Product;
import jakarta.persistence.Column;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;
import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class ProductImageDTO {
    @JsonProperty("image_url")
    @Size(min = 5, max = 200, message = "The image url must be between 5 and 200 characters")
    private String imageUrl;

    @JsonProperty("product_id")
    @Min(value = 1, message = "Id must be greater than 0")
    private Long productId;
}
