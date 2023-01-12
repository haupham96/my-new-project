package com.example.bt.app.service.promotion;

import com.example.bt.app.dto.CartDTO;
import com.example.bt.app.dto.ProductDTO;
import com.example.bt.app.entity.Product;
import com.example.bt.app.entity.ProductPromotion;
import com.example.bt.app.entity.Promotion;
import com.example.bt.app.exception.ProductNotFoundException;
import com.example.bt.app.repository.IProductPromotionRepository;
import com.example.bt.app.repository.IProductRepository;
import com.example.bt.app.repository.IPromotionRepository;
import com.example.bt.app.service.product.IProductService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.IOException;
import java.util.Calendar;
import java.util.List;
import java.util.TimeZone;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @author HauPV
 * Unit Test cho PromotionService
 * => OK
 * */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class PromotionServiceTest {

    @Autowired
    private IPromotionService iPromotionService;
    @Autowired
    private IPromotionRepository iPromotionRepository;

    @Autowired
    private IProductService iProductService;
    @Autowired
    private IProductRepository iProductRepository;

    @Autowired
    private IProductPromotionRepository iProductPromotionRepository;

    /**
     * Trường hợp giỏ hàng không có sản phẩm
     * -> Không đc khuyến mãi
     */
    @Test
    @DisplayName("findPromotion_WithNoProductInCart")
    void findPromotion_WithNoProductInCart() {
        CartDTO cartDTO = new CartDTO();
        /* products isEmpty */
        assertTrue(cartDTO.getProducts().isEmpty());
        this.iPromotionService.findPromotion(cartDTO);
        /* ko có promotion */
        assertNull(cartDTO.getPromotion());
    }

    /**
     * Trường hợp hiện đang ko có chương trình khuyến mãi
     * -> Không đc khuyến mãi
     */
    @Test
    @DisplayName("findPromotion_NotFoundPromotionByday")
    void findPromotion_NotFoundPromotionByday() throws IOException, ProductNotFoundException {
        CartDTO cartDTO = new CartDTO();
        ProductDTO productDTO = this.iProductService.findById(14);
        assertNotNull(productDTO);
        /* setup giỏ hàng có sản phẩm */
        cartDTO.changeQuantity(productDTO, 2);
        assertFalse(cartDTO.getProducts().isEmpty());
        /* hiện đang ko có chương trình KM */
        List<Promotion> promotions = this.iPromotionRepository.findPromotionByNow();
        assertTrue(promotions.isEmpty());
        this.iPromotionService.findPromotion(cartDTO);
        /* ko có promotion */
        assertNull(cartDTO.getPromotion());
    }

    /**
     * Trường hợp bị gắn cờ ko được khuyến mãi
     * -> Không đc khuyến mãi
     */
    @Test
    @DisplayName("findPromotion_IsUsedDiscountFlag")
    void findPromotion_IsUsedDiscountFlag() throws IOException, ProductNotFoundException {
        CartDTO cartDTO = new CartDTO();
        cartDTO.setUsedDiscount(true);
        ProductDTO iPhone11DTO = this.iProductService.findById(14);
        ProductDTO iPhone13DTO = this.iProductService.findById(15);
        cartDTO.changeQuantity(iPhone11DTO, 1);
        cartDTO.changeQuantity(iPhone13DTO, 1);

        /* setup khuyến mãi 7 ngày kể từ hôm nay */
        Promotion promotion = new Promotion();
        promotion.setName("Khuyến mãi 15%");
        promotion.setValue(0.15);
        Calendar cldFrom = Calendar.getInstance(TimeZone.getDefault());
        promotion.setFrom(cldFrom);
        Calendar cldTo = (Calendar) cldFrom.clone();
        cldTo.add(Calendar.DATE, 7);
        promotion.setTo(cldTo);
        this.iPromotionRepository.save(promotion);
        /* setup điều kiện khuyến mãi : iPhone11 + iPhone13 */
        Product iPhone11 = this.iProductRepository.findById(14).orElse(null);
        assertNotNull(iPhone11);
        Product iPhone13 = this.iProductRepository.findById(15).orElse(null);
        assertNotNull(iPhone13);
        ProductPromotion iPhone11Promotion = new ProductPromotion();
        iPhone11Promotion.setPromotion(promotion);
        iPhone11Promotion.setProduct(iPhone11);
        this.iProductPromotionRepository.save(iPhone11Promotion);
        ProductPromotion iPhone13Promotion = new ProductPromotion();
        iPhone13Promotion.setPromotion(promotion);
        iPhone13Promotion.setProduct(iPhone13);
        this.iProductPromotionRepository.save(iPhone13Promotion);

        /* testing */
        this.iPromotionService.findPromotion(cartDTO);
        /* ko có promotion */
        assertNull(cartDTO.getPromotion());

        /* clear data sau khi test */
        this.iProductPromotionRepository.delete(iPhone11Promotion);
        this.iProductPromotionRepository.delete(iPhone13Promotion);
        this.iPromotionRepository.delete(promotion);
    }

    /**
     * Trường hợp hàng trong giỏ không đủ để hưởng khuyến mãi
     * -> Không đc khuyến mãi
     */
    @Test
    @DisplayName("findPromotion_WithNotEnoughProduct")
    void findPromotion_WithNotEnoughProduct() throws IOException, ProductNotFoundException {
        CartDTO cartDTO = new CartDTO();
        /* setup giỏ hàng chỉ có 1 sp iPhone11 */
        ProductDTO iPhone11DTO = this.iProductService.findById(14);
        cartDTO.changeQuantity(iPhone11DTO, 1);

        /* setup khuyến mãi 7 ngày kể từ hôm nay */
        Promotion promotion = new Promotion();
        promotion.setName("Khuyến mãi 15%");
        promotion.setValue(0.15);
        Calendar cldFrom = Calendar.getInstance(TimeZone.getDefault());
        promotion.setFrom(cldFrom);
        Calendar cldTo = (Calendar) cldFrom.clone();
        cldTo.add(Calendar.DATE, 7);
        promotion.setTo(cldTo);
        this.iPromotionRepository.save(promotion);
        /* setup điều kiện khuyến mãi : iPhone11 + iPhone13 */
        Product iPhone11 = this.iProductRepository.findById(14).orElse(null);
        assertNotNull(iPhone11);
        Product iPhone13 = this.iProductRepository.findById(15).orElse(null);
        assertNotNull(iPhone13);
        ProductPromotion iPhone11Promotion = new ProductPromotion();
        iPhone11Promotion.setPromotion(promotion);
        iPhone11Promotion.setProduct(iPhone11);
        this.iProductPromotionRepository.save(iPhone11Promotion);
        ProductPromotion iPhone13Promotion = new ProductPromotion();
        iPhone13Promotion.setPromotion(promotion);
        iPhone13Promotion.setProduct(iPhone13);
        this.iProductPromotionRepository.save(iPhone13Promotion);

        /* testing */
        this.iPromotionService.findPromotion(cartDTO);
        /* ko có promotion */
        assertNull(cartDTO.getPromotion());

        /* clear data sau khi test */
        this.iProductPromotionRepository.delete(iPhone11Promotion);
        this.iProductPromotionRepository.delete(iPhone13Promotion);
        this.iPromotionRepository.delete(promotion);
    }

    /**
     * Trường hợp giỏ hàng được khuyến mãi
     */
    @Test
    @DisplayName("findPromotion_Success")
    void findPromotion_Success() throws IOException, ProductNotFoundException {
        CartDTO cartDTO = new CartDTO();
        /* setup giỏ hàng chỉ có 1 sp iPhone11 */
        ProductDTO iPhone11DTO = this.iProductService.findById(14);
        ProductDTO iPhone13DTO = this.iProductService.findById(15);
        cartDTO.changeQuantity(iPhone11DTO, 1);
        cartDTO.changeQuantity(iPhone13DTO, 1);

        /* setup khuyến mãi 7 ngày kể từ hôm nay */
        Promotion promotion = new Promotion();
        promotion.setName("Khuyến mãi 15%");
        promotion.setValue(0.15);
        Calendar cldFrom = Calendar.getInstance(TimeZone.getDefault());
        cldFrom.set(2023,Calendar.JANUARY,9,0,0,0);
        promotion.setFrom(cldFrom);
        Calendar cldTo = (Calendar) cldFrom.clone();
        cldTo.add(Calendar.DATE, 7);
        promotion.setTo(cldTo);
        this.iPromotionRepository.save(promotion);
        /* setup điều kiện khuyến mãi : iPhone11 + iPhone13 */
        Product iPhone11 = this.iProductRepository.findById(14).orElse(null);
        assertNotNull(iPhone11);
        Product iPhone13 = this.iProductRepository.findById(15).orElse(null);
        assertNotNull(iPhone13);
        ProductPromotion iPhone11Promotion = new ProductPromotion();
        iPhone11Promotion.setPromotion(promotion);
        iPhone11Promotion.setProduct(iPhone11);
        this.iProductPromotionRepository.saveAndFlush(iPhone11Promotion);
        ProductPromotion iPhone13Promotion = new ProductPromotion();
        iPhone13Promotion.setPromotion(promotion);
        iPhone13Promotion.setProduct(iPhone13);
        this.iProductPromotionRepository.saveAndFlush(iPhone13Promotion);

        /* testing */
        this.iPromotionService.findPromotion(cartDTO);
        assertNotNull(promotion);
        assertNotNull(cartDTO.getPromotion());
        assertEquals(promotion, cartDTO.getPromotion());

        /* clear data sau khi test */
        this.iProductPromotionRepository.delete(iPhone11Promotion);
        this.iProductPromotionRepository.delete(iPhone13Promotion);
        this.iPromotionRepository.delete(promotion);
    }

}
