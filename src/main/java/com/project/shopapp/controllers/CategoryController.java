package com.project.shopapp.controllers;

import com.project.shopapp.dtos.CategoryDTO;
import com.project.shopapp.models.Category;
import com.project.shopapp.responses.CategoryResponse;
import com.project.shopapp.responses.UpdateCategoryResponse;
import com.project.shopapp.services.CategoryService;
import com.project.shopapp.components.LocalizationUtils;
import com.project.shopapp.utils.MessageKeys;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.io.IOException;

@RestController
@RequiredArgsConstructor //để tạo constructor với tham số là categoryService
@RequestMapping("/${api.prefix}/categories")
public class CategoryController {
    @Autowired
    private final CategoryService categoryService;
    private final LocalizationUtils localizationUtils;

    @PostMapping(value = "")
    public ResponseEntity<CategoryResponse> createCategory(
            @Valid @RequestBody CategoryDTO categoryDTO,
            BindingResult result //để trả về lỗi cụ thể khi mà request bị lỗi
    ) throws IOException {
        try {
            if(result.hasErrors()) {
                List<String> errorMessages = result.getFieldErrors()
                        .stream()
                        .map(FieldError::getDefaultMessage)
                        .toList();

                return ResponseEntity.badRequest().body(
                        CategoryResponse.builder()
                                .message(localizationUtils.getLocalizedMessage(MessageKeys.CATEGORY_CREATE_FAILED, errorMessages))
                                .build()
                );
            }
            Category category = categoryService.createCategory(categoryDTO);

            return ResponseEntity.ok(CategoryResponse.builder()
                            .message(localizationUtils.getLocalizedMessage(MessageKeys.CATEGORY_CREATE_SUCCESSFULLY))
                            .category(category)
                            .build()
            );
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(
                    CategoryResponse.builder()
                            .message(localizationUtils.getLocalizedMessage(MessageKeys.CATEGORY_CREATE_FAILED, e.getMessage()))
                            .build()
            );
        }
    }

    @GetMapping("")
    public ResponseEntity<?> getAllCategories(
            @RequestParam("page") int page,
            @RequestParam("limit") int limit
    ) {
        try {
            List<Category> categoryList = categoryService.getAllCategories();
            return ResponseEntity.ok(categoryList);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getCategoryById(@PathVariable("id") String categoryId) {
        try {
            Category category = categoryService.getCategoryById(Long.parseLong(categoryId));
            return ResponseEntity.ok(category);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<UpdateCategoryResponse> updateCategory(
            @PathVariable Long id,
            @Valid @RequestBody CategoryDTO categoryDTO
    ) {
        try {
            categoryService.updateCategory(id, categoryDTO);

            return ResponseEntity.ok(UpdateCategoryResponse.builder()
                    .message(localizationUtils.getLocalizedMessage(MessageKeys.CATEGORY_UPDATE_SUCCESSFULLY))
                    .build()
            );
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(UpdateCategoryResponse.builder().message(e.getMessage()).build());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteCategory(@PathVariable Long id) {
        try {
            categoryService.deleteCategory(id);
            return ResponseEntity.ok(localizationUtils.getLocalizedMessage(MessageKeys.CATEGORY_DELETE_SUCCESSFULLY, id));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
