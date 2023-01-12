package com.example.bt.app.service.product_image;

import com.example.bt.app.entity.Product;
import com.example.bt.app.entity.ProductImage;
import com.example.bt.app.exception.EmptyCollectionException;
import com.example.bt.app.exception.NullValueException;
import com.example.bt.app.repository.IProductImageRepository;
import com.example.bt.app.repository.IProductRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @author HauPV
 * Test cho ProductImageService
 * */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class ProductImageServiceTest {

    @LocalServerPort
    int port;

    @Autowired
    private ProductImageServiceImpl iProductImageService;
    @Autowired
    private IProductImageRepository iProductImageRepository;
    @Autowired
    private IProductRepository iProductRepository;

    /**
     * Trường hợp thêm vào list rỗng
     * -> Exception
     */
    @Test
    @DisplayName("saveAll_FailWithEmptyList")
    void saveAll_FailWithEmptyList() {
        assertThrows(EmptyCollectionException.class, () -> {
            this.iProductImageService.saveAll(new ArrayList<>());
        });
    }

    /**
     * Trường hợp thêm vào list chứa bất kì giá trị null nào
     * -> Exception
     */
    @Test
    @DisplayName("saveAll_FailWithAnyNullValue")
    void saveAll_FailWithAnyNullValue() {
        assertThrows(NullValueException.class, () -> {
            List<ProductImage> list = new ArrayList<>();
            list.add(null);
            this.iProductImageService.saveAll(list);
        });
    }

    /**
     * Trường hợp thành công
     */
    @Test
    @DisplayName("saveAll_Success")
    void saveAll_Success() throws EmptyCollectionException, NullValueException {
        /* seeding data */
        List<ProductImage> productImagesOfIphone11 = this.iProductImageRepository.findAllByProductId(14);
        assertFalse(productImagesOfIphone11.isEmpty());
        Product product = new Product("test", 1000000L, "test");
        this.iProductRepository.save(product);
        List<ProductImage> list = new ArrayList<>();
        productImagesOfIphone11.forEach(productImage -> {
            ProductImage proImg = new ProductImage();
            proImg.setProduct(product);
            proImg.setImage(productImage.getImage());
            list.add(proImg);
        });
        assertFalse(list.isEmpty());

        /* testing */
        this.iProductImageService.saveAll(list);
        /* kiểm tra list sau khi thêm có được update id chưa */
        assertTrue(list.stream().allMatch(productImage -> productImage.getId() > 0));

        /* clear data sau test */
        this.iProductImageRepository.deleteAllByProductId(product.getId());
        this.iProductRepository.deleteById(product.getId());
    }

    /**
     * Trường hợp thất bại
     * -> không tìm thấy List ProductImage theo product_id
     */
    @Test
    @DisplayName("deleteAllProductImageByProductId_FailWithInvalidProductId")
    void deleteAllProductImageByProductId_FailWithInvalidProductId() {
        assertThrows(EmptyCollectionException.class, () -> {
            this.iProductImageService.deleteAllProductImageByProductId(10000);
        });
    }

    /**
     * Trường hợp thành công
     */
    @Test
    @DisplayName("deleteAllProductImageByProductId_Success")
    void deleteAllProductImageByProductId_Success() {
        assertDoesNotThrow(() -> {
            /* seeding data */
            List<ProductImage> productImagesOfIphone11 = this.iProductImageRepository.findAllByProductId(14);
            assertFalse(productImagesOfIphone11.isEmpty());
            Product product = new Product("test", 1000000L, "test");
            this.iProductRepository.save(product);
            List<ProductImage> list = new ArrayList<>();
            productImagesOfIphone11.forEach(productImage -> {
                ProductImage proImg = new ProductImage();
                proImg.setProduct(product);
                proImg.setImage(productImage.getImage());
                list.add(proImg);
            });
            assertFalse(list.isEmpty());
            this.iProductImageService.saveAll(list);
            assertTrue(list.stream().allMatch(productImage -> productImage.getId() > 0));

            /* testing */
            this.iProductImageService.deleteAllProductImageByProductId(product.getId());
            List<ProductImage> listDeleted = this.iProductImageRepository.findAllByProductId(product.getId());
            assertTrue(listDeleted.isEmpty());

            /* clear data sau test */
            this.iProductRepository.deleteById(product.getId());
        });
    }

    /**
     * Trường hợp không tìm thấy theo id -> trả về emptyList
     */
    @Test
    @DisplayName("findAllByProductId_WithInvalidId")
    void findAllByProductId_WithInvalidId() {
        List<ProductImage> list = this.iProductImageService.findAllByProductId(0);
        assertTrue(list.isEmpty());
    }

    /**
     * Trường hợp tìm thấy theo id
     */
    @Test
    @DisplayName("findAllByProductId_Success")
    void findAllByProductId_Success() {
        List<ProductImage> list = this.iProductImageService.findAllByProductId(14);
        assertFalse(list.isEmpty());
        assertEquals(6, list.size());
    }

    /**
     * Trường hợp không tìm thấy -> trả về null
     */
    @Test
    @DisplayName("findByProducIdAndMainImage_WithInvalidId")
    void findByProducIdAndMainImage_WithInvalidId() {
        ProductImage productImage = this.iProductImageService.findByProducIdAndMainImage(0, true);
        assertNull(productImage);
    }

    /**
     * Trường hợp tìm thấy
     */
    @Test
    @DisplayName("findByProducIdAndMainImage_Success")
    void findByProducIdAndMainImage_Success() {
        ProductImage productImage = this.iProductImageService.findByProducIdAndMainImage(14, true);
        assertNotNull(productImage);
        assertEquals(productImage.getProduct().getId(), 14);
    }

    /**
     * Trường hợp không tìm thấy -> trả về EmptyList
     */
    @Test
    @DisplayName("findByProductIdAndDetailImage_WithInvalidId")
    void findByProductIdAndDetailImage_WithInvalidId() {
        var productDetailImages =
                this.iProductImageService.findByProductIdAndDetailImage(0, false);
        assertTrue(productDetailImages.isEmpty());
    }

    /**
     * Trường hợp tìm thấy
     */
    @Test
    @DisplayName("findByProductIdAndDetailImage_WithInvalidId")
    void findByProductIdAndDetailImage_Success() {
        var productDetailImages =
                this.iProductImageService.findByProductIdAndDetailImage(14, false);
        assertFalse(productDetailImages.isEmpty());
        assertTrue(productDetailImages.stream().noneMatch(value -> value.getImage().isMainImage()));
        assertEquals(5, productDetailImages.size());
    }

    /**
     * Trường hợp không tìm thấy theo product_id và image_id
     * -> Exception
     */
    @Test
    @DisplayName("deleteByProductIdAndImageId_WithInvalidIds")
    void deleteByProductIdAndImageId_WithInvalidIds() {
        assertThrows(NullValueException.class, () -> {
            this.iProductImageService.deleteByProductIdAndImageId(0, 0);
        });
    }

    /**
     * Trường hợp tìm thấy
     * -> Xoá
     */
    @Test
    @DisplayName("deleteByProductIdAndImageId_Success")
    void deleteByProductIdAndImageId_Success() {
        assertDoesNotThrow(() -> {
            /* seeding data */
            Product product = new Product("test", 1000000L, "test");
            this.iProductRepository.save(product);
            ProductImage mainImageOfIphone11 = this.iProductImageRepository
                    .findByProductIdAndImage_IsMainImage(14, true);
            ProductImage productImage = new ProductImage();
            productImage.setProduct(product);
            productImage.setImage(mainImageOfIphone11.getImage());
            this.iProductImageRepository.save(productImage);

            /* Testing */
            this.iProductImageService.deleteByProductIdAndImageId(product.getId(), productImage.getImage().getId());
            ProductImage deleted = this.iProductImageRepository.findByProductIdAndImage_Id(product.getId(), productImage.getImage().getId());
            /* Kiểm tra sau khi đã xoá có tồn tại trong db ko */
            assertNull(deleted);

            /* clear data sau khi test */
            this.iProductRepository.deleteById(product.getId());
        });
    }

    /**
     * Trường hợp không tìm thấy theo product_id
     * -> Exception
     */
    @Test
    @DisplayName("deleteDetailImagesByProductId_WithInvalidProductId")
    void deleteDetailImagesByProductId_WithInvalidProductId() {
        assertThrows(EmptyCollectionException.class, () -> {
            this.iProductImageService.deleteDetailImagesByProductId(0);
        });
    }

    /**
     * Trường hợp xoá thành công
     */
    @Test
    @DisplayName("deleteDetailImagesByProductId_WithInvalidProductId")
    void deleteDetailImagesByProductId_Success() {
        assertDoesNotThrow(() -> {
            /* seeding data */
            /* tạo product */
            Product product = new Product("test", 1000000L, "test");
            this.iProductRepository.save(product);
            var productDetailImagesOfIphone11 = this.iProductImageRepository
                    .findAllByProductIdAndImage_IsMainImage(14, false);
            assertFalse(productDetailImagesOfIphone11.isEmpty());
            assertTrue(productDetailImagesOfIphone11.stream()
                    .noneMatch(productImage -> productImage.getImage().isMainImage()));
            /* tạo product_image với detail_image ( is_main_image = fasle )*/
            List<ProductImage> listDetailImages = new ArrayList<>();
            productDetailImagesOfIphone11.forEach(productImage -> {
                ProductImage proImg = new ProductImage();
                proImg.setProduct(product);
                proImg.setImage(productImage.getImage());
                listDetailImages.add(proImg);
            });
            assertFalse(listDetailImages.isEmpty());
            /* lưu data test vào db */
            this.iProductImageRepository.saveAll(listDetailImages);
            assertTrue(listDetailImages.stream().allMatch(productImage -> productImage.getId() > 0));

            /* Testing */
            this.iProductImageService.deleteDetailImagesByProductId(product.getId());
            /* Kiểm tra xem sau khi xoá có còn tồn tại trong db ko */
            List<ProductImage> deleted = this.iProductImageRepository
                    .findAllByProductIdAndImage_IsMainImage(product.getId(), false);
            assertTrue(deleted.isEmpty());

            /* clear data sau khi test */
            this.iProductRepository.deleteById(product.getId());
        });
    }

    /**
     * Trường hợp xoá product_image theo product_id và list image_id
     */
    @Test
    @DisplayName("deleteAllByProductIdAndImageIds")
    void deleteAllByProductIdAndImageIds() {
        assertDoesNotThrow(() -> {
            /* seeding data */
            List<ProductImage> productImagesOfIphone11 = this.iProductImageRepository.findAllByProductId(14);
            assertFalse(productImagesOfIphone11.isEmpty());
            /* tạo product */
            Product product = new Product("test", 1000000L, "test");
            this.iProductRepository.save(product);
            /* tạo list product_image */
            List<ProductImage> list = new ArrayList<>();
            productImagesOfIphone11.forEach(productImage -> {
                ProductImage proImg = new ProductImage();
                proImg.setProduct(product);
                proImg.setImage(productImage.getImage());
                list.add(proImg);
            });
            assertFalse(list.isEmpty());
            this.iProductImageService.saveAll(list);
            assertTrue(list.stream().allMatch(productImage -> productImage.getId() > 0));

            /* testing */
            List<Integer> imageDeleteIds = list.stream().map(productImage -> productImage.getImage().getId()).collect(Collectors.toList());
            this.iProductImageService.deleteAllByProductIdAndImageIds(product.getId(), imageDeleteIds);
            /* kiểm tra đã xoá trong db chưa */
            List<ProductImage> deleted = this.iProductImageRepository.findAllByProductIdAndImageIds(product.getId(), imageDeleteIds);
            assertTrue(deleted.isEmpty());

            /* clear data sau test */
            this.iProductRepository.deleteById(product.getId());
        });
    }

}
