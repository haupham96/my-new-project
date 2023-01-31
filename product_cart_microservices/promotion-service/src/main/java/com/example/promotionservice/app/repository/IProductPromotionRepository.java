package com.example.promotionservice.app.repository;

import com.example.promotionservice.app.entity.ProductPromotion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface IProductPromotionRepository extends JpaRepository<ProductPromotion, Integer> {
    /*  Tìm product_promotion theo promotion_id */
    @Query(value = " SELECT * FROM microservices_promotion.product_promotion WHERE promotion_id = ?  ",
            nativeQuery = true)
    List<ProductPromotion> findAllByPromotionId(int promotionId);
}
