package com.example.cartservice.app.dto;

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
    private KeycloakUserDTO user;
    private List<ProductDTO> products;
    private PromotionDTO promotionDTO;
}
