package com.example.bt.app.service.product_promotion;

import com.example.bt.app.entity.ProductPromotion;

import java.util.List;

/**
 * @author : HauPV
 * service cho product_promotion
 */
public interface IProductPromotionService {
    List<ProductPromotion> findAllByPromotionId(int promotionId);
}
