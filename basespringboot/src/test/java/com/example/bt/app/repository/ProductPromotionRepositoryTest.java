package com.example.bt.app.repository;

import com.example.bt.app.entity.ProductPromotion;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @author HauPV
 * Unit Test cho IProductPromotionRepository
 */
@SpringBootTest
public class ProductPromotionRepositoryTest {

    @Autowired
    private IProductPromotionRepository iProductPromotionRepository;

    /**
     * Trường hợp tìm list product_promotion với promotion_id ko hợp lệ
     * -> return EmptyList
     */
    @Test
    void findAllByPromotionId_WithInvalidPromotionId() {
        List<ProductPromotion> list = this.iProductPromotionRepository.findAllByPromotionId(0);
        assertTrue(list.isEmpty());
    }

    /**
     * Trường hợp tìm list product_promotion với promotion_id hợp lệ
     */
    @Test
    void findAllByPromotionId_Success() {
        List<ProductPromotion> list = this.iProductPromotionRepository.findAllByPromotionId(1);
        assertFalse(list.isEmpty());
        assertEquals(3, list.size());
    }

}
