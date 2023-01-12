package com.example.bt.app.repository;

import com.example.bt.app.entity.Promotion;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Calendar;
import java.util.List;
import java.util.TimeZone;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @author HauPV
 * Unit Test cho IPromotionRepository
 */
@SpringBootTest
public class PromotionRepositoryTest {

    @Autowired
    private IPromotionRepository iPromotionRepository;

    /**
     * Trường hợp tìm ko thấy promotion
     * -> return EmptyList
     */
    @Test
    void findPromotionByNow_WithNotFoundPromotion() {
        List<Promotion> list = this.iPromotionRepository.findPromotionByNow();
        assertTrue(list.isEmpty());
    }

    /**
     * Trường hợp tìm thấy
     */
    @Test
    void findPromotionByNow_Success() {
        /* setup data */
        Promotion promotion = new Promotion();
        promotion.setName("Khuyến mãi 15%");
        promotion.setValue(0.15);
        Calendar cldFrom = Calendar.getInstance(TimeZone.getDefault());
        cldFrom.set(2023, Calendar.JANUARY, 10, 0, 0, 0);
        promotion.setFrom(cldFrom);
        Calendar cldTo = (Calendar) cldFrom.clone();
        cldTo.add(Calendar.DATE, 7);
        promotion.setTo(cldTo);
        this.iPromotionRepository.save(promotion);
        assertTrue(promotion.getId() > 0);

        /* Testing */
        List<Promotion> list = this.iPromotionRepository.findPromotionByNow();
        assertFalse(list.isEmpty());
        assertEquals(promotion.getName(), list.get(0).getName());

        /* clear data sau khi test */
        this.iPromotionRepository.deleteById(promotion.getId());
    }

    /**
     * Trường hợp tìm promotion với product_id không hợp lệ
     * -> return Null
     */
    @Test
    void findPromotionByProductId_WithInvalidProductId() {
        Promotion promotion = this.iPromotionRepository.findPromotionByProductId(0);
        assertNull(promotion);
    }

    /**
     * Trường hợp tìm promotion với product_id hợp lệ
     */
    @Test
    void findPromotionByProductId_Success() {
        Promotion promotion = this.iPromotionRepository.findPromotionByProductId(14);
        assertNotNull(promotion);
    }

}
