package com.example.bt.app.repository;

import com.example.bt.app.entity.Cart;
import com.example.bt.app.entity.CartProduct;
import com.example.bt.app.entity.Product;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.orm.jpa.JpaSystemException;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @author HauPV
 * JUnit Test cho ICartProductRepository
 */
@SpringBootTest
class CartProductRepositoryTest {

    @Autowired
    private ICartProductRepository iCartProductRepository;
    @Autowired
    private ICartRepository iCartRepository;
    @Autowired
    private IProductRepository iProductRepository;

    /**
     * Trường hợp cart_id không tồn tại trong table cart_product
     */
    @Test
    void findAllByCartId_WithInValidCartId() {
//      cart_id = 0 không có data trong table cart_product
        int invalidCartId = 0;
        List<CartProduct> list = this.iCartProductRepository.findAllByCartId(invalidCartId);
        assertEquals(0, list.size());
    }

    /**
     * Trường hợp tìm được List CartProduct theo cart_id
     */
    @Test
    void findAllByCartId_Success() {
        /* thêm cart và cart_product */
        Cart cart = new Cart();
        this.iCartRepository.save(cart);
        Product product1 = this.iProductRepository.findById(14).orElse(null);
        Product product2 = this.iProductRepository.findById(15).orElse(null);
        assertNotNull(product1);
        assertNotNull(product2);

        assert cart.getCartId() > 0 : "not update id";

        CartProduct cartProduct1 = new CartProduct();
        cartProduct1.setCart(cart);
        cartProduct1.setProduct(product1);
        cartProduct1.setQuantity(1);
        this.iCartProductRepository.save(cartProduct1);

        CartProduct cartProduct2 = new CartProduct();
        cartProduct2.setCart(cart);
        cartProduct2.setProduct(product2);
        cartProduct2.setQuantity(1);
        this.iCartProductRepository.save(cartProduct2);

        /* Testing */
        List<CartProduct> cartProducts = this.iCartProductRepository.findAllByCartId(cart.getCartId());
        assertFalse(cartProducts.isEmpty());
        assertEquals(2, cartProducts.size());

        /* clear data sau khi test */
        this.iCartProductRepository.deleteAllCartProductByCartId(cart.getCartId());
        this.iCartRepository.deleteById(cart.getCartId());
    }

    /**
     * Trường hợp ko tìm thấy cart_product với cart_id ko hợp lệ
     * -> return null
     */
    @Test
    void findByCartIdAndProductId_FailWithInvalidCartId() {
        CartProduct cartProduct = this.iCartProductRepository.findByCartIdAndProductId(0, 14);
        assertNull(cartProduct);
    }

    /**
     * Trường hợp không tìm thấy CartProduct với product_id không hợp lệ
     * -> return null
     */
    @Test
    void findByCartIdAndProductId_FailWithInvalidProductId() {
        Cart cart = new Cart();
        this.iCartRepository.save(cart);
        assertTrue(cart.getCartId() > 0);
        CartProduct cartProduct = this.iCartProductRepository.findByCartIdAndProductId(cart.getCartId(), 0);
        assertNull(cartProduct);
        /* clear data sau khi test */
        this.iCartRepository.deleteById(cart.getCartId());
    }

    /**
     * Trường hợp tìm thấy cart_product theo cart_id và product_id
     */
    @Test
    void findByCartIdAndProductId_Success() {
        /* seeding data */
        Cart cart = new Cart();
        this.iCartRepository.save(cart);
        assertTrue(cart.getCartId() > 0);
        Product product = this.iProductRepository.findById(14).orElse(null);
        assertNotNull(product);
        CartProduct cartProduct = new CartProduct();
        cartProduct.setCart(cart);
        cartProduct.setProduct(product);
        cartProduct.setQuantity(1);
        this.iCartProductRepository.save(cartProduct);
        assertTrue(cartProduct.getId() > 0);

        /*testing*/
        CartProduct cartProductFound = this.iCartProductRepository
                .findByCartIdAndProductId(cart.getCartId(), product.getId());
        assertNotNull(cartProductFound);
        assertEquals(product.getId(), cartProductFound.getProduct().getId());
        assertEquals(cart.getCartId(), cartProductFound.getCart().getCartId());

        /* clear data sau khi test */
        this.iCartProductRepository.deleteAllCartProductByCartId(cart.getCartId());
        this.iCartRepository.deleteById(cart.getCartId());
    }

    /**
     * Trường hợp id không hợp lệ
     * -> Không có gì xảy ra
     */
    @Test
    void deleteAllCartProductByCartId_WithInvalidId() {
        assertDoesNotThrow(() -> {
            this.iCartProductRepository.deleteAllCartProductByCartId(0);
        });
    }

    /**
     * Trường hợp id hợp lệ
     * -> Xoá trong db
     */
    @Test
    void deleteAllCartProductByCartId_Success() {
        /* seeding data */
        Cart cart = new Cart();
        this.iCartRepository.save(cart);
        assertTrue(cart.getCartId() > 0);
        Product product = this.iProductRepository.findById(14).orElse(null);
        assertNotNull(product);
        CartProduct cartProduct = new CartProduct();
        cartProduct.setCart(cart);
        cartProduct.setProduct(product);
        cartProduct.setQuantity(1);
        this.iCartProductRepository.save(cartProduct);
        assertTrue(cartProduct.getId() > 0);

        /* testing */
        List<CartProduct> beforeDelete = this.iCartProductRepository.findAllByCartId(cart.getCartId());
        assertFalse(beforeDelete.isEmpty());
        this.iCartProductRepository.deleteAllCartProductByCartId(cart.getCartId());
        List<CartProduct> afterDelete = this.iCartProductRepository.findAllByCartId(cart.getCartId());
        assertTrue(afterDelete.isEmpty());

        /* clear data sau khi test */
        this.iCartRepository.deleteById(cart.getCartId());
    }

    /**
     * Trường hợp cart_id không hợp lệ
     * -> Exception
     */
    @Test
    @Transactional
    void saveNew_FailWithInvalidCartId() {
        Product product = this.iProductRepository.findById(14).orElse(null);
        assertNotNull(product);
        assertThrows(DataIntegrityViolationException.class, () -> {
            this.iCartProductRepository.saveNew(0, product.getId(), 1);
        });
    }

    /**
     * Trường hợp product_id không hợp lệ
     * -> Exception
     */
    @Test
    @Transactional
    void saveNew_FailWithInvalidProductId() {
        Cart cart = new Cart();
        this.iCartRepository.save(cart);
        assertTrue(cart.getCartId() > 0);

        assertThrows(DataIntegrityViolationException.class, () -> {
            this.iCartProductRepository.saveNew(cart.getCartId(), 0, 1);
        });
    }

    /**
     * Trường hợp quantity không hợp lệ : < 0
     * -> Exception
     */
    @Test
    @Transactional
    void saveNew_FailWithInvalidQuantity() {
        Cart cart = new Cart();
        this.iCartRepository.save(cart);
        assertTrue(cart.getCartId() > 0);
        Product product = this.iProductRepository.findById(14).orElse(null);
        assertNotNull(product);

        assertThrows(JpaSystemException.class, () -> {
            this.iCartProductRepository.saveNew(cart.getCartId(), product.getId(), 0);
        });
    }

    /**
     * Trường hợp thành công
     */
    @Test
    @Transactional
    void saveNew_Success() {
        /* seeding data */
        Cart cart = new Cart();
        this.iCartRepository.save(cart);
        assertTrue(cart.getCartId() > 0);
        Product product = this.iProductRepository.findById(14).orElse(null);
        assertNotNull(product);

        /* testing */
        this.iCartProductRepository.saveNew(cart.getCartId(), product.getId(), 1);
        CartProduct cartProduct = this.iCartProductRepository
                .findByCartIdAndProductId(cart.getCartId(), product.getId());
        assertNotNull(cartProduct);
        assertEquals(cartProduct.getCart().getCartId(), cart.getCartId());
        assertEquals(cartProduct.getProduct().getId(), product.getId());
        assertEquals(1, cartProduct.getQuantity());

        /* clear data sau khi test */
        this.iCartProductRepository.deleteAllCartProductByCartId(cart.getCartId());
        this.iCartRepository.deleteById(cart.getCartId());
    }

    /**
     * Trường hợp product_id không hợp lệ
     * -> Không có gì thay đổi
     */
    @Test
    void deleteAllByProductId_WithInvalidProductId() {
        assertDoesNotThrow(() -> {
            this.iCartProductRepository.deleteAllByProductId(0);
        });
    }

    /**
     * Trường hợp product_id hợp lệ
     * -> Xoá trong db
     */
    @Transactional
    @Test
    void deleteAllByProductId_Success() {
        /* seeding data */
        Cart cart1 = new Cart();
        this.iCartRepository.save(cart1);
        assertTrue(cart1.getCartId() > 0);

        Cart cart2 = new Cart();
        this.iCartRepository.save(cart2);
        assertTrue(cart2.getCartId() > 0);

        Product product = this.iProductRepository.findById(14).orElse(null);
        assertNotNull(product);
        this.iCartProductRepository.saveNew(cart1.getCartId(), product.getId(), 1);
        this.iCartProductRepository.saveNew(cart2.getCartId(), product.getId(), 1);

        /* testing */
        List<CartProduct> beforeDelete = this.iCartProductRepository.findAllByProductId(product.getId());
        assertEquals(2, beforeDelete.size());
        this.iCartProductRepository.deleteAllByProductId(product.getId());
        List<CartProduct> afterDelete = this.iCartProductRepository.findAllByProductId(product.getId());
        assertTrue(afterDelete.isEmpty());

        /* clear data sau khi test */
        this.iCartRepository.deleteById(cart1.getCartId());
        this.iCartRepository.deleteById(cart2.getCartId());
    }

    /**
     * Trường hợp product_id không hợp lệ
     * -> return EmptyList
     */
    @Test
    void findAllByProductId_WithInvalidId() {
        List<CartProduct> list = this.iCartProductRepository.findAllByProductId(0);
        assertTrue(list.isEmpty());
    }

    /**
     * Trường hợp product_id hợp lệ
     */
    @Test
    @Transactional
    void findAllByProductId_Success() {
        /* seeding data */
        Cart cart1 = new Cart();
        this.iCartRepository.save(cart1);
        assertTrue(cart1.getCartId() > 0);

        Cart cart2 = new Cart();
        this.iCartRepository.save(cart2);
        assertTrue(cart2.getCartId() > 0);

        Product product = this.iProductRepository.findById(14).orElse(null);
        assertNotNull(product);
        this.iCartProductRepository.saveNew(cart1.getCartId(), product.getId(), 1);
        this.iCartProductRepository.saveNew(cart2.getCartId(), product.getId(), 1);

        /* testing */
        List<CartProduct> list = this.iCartProductRepository.findAllByProductId(product.getId());
        assertFalse(list.isEmpty());
        assertEquals(2,list.size());

        /* clear data sau khi test */
        this.iCartProductRepository.deleteAllByProductId(product.getId());
        this.iCartRepository.deleteById(cart1.getCartId());
        this.iCartRepository.deleteById(cart2.getCartId());
    }
}
