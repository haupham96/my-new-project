package com.example.productservice.app.repository;

import com.example.productservice.app.dto.response.ProductPrice;
import com.example.productservice.app.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface IProductRepository extends JpaRepository<Product, Integer> {
    Product findByName(String name);

    @Query(value = " SELECT name , price FROM microservices.product WHERE product.name IN :productNames ; ",
            nativeQuery = true)
    List<ProductPrice> findAllProductPriceByProductName(@Param("productNames") List<String> productNames);
}
