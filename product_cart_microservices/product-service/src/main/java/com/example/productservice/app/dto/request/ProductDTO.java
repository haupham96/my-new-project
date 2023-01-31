package com.example.productservice.app.dto.request;


import com.example.productservice.app.entity.Product;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;
import org.springframework.web.multipart.MultipartFile;

@Data
@Builder
public class ProductDTO implements Validator {
    private Integer id;
    @NotBlank(message = "name cannot be empty")
    private String name;

    @NotNull(message = "cannot be null")
    @Min(value = 1000000, message = "price must be greater than 1,000,000 VND")
    private long price;

    @NotBlank(message = "cannot be empty")
    private String description;

    private MultipartFile mainImage;
    private MultipartFile[] detailImages;

    public static Product mapToEntity(ProductDTO productDTO) {
        return Product.builder()
                .id(productDTO.getId())
                .name(productDTO.getName())
                .price(productDTO.getPrice())
                .description(productDTO.getDescription())
                .build();
    }

    @Override
    public boolean supports(Class<?> clazz) {
        return false;
    }

    @Override
    public void validate(Object target, Errors errors) {

    }
}
