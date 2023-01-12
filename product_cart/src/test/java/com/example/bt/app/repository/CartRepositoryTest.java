package com.example.bt.app.repository;

import com.example.bt.app.entity.AppUser;
import com.example.bt.app.entity.Cart;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @author HauPV
 * JUnit Test cho ICartRepository
 */
@SpringBootTest
class CartRepositoryTest {

    @Autowired
    private ICartRepository iCartRepository;

    @Autowired
    private IAppUserRepository iAppUserRepository;

    /**
     * Trường hợp user_id không hợp lệ
     * -> return null
     */
    @Test
    void findCartByUserId_WithInvalidUserId() {
        Cart cart = this.iCartRepository.findCartByUserId(0);
        assertNull(cart);
    }

    /**
     * Trường hợp user_id hợp lệ
     */
    @Test
    void findCartByUserId_Success() {
        AppUser appUser = this.iAppUserRepository.findByUsername("test");
        assertNotNull(appUser);
        Cart cart = this.iCartRepository.findCartByUserId(appUser.getUserId());
        assertNotNull(cart);
    }

    /**
     * Trường hợp setFlag đã sử dụng discount cho cart_id không hợp lệ
     * -> Không có gì thay đổi
     */
    @Test
    void setUsedDiscount_WithInvalidCartId() {
        assertDoesNotThrow(() -> {
            this.iCartRepository.setUsedDiscount(0);
        });
    }

    /**
     * Trường hợp setFlag đã sử dụng discount cho cart_id hợp lệ
     */
    @Test
    void setUsedDiscount_Success() {
        /* setup data */
        AppUser appUser = this.iAppUserRepository.findByUsername("test");
        assertNotNull(appUser);
        Cart cartOfUser = this.iCartRepository.findCartByUserId(appUser.getUserId());
        assertNotNull(cartOfUser);

        /* testing */
        this.iCartRepository.setUsedDiscount(cartOfUser.getCartId());
        cartOfUser = this.iCartRepository.findCartByUserId(appUser.getUserId());
        assertNotNull(cartOfUser);
        assertTrue(cartOfUser.isUsedDiscount());

        /* clear data sau khi test */
        cartOfUser.setUsedDiscount(false);
        iCartRepository.save(cartOfUser);
    }

    /**
     * Trường hợp cart_id không hợp lệ
     * -> không có gì xảy ra
     */
    @Test
    void updateTotalPrice_WithInvalidCartId() {
        this.iCartRepository.updateTotalPrice(0, 1000000L);
    }

    /**
     * Trường hợp update thành công
     */
    @Test
    void updateTotalPrice_Success() {
        /* setup data */
        Cart cart = new Cart();
        this.iCartRepository.save(cart);
        assertTrue(cart.getCartId() > 0);

        /* testing */
        this.iCartRepository.updateTotalPrice(cart.getCartId(), 2000000);
        Cart updated = this.iCartRepository.findById(cart.getCartId()).orElse(null);
        assertNotNull(updated);
        assertTrue(updated.getTotalPrice().longValue() > 1000000);

        /* clear data sau khi test */
        this.iCartRepository.deleteById(cart.getCartId());
    }

}
