package com.example.cartservice.app.dto.request;

import com.example.cartservice.app.dto.KeycloakUserDTO;
import com.example.cartservice.app.dto.ProductDTO;
import lombok.Data;

import java.util.List;

@Data
public class CartRequest {
    private Integer cartId;
    private KeycloakUserDTO user;
    private List<ProductDTO> products;
}
