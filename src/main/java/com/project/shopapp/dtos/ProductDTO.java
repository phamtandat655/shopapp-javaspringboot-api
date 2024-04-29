package com.project.shopapp.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Data
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ProductDTO {
    // @NotBlank: Chuỗi bị ràng buộc có giá trị miễn là nó không rỗng và độ dài được cắt bớt lớn hơn 0.
    @NotBlank(message = "Product's name cannot be empty")
    @Size(min = 3, max = 200, message="Name must be between 3 and 200 characters")
    private String name;

    @Min(value = 0, message = "Price must be greater than 0")
    @Max(value = 10000000, message = "Price must be less than or equal than 10000000")
    private Float price;

    private String thumbnail;
    private String description;

    @JsonProperty("category_id")
    private Long categoryId;

//    private List<MultipartFile> files;
}
