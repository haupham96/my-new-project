package com.example.bt.app.service.promotion;

import com.example.bt.app.dto.CartDTO;
import com.example.bt.app.entity.Promotion;

/**
 * @author : HauPV
 * service cho promotion
 */
public interface IPromotionService {
    void findPromotion(CartDTO cartDTO);

    Promotion findPromotionByProductId(int productId);
}
