package com.example.cartservice.app.repository;

import com.example.cartservice.app.entity.CartProduct;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ICartProductReposiroty extends JpaRepository<CartProduct, Integer> {
    List<CartProduct> findAllByCartId(int cartId);

    CartProduct findByCartIdAndProductName(int cartId, String productName);

    @Modifying
    @Query(value = " DELETE FROM cart_product " +
            "WHERE product_name IN :productNames ",
            nativeQuery = true)
    void deleteAllByProductNameIn(@Param("productNames") List<String> productNames);

    @Modifying(flushAutomatically = true)
    void deleteAllByCartId(int cartId);
}
