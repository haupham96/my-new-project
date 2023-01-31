package com.example.vnpayservice.app.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CartDTO {
    private int id;
    private JwtUser user;
    private List<ProductDTO> products;

    public long getTotalPrice() {
        return this.products.stream()
                .map(ProductDTO::getProductPrice)
                .reduce(Long::sum)
                .orElse(0L);
    }

}
