package com.example.bt.app.service.product_promotion;


import com.example.bt.app.entity.ProductPromotion;
import com.example.bt.app.repository.IProductPromotionRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @author : HauPV
 * service cho product_promotion
 */
@Slf4j
@Service
public class ProductPromotionServiceImpl implements IProductPromotionService {

    @Autowired
    private IProductPromotionRepository iProductPromotionRepository;

    /*  Tìm product_promotion theo promotion_id */
    @Override
    public List<ProductPromotion> findAllByPromotionId(int promotionId) {
        log.info("class : {}", this.getClass().getSimpleName());
        log.info("method : findAllByPromotionId -> return List<ProductPromotion>");
        return this.iProductPromotionRepository.findAllByPromotionId(promotionId);
    }

}
