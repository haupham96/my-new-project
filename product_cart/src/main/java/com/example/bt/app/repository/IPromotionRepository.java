package com.example.bt.app.repository;

import com.example.bt.app.entity.Promotion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

/**
 * @author : HauPV
 * repository cho promotion
 */
public interface IPromotionRepository extends JpaRepository<Promotion, Integer> {
    /* Tìm promotion theo ngày hiện tại */
    @Query(value = " SELECT promotion.id, promotion.start, promotion.name, promotion.end, promotion.value " +
            "FROM devdb.promotion " +
            "WHERE CURRENT_TIMESTAMP() BETWEEN promotion.start  AND promotion.end " +
            " ORDER BY promotion.value ",
            nativeQuery = true)
    List<Promotion> findPromotionByNow();

    @Query(value = " SELECT promotion.id, promotion.start, promotion.name, promotion.end, promotion.value " +
            "FROM promotion " +
            "JOIN product_promotion " +
            "ON promotion.id = product_promotion.promotion_id " +
            "WHERE  product_id = ? " +
            "limit 1 ",
            nativeQuery = true)
    Promotion findPromotionByProductId(int productId);
}
