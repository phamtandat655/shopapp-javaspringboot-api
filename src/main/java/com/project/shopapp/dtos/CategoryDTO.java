package com.project.shopapp.dtos;

import jakarta.validation.constraints.NotEmpty;
import lombok.*;

@Data
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CategoryDTO {
    // @NotEmpty: CharSequence , Collection , Map hoặc Array bị ràng buộc là hợp lệ miễn là nó không rỗng và kích thước/độ dài của nó lớn hơn 0.
    private String name;
}
