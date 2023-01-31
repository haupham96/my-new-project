package com.example.cartservice.app.entity;

import com.example.cartservice.common.TableName;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Entity
@Builder
@Table(name = TableName.CART_PRODUCT,
        uniqueConstraints = @UniqueConstraint(
                name = "PRODUCT_NAME_CART_ID_UNQ",
                columnNames = {"productName", "cart_id"}
        ))
public class CartProduct {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(nullable = false)
    private String productName;

    @Column(nullable = false)
    private Long productPrice;
    private int quantity;
    @ManyToOne
    @JoinColumn(name = "cart_id", referencedColumnName = "id")
    private Cart cart;

    public CartProduct(String productName, Long productPrice, int quantity, Cart cart) {
        this.productName = productName;
        this.productPrice = productPrice;
        this.quantity = quantity;
        this.cart = cart;
    }
}
