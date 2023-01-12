package com.example.bt.app.service.product;

import com.example.bt.app.dto.ProductDTO;
import com.example.bt.app.entity.*;
import com.example.bt.app.exception.ConflictException;
import com.example.bt.app.exception.ProductNotFoundException;
import com.example.bt.app.repository.*;
import org.apache.tomcat.util.http.fileupload.FileUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ClassPathResource;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.ui.Model;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @author HauPV
 * JUnit Test cho ProductService
 * => OK
 */
@SpringBootTest
class ProductServiceTest {

    @Autowired
    private ICartProductRepository iCartProductRepository;
    @Autowired
    private ICartRepository iCartRepository;
    @Autowired
    private IProductService iProductService;
    @Autowired
    private IProductRepository iProductRepository;
    @Autowired
    private IProductImageRepository iProductImageRepository;
    @Autowired
    private IImageRepository iImageRepository;
    @Mock
    Model model;
    @Mock
    RedirectAttributes redirectAttributes;

    MultipartFile mainImage;
    MultipartFile[] detailImages;
    String fileStoreLocation = "D:\\training_beetech\\PhamVanHau\\basespringboot\\src\\\\main\\resources\\static\\image\\";

    /**
     * Set up ảnh cho chức năng thêm mới và edit
     * */
    @BeforeEach
    void setUp() throws IOException {
        mainImage = new MockMultipartFile(
                "test-1.jpg", Files.readAllBytes(Paths.get("D:\\image\\test\\test-1.jpg")));
        detailImages = new MultipartFile[]{
                new MockMultipartFile("test-2.jpg", Files.readAllBytes(Paths.get("D:\\image\\test\\test-2.jpg"))),
                new MockMultipartFile("test-3.jpg", Files.readAllBytes(Paths.get("D:\\image\\test\\test-3.jpg"))),
                new MockMultipartFile("test-4.jpg", Files.readAllBytes(Paths.get("D:\\image\\test\\test-4.jpg"))),
                new MockMultipartFile("test-5.jpg", Files.readAllBytes(Paths.get("D:\\image\\test\\test-5.jpg"))),
                new MockMultipartFile("test-6.jpg", Files.readAllBytes(Paths.get("D:\\image\\test\\test-6.jpg")))
        };
    }

    /**
     * Trường hợp thêm mới thành công với các dữ liệu phù hợp
     */
    @Test
    @DisplayName("handleCreate_Success")
    void handleCreate_Success() throws IOException {
//      Giả định 1 ProductDTO hợp lệ
        ProductDTO productDTO = new ProductDTO();
        productDTO.setName("test");
        productDTO.setPrice(10000000L);
        productDTO.setDescription("test");
        productDTO.setIProductService(iProductService);
        productDTO.setMainImage(mainImage);
        productDTO.setDetailImages(detailImages);

        /* ko xảy ra Exception -> thành công */
        assertDoesNotThrow(() -> {
            this.iProductService.handleCreate(productDTO, model);
        });
        Product product = this.iProductService.findByName(productDTO.getName());
        assertNotNull(product);
        assertEquals(productDTO.getName(), product.getName());

        List<Image> images = this.iImageRepository.findAllByProductId(product.getId());
        assertFalse(images.isEmpty());
//      Clear data sau khi test xong
        /* xoá ảnh trong thư mục */
        String folderDelete = fileStoreLocation + images.get(0).getStorePath();
        images.forEach(image -> {
            File file = new File(fileStoreLocation + image.getStorePath() + image.getOriginalFileName());
            boolean isDelted = file.delete();
            assertTrue(isDelted);
        });
        FileUtils.deleteDirectory(new File(folderDelete));
        this.iProductImageRepository.deleteAllByProductId(product.getId());
        this.iImageRepository.deleteAll(images);
        this.iProductRepository.deleteById(product.getId());
    }

    /**
     * Trường hợp thất bại khi bị trùng tên Product trong db
     */
    @Test
    @DisplayName("handleCreate_Fail")
    void handleCreate_FailWithDuplicateName() {
        ProductDTO productDTO = new ProductDTO();
//      Tên sản phẩm : test đã tồn tại trong database
        productDTO.setName("iPhone 11");
        productDTO.setPrice(10000000L);
        productDTO.setDescription("Chống nước 100%");
        productDTO.setIProductService(iProductService);

        assertThrows(Exception.class, () -> {
            this.iProductService.handleCreate(productDTO, model);
        });
    }

    /**
     * Trường hợp tìm theo tên sản phẩm đã tồn tại trong db thành công
     */
    @Test
    @DisplayName("findByName_Success")
    void findByName_Success() {
        String existedProductName = "iPhone 11";
        Product product = this.iProductService.findByName(existedProductName);
        assertNotNull(product);
        assertEquals(existedProductName, product.getName());
    }

    /**
     * Trường hợp tìm theo tên ko có trong db -> trả về null
     */
    @Test
    @DisplayName("findByName_FailWithInValidName")
    void findByName_FailWithInValidName() {
        String notExisteProductName = "Mercedes";
        Product product = this.iProductService.findByName(notExisteProductName);
        assertNull(product);
    }

    /**
     * Trường hợp tìm tất cả record trong db
     */
    @Test
    @DisplayName("findAll")
    void findAll() {
        List<ProductDTO> list = this.iProductService.findAll();
        assertTrue(list.size() > 0);
    }

    /**
     * Trường hợp tìm Product theo id hợp lệ
     */
    @Test
    @DisplayName("findEntityById_Success")
    void findEntityById_Success() {
        int existingProductId = 14;
        Product product = this.iProductService.findEntityById(existingProductId);

        assertNotNull(product);
        assertEquals(existingProductId, product.getId());
    }

    /**
     * Tìm Product theo id không hợp lệ -> trả về null
     */
    @Test
    @DisplayName("findEntityById_FailWithInvalidId")
    void findEntityById_FailWithInvalidId() {
        int invalidProductId = 123;
        Product product = this.iProductService.findEntityById(invalidProductId);
        assertNull(product);
    }

    /**
     * Tìm ProductDTO theo id không hợp lệ
     * -> Exception
     */
    @Test
    @DisplayName("findById_FailWithInvalidId")
    void findById_FailWithInvalidId() {
        int invalidProductId = 123;
        assertThrows(ProductNotFoundException.class, () -> {
            ProductDTO productDTO = this.iProductService.findById(invalidProductId);
        });
    }

    /**
     * Tìm ProductDTO theo id hợp lệ
     */
    @Test
    @DisplayName("findById_FailWithInvalidId")
    void findById_Success() {
        int validProductId = 14;
        assertDoesNotThrow(() -> {
            ProductDTO productDTO = this.iProductService.findById(validProductId);
            assertNotNull(productDTO);
            assertEquals("iPhone 11", productDTO.getName());
        });
    }

    /**
     * Trường hợp thêm đồng loạt nhiều sản phẩm trong db thành công
     */
    @Test
    @DisplayName("addProductsFromCSV_Success")
    void addProductsFromCSV_Success() throws Exception {
        ClassPathResource csvResource = new ClassPathResource("/static/csv/products.csv");
        List<ProductDTO> beforeImportFromCSV = this.iProductService.findAll();
        MultipartFile csv = new MockMultipartFile("products.csv", csvResource.getInputStream());
        this.iProductService.addProductsFromCSV(csv);
        List<ProductDTO> afterImportFormCSV = this.iProductService.findAll();
        assertTrue(afterImportFormCSV.size() > beforeImportFromCSV.size());

        /* clear data sau khi test */
        afterImportFormCSV.removeAll(beforeImportFromCSV);
        List<Integer> productIds = afterImportFormCSV.stream().map(ProductDTO::getId).collect(Collectors.toList());
        this.iProductRepository.deleteAllById(productIds);
    }

    /**
     * Trường hợp thêm đồng loạt nhiều sản phẩm trong db thất bại
     * -> FileNotFoundException
     */
    @Test
    @DisplayName("addProductsFromCSV")
    void addProductsFromCSV_FailWithFileNotFoundException() {
        MultipartFile csv = new MockMultipartFile("products.csv", new byte[0]);
        assertThrows(FileNotFoundException.class, () -> {
            this.iProductService.addProductsFromCSV(csv);
        });
    }

    /**
     * Trường hợp xoá sản phẩm với id không hợp lệ
     * -> Exception
     */
    @Test
    @DisplayName("handleDelete_WithInValidProductId")
    void handleDelete_WithInValidProductId() {
        int invalidId = 10000;
        assertThrows(ProductNotFoundException.class, () -> {
            this.iProductService.handleDelete(invalidId, redirectAttributes);
        });
    }

    /**
     * Trường hợp xoá sản phẩm với id hợp lệ
     * -> Exception
     */
    @Test
    @DisplayName("handleDelete_Success")
    void handleDelete_Success() throws IOException {
        /* setup data */
        ProductDTO productDTO = new ProductDTO();
        productDTO.setName("test");
        productDTO.setPrice(10000000L);
        productDTO.setDescription("test");
        productDTO.setIProductService(iProductService);
        productDTO.setMainImage(mainImage);
        productDTO.setDetailImages(detailImages);
        assertDoesNotThrow(() -> {
            this.iProductService.handleCreate(productDTO, model);
        });
        Product product = this.iProductService.findByName(productDTO.getName());
        assertNotNull(product);

        /* setup cart_product */
        Cart cart = new Cart();
        this.iCartRepository.save(cart);
        CartProduct cartProduct = new CartProduct();
        cartProduct.setCart(cart);
        cartProduct.setProduct(product);
        cartProduct.setQuantity(2);
        this.iCartProductRepository.save(cartProduct);

        /* lưu list img sẽ xoá sau khi test xong */
        List<Image> imagesDelete = this.iImageRepository.findAllByProductId(product.getId());
        assertFalse(imagesDelete.isEmpty());

        /* === === === Testing === === === */
        assertDoesNotThrow(() -> {
            this.iProductService.handleDelete(product.getId(), redirectAttributes);
        });
        Product productDeleted = this.iProductRepository.findById(product.getId()).orElse(null);
        /* sau khi xoá xong -> ko tìm thấy */
        assertNull(productDeleted);
        List<ProductImage> productImages = this.iProductImageRepository.findAllByProductId(product.getId());
        /* ko tìm thấy product_image theo pỏduct_id */
        assertTrue(productImages.isEmpty());
        CartProduct cartProductDeleted = this.iCartProductRepository.findById(cartProduct.getId()).orElse(null);
        /* ko tìm thấy cart_product có tham chiếu đến product đã được xoá */
        assertNull(cartProductDeleted);

//      Clear data sau khi test xong
        /* xoá thư mục ảnh */
        String folderDelete = fileStoreLocation + imagesDelete.get(0).getStorePath();
        imagesDelete.forEach(image -> {
            File file = new File(fileStoreLocation + image.getStorePath() + image.getOriginalFileName());
            boolean isDeleted = file.delete();
            assertTrue(isDeleted);
        });
        FileUtils.deleteDirectory(new File(folderDelete));

        /* xoá các dữ liệu đã gán */
        this.iProductImageRepository.deleteAllByProductId(product.getId());
        this.iImageRepository.deleteAll(imagesDelete);
        this.iCartRepository.deleteById(cart.getCartId());
    }

    /**
     * Trường hợp id ko tồn tại
     * -> Exception
     */
    @Test
    @DisplayName("getDataForEditPage_WithInvalidId")
    void getDataForEditPage_WithInvalidId() {
        int invalidId = 10000;
        assertThrows(ProductNotFoundException.class, () -> {
            this.iProductService.getDataForEditPage(invalidId);
        });
    }

    /**
     * Trường hợp id hợp lệ
     */
    @Test
    @DisplayName("getDataForEditPage_WithInvalidId")
    void getDataForEditPage_Success() {
        int invalidId = 14;
        assertDoesNotThrow(() -> {
            ProductDTO productDTO = this.iProductService.getDataForEditPage(invalidId);
            assertNotNull(productDTO);
            assertEquals("iPhone 11", productDTO.getName());
        });
    }

    /**
     * Trường hợp thất bại do id trên Url và id của ProductDTO khác nhau
     * -> ConflictException
     */
    @Test
    @DisplayName("handleEdit_FailWithConflictException")
    void handleEdit_FailWithConflictException() {
        ProductDTO productDTO = new ProductDTO();
        productDTO.setId(23);
        productDTO.setName("test");
        productDTO.setPrice(10000000L);
        productDTO.setDescription("test");
        assertThrows(ConflictException.class, () -> {
            this.iProductService.handleEdit(24, productDTO);
        });
    }

    /**
     * Trường hợp thất bại do id không hợp lệ
     * -> Exception
     */
    @Test
    @DisplayName("handleEdit_FailWithProductNotFoundException")
    void handleEdit_FailWithProductNotFoundException() {
        ProductDTO productDTO = new ProductDTO();
        productDTO.setId(10000);
        productDTO.setName("test");
        productDTO.setPrice(10000000L);
        productDTO.setDescription("test");
        assertThrows(ProductNotFoundException.class, () -> {
            this.iProductService.handleEdit(10000, productDTO);
        });
    }

    /**
     * Trường hợp thành công và không chỉnh sửa ảnh
     */
    @Test
    @DisplayName("handleEdit_SuccessWithoutUploadNewImage")
    void handleEdit_SuccessWithoutUploadNewImage() {
        /* Setup Product */
        Product product = new Product();
        product.setName("test");
        product.setPrice(10000000L);
        product.setDescription("test");
        this.iProductRepository.save(product);

        ProductDTO productDTO = Product.mapToDTO(product);
        productDTO.setName("test edit");
        product.setPrice(20000000L);
        product.setDescription("test edit");
        assertDoesNotThrow(() -> {
            this.iProductService.handleEdit(productDTO.getId(), productDTO);
        });
        Product productEdited = this.iProductRepository.findById(product.getId()).orElse(null);
        /* đối chiếu các giá trị sau khi đã eidt xong */
        assertNotNull(productEdited);
        assertEquals(productDTO.getName(), productEdited.getName());
        assertEquals(productDTO.getPrice(), productEdited.getPrice());
        assertEquals(productDTO.getDescription(), productEdited.getDescription());

        /* clear data sau khi test xong */
        this.iProductRepository.deleteById(product.getId());
    }

    /**
     * Trường hợp chỉnh sửa thành công và upload ảnh mới
     */
    @Test
    @DisplayName("handleEdit_SuccessWithUploadNewImage")
    void handleEdit_SuccessWithUploadNewImage() throws IOException {
        /* seeding data */
        ProductDTO productDTO = new ProductDTO();
        productDTO.setName("test");
        productDTO.setPrice(10000000L);
        productDTO.setDescription("test");
        productDTO.setIProductService(iProductService);
        productDTO.setMainImage(mainImage);
        productDTO.setDetailImages(detailImages);
        assertDoesNotThrow(() -> {
            this.iProductService.handleCreate(productDTO, model);
        });
        Product product = this.iProductService.findByName(productDTO.getName());
        assertNotNull(product);

        /* sau khi thêm product -> edit */
        List<Image> deleteImages = this.iImageRepository.findAllByProductId(product.getId());
        assertFalse(deleteImages.isEmpty());
        /* giả sử update ảnh chính và ảnh đại diện */
        productDTO.setId(product.getId());
        productDTO.setMainImage(mainImage);
        productDTO.setDetailImages(detailImages);
        assertDoesNotThrow(() -> {
            this.iProductService.handleEdit(productDTO.getId(), productDTO);
            /* danh sách id của các ảnh cũ */
            List<Integer> imageDeletedIds = deleteImages.stream().map(Image::getId).collect(Collectors.toList());
            /* kiểm tra các ảnh cũ đã set deleteFlag = true chưa */
            List<Image> imagesDeleted = this.iImageRepository.findAllById(imageDeletedIds);
            assertTrue(imagesDeleted.stream().allMatch(Image::isDelete));

            /* kiểm tra các ảnh mới thêm vào */
            List<Image> imagesNew = this.iImageRepository.findAllByProductId(productDTO.getId());
            assertFalse(imagesNew.stream().allMatch(Image::isDelete));
            assertEquals(6, imagesNew.size());

            /* Sau khi kiểm tra xong thêm vào danh sách xoá */
            deleteImages.addAll(imagesNew);
        });

//      Clear data sau khi test xong
        /* xoá ảnh trong thư mục */
        String folderDelete = fileStoreLocation + deleteImages.get(0).getStorePath();
        deleteImages.forEach(image -> {
            File file = new File(fileStoreLocation + image.getStorePath() + image.getOriginalFileName());
            boolean isDeleted = file.delete();
            assertTrue(isDeleted);
        });
        FileUtils.deleteDirectory(new File(folderDelete));
        this.iProductImageRepository.deleteAllByProductId(product.getId());
        this.iImageRepository.deleteAll(deleteImages);
        this.iProductRepository.deleteById(product.getId());
    }

}
