package com.example.promotionservice.app.repository;

import com.example.promotionservice.app.entity.Promotion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface IPromotionRepository extends JpaRepository<Promotion, Integer> {
    /* Tìm promotion theo ngày hiện tại */
    @Query(value = " SELECT promotion.id, promotion.start, promotion.name, promotion.end, promotion.value " +
            "FROM microservices_promotion.promotion " +
            "WHERE CURRENT_TIMESTAMP() BETWEEN promotion.start  AND promotion.end " +
            " ORDER BY promotion.value ",
            nativeQuery = true)
    List<Promotion> findPromotionByNow();
}
