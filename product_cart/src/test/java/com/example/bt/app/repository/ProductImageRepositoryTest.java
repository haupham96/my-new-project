package com.example.bt.app.repository;

import com.example.bt.app.entity.Image;
import com.example.bt.app.entity.Product;
import com.example.bt.app.entity.ProductImage;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @author HauPV
 * Unit Test cho IProductImageRepository
 */
@SpringBootTest
public class ProductImageRepositoryTest {

    @Autowired
    private IProductImageRepository iProductImageRepository;
    @Autowired
    private IProductRepository iProductRepository;

    /**
     * Trường hợp product_id ko hợp lệ
     * -> Không có gì thay đổi
     */
    @Test
    void deleteAllByProductId_WithInvalidProductId() {
        assertDoesNotThrow(() -> {
            this.iProductImageRepository.deleteAllByProductId(0);
        });
    }

    /**
     * Trường hợp product_id hợp lệ
     * -> xoá record product_image trong db
     */
    @Test
    void deleteAllByProductId_Success() {
        /* setup data */
        List<ProductImage> iPhone11Images = this.iProductImageRepository.findAllByProductId(14);
        assertTrue(iPhone11Images.size() >= 6);
        Product product = new Product("test", 1000000L, "test");
        this.iProductRepository.save(product);
        assertTrue(product.getId() > 0);

        List<ProductImage> productImagesNew = new ArrayList<>();
        iPhone11Images.forEach(productImage -> {
            ProductImage productImageNew = new ProductImage();
            productImageNew.setProduct(product);
            productImageNew.setImage(productImage.getImage());
            productImagesNew.add(productImageNew);
        });
        assertTrue(productImagesNew.size() > 0);
        this.iProductImageRepository.saveAllAndFlush(productImagesNew);

        /* testing */
        List<ProductImage> beforeDelete = this.iProductImageRepository.findAllByProductId(product.getId());
        assertFalse(beforeDelete.isEmpty());
        this.iProductImageRepository.deleteAllByProductId(product.getId());
        List<ProductImage> afterDelete = this.iProductImageRepository.findAllByProductId(product.getId());
        assertTrue(afterDelete.isEmpty());

        /* clear data sau khi test xong */
        this.iProductRepository.deleteById(product.getId());
    }

    /**
     * Trường hợp tìm theo id ko hợp lệ
     * -> return EmptyList
     */
    @Test
    void findAllByProductId_WithInvalidId() {
        List<ProductImage> list = this.iProductImageRepository.findAllByProductId(0);
        assertTrue(list.isEmpty());
    }

    /**
     * Trường hợp tìm theo id hợp lệ
     */
    @Test
    void findAllByProductId_Success() {
        List<ProductImage> list = this.iProductImageRepository.findAllByProductId(14);
        assertEquals(6, list.size());
    }

    /**
     * Tìm ảnh đại diện với product_id không hợp lệ
     * -> return null
     */
    @Test
    void findByProductIdAndImage_IsMainImage_WithInvalidProductId() {
        ProductImage productImage = this.iProductImageRepository.findByProductIdAndImage_IsMainImage(0, true);
        assertNull(productImage);
    }

    /**
     * Tìm ảnh đại diện với product_id hợp lệ
     */
    @Test
    void findByProductIdAndImage_IsMainImage() {
        ProductImage productImage = this.iProductImageRepository
                .findByProductIdAndImage_IsMainImage(14, true);
        assertNotNull(productImage);
        assertTrue(productImage.getImage().isMainImage());
    }

    /**
     * Tìm các ảnh chi tiết với product_id không hợp lệ
     * -> return EmptyList
     */
    @Test
    void findAllByProductIdAndImage_IsMainImage_WithInvalidProductId() {
        List<ProductImage> detailImages = this.iProductImageRepository
                .findAllByProductIdAndImage_IsMainImage(0, false);
        assertTrue(detailImages.isEmpty());
    }

    /**
     * Tìm các ảnh chi tiết với product_id hợp lệ
     */
    @Test
    void findAllByProductIdAndImage_IsMainImage_Success() {
        List<ProductImage> detailImages = this.iProductImageRepository
                .findAllByProductIdAndImage_IsMainImage(14, false);
        assertEquals(5, detailImages.size());
    }

    /**
     * Xoá product_image với product_id ko hợp lệ
     * -> Không có gì thay đổi
     */
    @Test
    void deleteByProductIdAndImageId_WithInvalidProductId() {
        assertDoesNotThrow(() -> {
            this.iProductImageRepository.deleteByProductIdAndImageId(0, 1);
        });
    }

    /**
     * Xoá product_image với image_id ko hợp lệ
     * -> Không có gì thay đổi
     */
    @Test
    void deleteByProductIdAndImageId_WithInvalidImageId() {
        assertDoesNotThrow(() -> {
            this.iProductImageRepository.deleteByProductIdAndImageId(14, 0);
        });
    }

    /**
     * Xoá product_image với id hợp lệ
     */
    @Test
    void deleteByProductIdAndImageId_Success() {
        /* setup data */
        ProductImage iPhone11MainImage = this.iProductImageRepository
                .findByProductIdAndImage_IsMainImage(14, true);
        assertNotNull(iPhone11MainImage);

        Image image = iPhone11MainImage.getImage();

        Product product = new Product("test", 1000000L, "test");
        this.iProductRepository.save(product);
        assertTrue(product.getId() > 0);

        ProductImage productImage = new ProductImage();
        productImage.setProduct(product);
        productImage.setImage(image);
        this.iProductImageRepository.saveAndFlush(productImage);

        /* testing */
        ProductImage before = this.iProductImageRepository.findByProductIdAndImage_Id(product.getId(), image.getId());
        assertNotNull(before);
        this.iProductImageRepository.deleteByProductIdAndImageId(product.getId(), image.getId());
        ProductImage after = this.iProductImageRepository.findByProductIdAndImage_Id(product.getId(), image.getId());
        assertNull(after);

        /* clear data sau khi test xong */
        this.iProductRepository.deleteById(product.getId());
    }

    /**
     * Trường hợp xoá product_image là các ảnh chi tiết với product_id ko hợp lệ
     * -> Không có gì thay đổi
     */
    @Test
    void deleteAllByProductIdAndImage_IsMainImage_WithInvalidProductId() {
        assertDoesNotThrow(() -> {
            this.iProductImageRepository.deleteAllByProductIdAndImage_IsMainImage(0, false);
        });
    }

    /**
     * Trường hợp xoá product_image là các ảnh chi tiết với product_id hợp lệ
     */
    @Test
    void deleteAllByProductIdAndImage_IsMainImage_Success() {
        /* setup data */
        List<ProductImage> iPhone11Images = this.iProductImageRepository
                .findAllByProductIdAndImage_IsMainImage(14, false);
        assertEquals(5, iPhone11Images.size());
        Product product = new Product("test", 1000000L, "test");
        this.iProductRepository.save(product);
        assertTrue(product.getId() > 0);

        List<ProductImage> detailProductImages = new ArrayList<>();
        iPhone11Images.forEach(productImage -> {
            ProductImage detailImage = new ProductImage();
            detailImage.setProduct(product);
            detailImage.setImage(productImage.getImage());
            detailProductImages.add(detailImage);
        });
        assertEquals(5, detailProductImages.size());
        this.iProductImageRepository.saveAllAndFlush(detailProductImages);

        /* testing */
        List<ProductImage> beforeDelete = this.iProductImageRepository
                .findAllByProductIdAndImage_IsMainImage(product.getId(), false);
        assertEquals(5, beforeDelete.size());
        this.iProductImageRepository.deleteAllByProductId(product.getId());
        List<ProductImage> afterDelete = this.iProductImageRepository
                .findAllByProductIdAndImage_IsMainImage(product.getId(), false);
        assertTrue(afterDelete.isEmpty());

        /* clear data sau khi test xong */
        this.iProductRepository.deleteById(product.getId());
    }

    /**
     * trường hợp xoá các product_image với product_id ko hợp lệ
     * -> Không có gì thay đổi
     */
    @Test
    void deleteAllByProductIdAndImageIds_WithInvalidProductId() {
        assertDoesNotThrow(() -> {
            this.iProductImageRepository.deleteAllByProductIdAndImageIds(0,
                    Arrays.asList(1, 2, 3, 4, 5));
        });
    }

    /**
     * trường hợp xoá các product_image với list image_id ko hợp lệ
     * -> Không có gì thay đổi
     */
    @Test
    void deleteAllByProductIdAndImageIds_WithInvalidListImageId() {
        assertDoesNotThrow(() -> {
            this.iProductImageRepository.deleteAllByProductIdAndImageIds(15, new ArrayList<>());
        });
    }

    /**
     * trường hợp xoá các product_image với product_id và list image_id hợp lệ
     */
    @Test
    void deleteAllByProductIdAndImageIds_Success() {
        /* setup data */
        List<ProductImage> iPhone11Images = this.iProductImageRepository.findAllByProductId(14);
        assertTrue(iPhone11Images.size() >= 6);
        Product product = new Product("test", 1000000L, "test");
        this.iProductRepository.save(product);
        assertTrue(product.getId() > 0);

        List<ProductImage> productImagesNew = new ArrayList<>();
        List<Integer> imageIds = new ArrayList<>();
        iPhone11Images.forEach(productImage -> {
            ProductImage productImageNew = new ProductImage();
            productImageNew.setProduct(product);
            productImageNew.setImage(productImage.getImage());
            productImagesNew.add(productImageNew);
            imageIds.add(productImage.getImage().getId());
        });
        assertEquals(6, productImagesNew.size());
        assertEquals(6, imageIds.size());
        this.iProductImageRepository.saveAllAndFlush(productImagesNew);

        /* testing */
        List<ProductImage> before = this.iProductImageRepository
                .findAllByProductIdAndImageIds(product.getId(), imageIds);
        assertEquals(6, before.size());
        this.iProductImageRepository.deleteAllByProductIdAndImageIds(product.getId(), imageIds);
        List<ProductImage> after = this.iProductImageRepository
                .findAllByProductIdAndImageIds(product.getId(), imageIds);
        assertTrue(after.isEmpty());

        /* clear data sau khi test xong */
        this.iProductRepository.deleteById(product.getId());
    }

    /**
     * Trường hợp tìm product_image với product_id ko hợp lệ
     * -> return null
     */
    @Test
    void findByProductIdAndImage_Id_WithInvalidProductId() {
        ProductImage productImage = this.iProductImageRepository.findByProductIdAndImage_Id(0, 1);
        assertNull(productImage);
    }

    /**
     * Trường hợp tìm product_image với image_id ko hợp lệ
     * -> return null
     */
    @Test
    void findByProductIdAndImage_Id_WithInvalidImageId() {
        ProductImage productImage = this.iProductImageRepository.findByProductIdAndImage_Id(14, 0);
        assertNull(productImage);
    }

    /**
     * Trường hợp tìm product_image với product_id và image_id hợp lệ
     */
    @Test
    void findByProductIdAndImage_Id_Success() {
        ProductImage productImage = this.iProductImageRepository.findByProductIdAndImage_Id(14, 1);
        assertNotNull(productImage);
        assertEquals(14, productImage.getProduct().getId());
        assertEquals(1, productImage.getImage().getId());
    }

    /**
     * Trường hợp tìm list product_image với product_id ko hợp lệ
     * -> return emptyList
     */
    @Test
    void findAllByProductIdAndImageIds_WithInvalidProductId() {
        List<Integer> imageIds = Arrays.asList(1, 2, 3, 4, 5, 6);
        List<ProductImage> list = this.iProductImageRepository.findAllByProductIdAndImageIds(0, imageIds);
        assertTrue(list.isEmpty());
    }

    /**
     * Trường hợp tìm list product_image với list image_id ko hợp lệ
     * -> return emptyList
     */
    @Test
    void findAllByProductIdAndImageIds_WithInvalidListImageId() {
        List<Integer> imageIds = Arrays.asList(-1, -2, -3, -4, -5, -6);
        List<ProductImage> list = this.iProductImageRepository.findAllByProductIdAndImageIds(14, imageIds);
        assertTrue(list.isEmpty());
    }

    /**
     * Trường hợp tìm list product_image với product_id và list image_id hợp lệ
     */
    @Test
    void findAllByProductIdAndImageIds_Success() {
        List<Integer> imageIds = Arrays.asList(1, 2, 3, 4, 5, 6);
        List<ProductImage> list = this.iProductImageRepository.findAllByProductIdAndImageIds(14, imageIds);
        assertFalse(list.isEmpty());
        assertEquals(6,list.size());
    }

}
