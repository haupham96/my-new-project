package com.example.bt.app.repository;

import com.example.bt.app.entity.ProductPromotion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @author : HauPV
 * repository cho product_promotion
 */
public interface IProductPromotionRepository extends JpaRepository<ProductPromotion, Integer> {

    /*  Tìm product_promotion theo promotion_id */
    @Query(value = " SELECT * FROM product_promotion WHERE promotion_id = ?  ",
            nativeQuery = true)
    List<ProductPromotion> findAllByPromotionId(int promotionId);

}
