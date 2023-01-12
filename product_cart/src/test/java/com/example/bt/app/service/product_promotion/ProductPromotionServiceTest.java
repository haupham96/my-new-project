package com.example.bt.app.service.product_promotion;

import com.example.bt.app.entity.ProductPromotion;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @author HauPV
 * Unit Test cho ProductPromotionService
 * => OK
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class ProductPromotionServiceTest {

    @Autowired
    private IProductPromotionService iProductPromotionService;

    /**
     * Trường hợp tìm với id không hợp lệ
     * -> trả về Empty List
     */
    @Test
    @DisplayName("findAllByPromotionId_WithInvalidId")
    void findAllByPromotionId_WithInvalidId() {
        List<ProductPromotion> list = this.iProductPromotionService.findAllByPromotionId(0);
        assertTrue(list.isEmpty());
    }

    /**
     * Trường hợp tìm với id hợp lệ
     */
    @Test
    @DisplayName("findAllByPromotionId_Success")
    void findAllByPromotionId_Success() {
        List<ProductPromotion> list = this.iProductPromotionService.findAllByPromotionId(1);
        assertFalse(list.isEmpty());
        assertEquals(3,list.size());
    }

}
