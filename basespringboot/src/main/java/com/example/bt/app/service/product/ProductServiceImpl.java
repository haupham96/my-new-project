package com.example.bt.app.service.product;

import com.example.bt.app.dto.ProductDTO;
import com.example.bt.app.entity.Image;
import com.example.bt.app.entity.Product;
import com.example.bt.app.entity.ProductImage;
import com.example.bt.app.entity.Promotion;
import com.example.bt.app.exception.*;
import com.example.bt.app.repository.ICartProductRepository;
import com.example.bt.app.repository.IProductRepository;
import com.example.bt.app.service.image.IImageService;
import com.example.bt.app.service.product_image.IProductImageService;
import com.example.bt.app.service.product_promotion.IProductPromotionService;
import com.example.bt.app.service.promotion.IPromotionService;
import com.example.bt.utils.Base64DecodedMultipartFile;
import com.example.bt.utils.CSVReaderUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * @author : HauPV
 * service cho product
 */
@Slf4j
@Service
public class ProductServiceImpl implements IProductService {

    @Autowired
    private IProductRepository iProductRepository;

    @Autowired
    private IImageService iImageService;

    @Autowired
    private IProductImageService iProductImageService;

    @Autowired
    private IProductPromotionService iProductPromotionService;

    @Autowired
    private IPromotionService iPromotionService;

    @Autowired
    private ICartProductRepository iCartProductRepository;

    //    Nơi lưu trữ hình ảnh của sản phẩm
    @Value("${file-store.location}")
    private String fileStoreLocation;

    //    Thêm mới sản phẩm vào db
    @Transactional(rollbackFor = {Exception.class, Throwable.class})
    @Override
    public void handleCreate(ProductDTO productDTO, Model model) {
        log.info(this.getClass().getSimpleName());
        log.info("method - handleCreate()");
        try {
            log.info("Khối try của handleCreate()");
            /* lưu product vào db */
            Product product;
            product = ProductDTO.mapToEntity(productDTO);
            this.iProductRepository.saveAndFlush(product);

            /* upload image và lưu vào db */
            productDTO.setId(product.getId());
            List<ProductImage> productImages = this.handleUploadImage(productDTO);
            productImages.forEach(productImage -> productImage.setProduct(product));
            if (!productImages.isEmpty()) {
                this.iProductImageService.saveAll(productImages);
            }
            model.addAttribute("message", "Đã thêm mới sản phẩm " + productDTO.getName());
            model.addAttribute("productDTO", new ProductDTO());
            log.info("kết thúc khối try của handleCreate()");
        } catch (IOException | EmptyFileException | EmptyCollectionException | NullValueException e) {
            log.info("Khối catch của handleCreate() -> IOException xảy ra");
            log.error(e.getMessage());
            model.addAttribute("message", "Thêm mới sản phẩm thất bại! hãy thử lại" + productDTO.getName());
            log.info("kết thúc khối catch của handleCreate()");
        }
        log.info("kết thúc method - handleCreate()");

    }

    //    Tìm sản phẩm theo tên -> chỉ sử dụng cho mục đích validate trùng tên ở DTO
    @Override
    public Product findByName(String name) {
        log.info(this.getClass().getSimpleName());
        log.info("method - findByName()");
        log.info("kết thúc method - findByName()");
        return this.iProductRepository.findByName(name);
    }

    //    Lấy ra toàn bộ danh sách sản phẩm
    @Override
    public List<ProductDTO> findAll() {
        log.info(this.getClass().getSimpleName());
        log.info("method - findAll()");
        List<Product> products = this.iProductRepository.findAll();
        log.info("kết thúc method - findAll()");
        return products.stream().map(Product::mapToDTO).collect(Collectors.toList());
    }

    //    Tìm sản phẩm theo id -> trả về kiểu DTO
    @Override
    public ProductDTO findById(Integer id) throws ProductNotFoundException {
        log.info(this.getClass().getSimpleName());
        log.info("method - findById()");
        Optional<Product> product = this.iProductRepository.findById(id);
        if (product.isPresent()) {
//            Nếu tìm thấy sản phẩm theo id -> trả về thông tin sp
            log.info("Khối if : product.isPresent()");
            ProductDTO productDTO = Product.mapToDTO(product.get());
            List<Image> images = this.iImageService.findAllByProductId(productDTO.getId());
            productDTO.setImages(images);
            log.info("Kết thúc khối if : product.isPresent()");
            log.info("kết thúc method - findById()");
            return productDTO;
        } else {
//            Không tìm thấy sp trong db theo id -> throw Exception
            log.info("khối else : product == null ");
            log.info("kết thúc method - findById() -> throw Exception vì không tìm thấy Product theo id");
            throw new ProductNotFoundException("Not Found Product With Id : " + id);
        }
    }

    //    Tìm sản phẩm theo id
    @Override
    public Product findEntityById(Integer productId) {
        log.info("class - {}", this.getClass().getSimpleName());
        log.info("method - findEntityById()");
        log.info("kết thúc method - findEntityById()");
        return this.iProductRepository.findById(productId).orElse(null);
    }

    //    Lưu đồng loạt 1 danh sách các sản phẩm ở 1 file csv vào db
    @Transactional(rollbackFor = {Exception.class, Throwable.class})
    @Override
    public void addProductsFromCSV(MultipartFile csv) throws IOException {
        if (csv.isEmpty()) {
            log.info("khối if : csv.isEmpty() => throw FileNotFoundException");
            throw new FileNotFoundException("File is Empty");
        }
        log.info("class - {}", this.getClass().getSimpleName());
        log.info("method - addProductsFromCSV()");
        List<Product> list = CSVReaderUtils.readProductsFromFileCSV(csv.getInputStream());
        for (Product product : list) {
            log.info("khối try");
            this.iProductRepository.saveOne(product);
            log.info("kết thúc khối try");
        }
        log.info("kết thúc method - addProductsFromCSV() -> lưu thành công");
    }

    //  Xử lý xoá sản phẩm trong db
    @Override
    @Transactional(rollbackFor = {Exception.class, Throwable.class})
    public void handleDelete(int productId, RedirectAttributes redirectAttributes) throws ProductNotFoundException, EmptyCollectionException {
        log.info("class {}", this.getClass().getSimpleName());
        log.info("method : handleDelete");
        Optional<Product> product = this.iProductRepository.findById(productId);
        if (product.isPresent()) {
            log.info("khối if : product.isPresent()");
            /* Tìm xem sản phẩm muốn xoá có đang nằm trong chương trình khuyến mãi ko */
            Promotion promotion = this.iPromotionService.findPromotionByProductId(product.get().getId());
            if (promotion != null) {
                /* Đang xoá sản phẩm trong chương trình khuyến mãi -> huỷ xoá */
                StringBuilder message = new StringBuilder("Sản phẩm ");
                SimpleDateFormat fmt = new SimpleDateFormat("HH:mm:ss dd/MM/yyyy");
                message.append(product.get().getName()).append(" đang nằm trong chương trình ")
                        .append(promotion.getName()).append(" đến hết ")
                        .append(fmt.format(promotion.getTo().getTime()));
                redirectAttributes.addFlashAttribute("message", message.toString());
                return;
            }
            /* Set flag delete cho image */
            List<ProductImage> productImages = this.iProductImageService.findAllByProductId(product.get().getId());
            if (!productImages.isEmpty()) {
                /* Xoá product_image của sp và set cờ xoá nếu có ảnh sản phẩm */
                List<Integer> imageDeleteIds = productImages
                        .stream()
                        .map(productImage -> productImage.getImage().getId())
                        .collect(Collectors.toList());
                this.iProductImageService.deleteAllProductImageByProductId(product.get().getId());
                this.iImageService.deleteImagesByIds(imageDeleteIds);
            }
            /* xoá cart_product theo product_id */
            this.iCartProductRepository.deleteAllByProductId(product.get().getId());
            /* xoá product theo id */
            this.iProductRepository.deleteById(productId);
            redirectAttributes.addFlashAttribute("message", "Xoá thành công sản phẩm : " + product.get().getName());
            log.info("kết thúc method : handleDelete + khối if : product.isPresent()");
        } else {
            log.info("khối else : product.isPresent()");
            log.info("kết thúc method : handleDelete => throw ProductNotFoundException");
            throw new ProductNotFoundException("Không tìm thấy sản phẩm id = " + productId);
        }
    }

    //  Xử lý chuyển dữ liệu đến trang edit
    @Override
    public ProductDTO getDataForEditPage(int productId) throws ProductNotFoundException {
        /* tìm sp theo id */
        log.info("class : {}", this.getClass().getSimpleName());
        log.info("method : getDataForEditPage");
        return this.findById(productId);
    }

    //  Xử lý chỉnh sửa sản phẩm
    @Transactional(rollbackFor = {Exception.class, Throwable.class})
    @Override
    public void handleEdit(int productId, ProductDTO productDTO)
            throws ProductNotFoundException, ConflictException, IOException,
            EmptyFileException, NullValueException, EmptyCollectionException {
        log.info("Class : {}", this.getClass().getSimpleName());
        log.info("method : handleEdit");
        final boolean MAIN_IMAGE = true;
        final boolean DETAIL_IMAGE = false;
        if (productId != productDTO.getId()) {
//          Nếu id trong form và trên URL khác nhau -> bị cố ý sửa id trên url -> trả về Exception
            log.info("khối if : productId != productDTO.getId() -> throw ConflictException");
            throw new ConflictException("Id Conflict : " + productDTO.getId() + " != " + productId);
        }
        Optional<Product> product = this.iProductRepository.findById(productDTO.getId());
        if (product.isEmpty()) {
//          Nếu không tìm thấy sp theo id -> trả về Exception
            log.info("khối if : product.isEmpty() -> throw ProductNotFoundException");
            throw new ProductNotFoundException("Không tìm thấy sản phẩm id = : " + product);
        }
        Product editProduct = ProductDTO.mapToEntity(productDTO);
        this.iProductRepository.save(editProduct);

//      Lấy ra các ảnh cũ trong db
        ProductImage oldProductMainImage = this.iProductImageService
                .findByProducIdAndMainImage(productId, MAIN_IMAGE);
        List<ProductImage> oldProductDetailImages = this.iProductImageService
                .findByProductIdAndDetailImage(productId, DETAIL_IMAGE);

        final boolean IS_UPDATE_MAIN_IMAGE = !(productDTO.getMainImage() == null)
                && !productDTO.getMainImage().isEmpty();
        final boolean IS_UPDATE_DETAIL_IMAGE = productDTO.getDetailImages() != null
                && productDTO.getDetailImages().length >= 1
                && !productDTO.getDetailImages()[0].isEmpty();

//      Danh sách lưu các ảnh sẽ được sửa đổi
        List<ProductImage> productImagesUpdate = this.handleUploadImage(productDTO);
//      id ảnh sẽ bị update và xoá đi trong table image
        List<Integer> imageDeleteIds = new ArrayList<>();
//          Nếu có ảnh đại diện tồn tại trong db  -> update lại ảnh đó
        log.info("khối if : oldProductMainImage != null");
        if (IS_UPDATE_MAIN_IMAGE && oldProductMainImage != null) {
//          Nếu có update ảnh đại diện
//          -> xoá record product_image và set flag delete cho main image
            log.info("khối if : IS_UPDATE_MAIN_IMAGE");
            /* Xoá ảnh đại diện */
            this.iProductImageService.deleteByProductIdAndImageId(productId, oldProductMainImage.getImage().getId());
            imageDeleteIds.add(oldProductMainImage.getImage().getId());
            log.info("kết thúc khối if : IS_UPDATE_MAIN_IMAGE");
        }

//          Nếu đã có ảnh chi tiết trong db -> thay đổi các ảnh cũ
        log.info("khối if : !oldProductDetailImages.isEmpty()");
        if (IS_UPDATE_DETAIL_IMAGE) {
//          Nếu update ảnh chi tiết
            if (!oldProductDetailImages.isEmpty()) {
                /* Nếu có ảnh cũ -> xoá ảnh cũ và update ảnh mới */
                log.info("khối if : IS_UPDATE_DETAIL_IMAGE");
                this.iProductImageService.deleteDetailImagesByProductId(productId);
                List<Integer> oldDetailImageIds = oldProductDetailImages
                        .stream()
                        .map(productImage -> productImage.getImage().getId())
                        .collect(Collectors.toList());
                /* xoá record ảnh cũ trong product_image */
                this.iProductImageService.deleteAllByProductIdAndImageIds(editProduct.getId(), oldDetailImageIds);
                imageDeleteIds.addAll(imageDeleteIds.size(), oldDetailImageIds);
                log.info("kết thúc khối if : IS_UPDATE_DETAIL_IMAGE");
            }
            log.info("kết thúc khối if : !oldProductDetailImages.isEmpty()");
        }

//      set deleteFlag = true cho chức năng xoá image
        this.iImageService.deleteImagesByIds(imageDeleteIds);

        /* Lưu các product_image mới trong db */
        for (ProductImage productImage : productImagesUpdate) {
            productImage.setProduct(editProduct);
        }
        if (!productImagesUpdate.isEmpty()) {
            this.iProductImageService.saveAll(productImagesUpdate);
        }
        log.info("kết thúc method : handleEdit");
    }

    //  Xử lý upload ảnh
    public List<ProductImage> handleUploadImage(ProductDTO productDTO) throws EmptyFileException {
        log.info("Class : {}", this.getClass().getSimpleName());
        log.info("method : handleUploadImage");
        final boolean IS_UPLOAD_MAIN_IMAGE = productDTO.getMainImage() != null
                && !productDTO.getMainImage().isEmpty();
        final boolean IS_UPLOAD_DETAIL_IMAGE = productDTO.getDetailImages() != null
                && productDTO.getDetailImages().length > 0
                && !productDTO.getDetailImages()[0].isEmpty();
        final boolean IS_MAIN_IMAGE = true;
        List<ProductImage> productImages = new ArrayList<>();
        if (IS_UPLOAD_MAIN_IMAGE) {
//          Nếu như có sửa ảnh đại diện -> upload lại file
            log.info("khối if : productDTO.getMainImage() != null");
            Image mainImage = Base64DecodedMultipartFile
                    .generateFile(productDTO.getMainImage(), this.fileStoreLocation,
                            productDTO.getId(), IS_MAIN_IMAGE);
            ProductImage productImage = new ProductImage();
            this.iImageService.save(mainImage);
            productImage.setImage(mainImage);
            productImages.add(productImage);
            log.info("kết thúc khối if : productDTO.getMainImage() != null");
        }
        if (IS_UPLOAD_DETAIL_IMAGE) {
//          Nếu có sửa ảnh chi tiết -> upload ảnh mới .
            log.info("khối if : productDTO.getDetailImages() != null && productDTO.getDetailImages().length > 0");
            for (MultipartFile image : productDTO.getDetailImages()) {
                Image imageDetail = Base64DecodedMultipartFile
                        .generateFile(image, this.fileStoreLocation,
                                productDTO.getId(), !IS_MAIN_IMAGE);
                this.iImageService.save(imageDetail);
                ProductImage productImage = new ProductImage();
                productImage.setImage(imageDetail);
                productImages.add(productImage);
            }

            log.info("kết thúc khối if : productDTO.getDetailImages() != null && productDTO.getDetailImages().length > 0");
        }
        log.info("kết thúc method : handleUploadImage");
        return productImages;
    }

}
