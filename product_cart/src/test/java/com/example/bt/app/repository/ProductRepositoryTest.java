package com.example.bt.app.repository;

import com.example.bt.app.entity.Product;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataIntegrityViolationException;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @author HauPV
 * JUnit Test cho IProductRepository
 */
@SpringBootTest
class ProductRepositoryTest {

    @Autowired
    IProductRepository iProductRepository;

    /**
     * Trường hợp không tìm thấy sản phẩm theo tên
     */
    @Test
    void findByName_WithInValidName() {
        String invalidProductName = "abcabc";
        Product product = this.iProductRepository.findByName(invalidProductName);

        assertNull(product);
    }

    /**
     * Trường hợp tìm sản phẩm theo tên với tên hợp lệ
     */
    @Test
    void findByName_Success() {
        String validProductName = "iPhone 11";
        Product product = this.iProductRepository.findByName(validProductName);

        assertNotNull(product);
        assertEquals(validProductName, product.getName());
    }

    /**
     * Trường hợp có các trường bị null
     * -> Exception
     */
    @Test
    void saveOn_WithNullValues() {
        assertThrows(DataIntegrityViolationException.class, () -> {
            Product product = new Product();
            this.iProductRepository.saveOne(product);
        });
    }

    /**
     * Trường hợp bị trùng tên sản phẩm
     * -> Exception
     */
    @Test
    void saveOne_WithExistedName() {
        assertThrows(DataIntegrityViolationException.class, () -> {
            Product product = new Product("iPhone 11", 1000000L, "test");
            this.iProductRepository.saveOne(product);
        });
    }

    /**
     * Trường hợp thêm thành công sản phẩm
     */
    @Test
    void saveOne_Success() {
        Product product = new Product();
        product.setName("test");
        product.setPrice(1000000L);
        product.setDescription("test");
        this.iProductRepository.save(product);
        assertTrue(product.getId() > 0);
        Product created = this.iProductRepository.findById(product.getId()).orElse(null);
        assertNotNull(created);

        /* clear data sau khi test */
        this.iProductRepository.deleteById(product.getId());
    }
}
