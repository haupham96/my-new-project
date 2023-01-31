package com.example.productservice.app.service.product;

import com.example.productservice.app.dto.request.ProductDTO;
import com.example.productservice.app.dto.response.ImageResponse;
import com.example.productservice.app.dto.response.ProductPrice;
import com.example.productservice.app.dto.response.ProductResponse;
import com.example.productservice.app.entity.Image;
import com.example.productservice.app.entity.Product;
import com.example.productservice.app.exception.ConflictException;
import com.example.productservice.app.exception.ProductExistedException;
import com.example.productservice.app.exception.ProductNotFoundException;
import com.example.productservice.app.repository.IImageRepository;
import com.example.productservice.app.repository.IProductRepository;
import com.example.productservice.common.MultiPartFileImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements IProductService {
    private final IProductRepository iProductRepository;
    private final IImageRepository iImageRepository;
    private final boolean IS_MAIN_IMAGE = true;
    private final boolean IS_DETAIL_IMAGE = false;

    @Value("${file.store-location}")
    private String fileStoreLocation;

    @Value("${path.product.static-image}")
    private String productImgStaticPath;

    @Override
    public List<ProductResponse> findAll() {
        return this.iProductRepository.findAll()
                .stream()
                .map(product -> Product.mapToProductResponse(product, this.getListImageByProductId(product.getId())))
                .collect(Collectors.toList());
    }

    public List<ImageResponse> getListImageByProductId(int productId) {
        return this.iImageRepository.findAllByProductId(productId)
                .stream().map(image -> Image.mapToImageResponse(image, productImgStaticPath))
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void createProduct(ProductDTO productDTO) throws ProductExistedException, FileNotFoundException {
        Product productCheck = this.iProductRepository.findByName(productDTO.getName());
        if (productCheck != null) {
            throw new ProductExistedException("duplicated product name : " + productDTO.getName());
        }
        Product product = ProductDTO.mapToEntity(productDTO);
        this.iProductRepository.save(product);
        List<Image> images = this.handleUploadImage(product, productDTO);
        if (!images.isEmpty()) {
            /* Nếu có upload ảnh -> save vào db */
            this.iImageRepository.saveAllAndFlush(images);
        }
    }

    @Override
    @Transactional
    public ProductResponse editProduct(int productId, ProductDTO productDTO)
            throws ConflictException, ProductNotFoundException, ProductExistedException, IOException {
        if (productId != productDTO.getId()) {
            throw new ConflictException("Conflict product id : " + productId + " & " + productDTO.getId());
        }
        Optional<Product> productOptional = this.iProductRepository.findById(productId);
        if (productOptional.isEmpty()) {
            throw new ProductNotFoundException("Not found product ");
        }
        Product productOld = productOptional.get();
        if (!productOld.getName().equals(productDTO.getName())) {
            /* Trường hợp có đổi tên -> check tên vừa đồi có bị trùng ko */
            Product findByName = this.iProductRepository.findByName(productDTO.getName());
            if (findByName != null) {
                throw new ProductExistedException(" product - " + productDTO.getName() + " already existed ! ");
            }
        }
        Product product = ProductDTO.mapToEntity(productDTO);
        this.iProductRepository.save(product);
        List<Image> images = this.handleUploadImage(product, productDTO);
        boolean isEditMainImage = images.stream().anyMatch(Image::isMainImage);
        boolean isEditDetailImage = images.stream().anyMatch(image -> !image.isMainImage());
        if (isEditMainImage) {
            List<Image> mainImageOld = this.iImageRepository
                    .findByProductIdAndIsMainImage(product.getId(), IS_MAIN_IMAGE);
            if (!mainImageOld.isEmpty()) {
                /* có ảnh trong db -> edit lại ảnh này trong list */
                Files.deleteIfExists(Paths.get(fileStoreLocation
                        + mainImageOld.get(0).getStorePath()
                        + mainImageOld.get(0).getOriginalFileName()));
                iImageRepository.delete(mainImageOld.get(0));
            }
        }
        if (isEditDetailImage) {
            List<Image> detailImagesOld = this.iImageRepository
                    .findByProductIdAndIsMainImage(product.getId(), IS_DETAIL_IMAGE);
            if (!detailImagesOld.isEmpty()) {
                for (Image detailImage : detailImagesOld) {
                    /* xoá các ảnh cũ */
                    Files.deleteIfExists(Paths.get(fileStoreLocation
                            + detailImage.getStorePath()
                            + detailImage.getOriginalFileName()));
                }
                iImageRepository.deleteAll(detailImagesOld);
            }
        }
        this.iImageRepository.saveAllAndFlush(images);
        return this.findById(product.getId());
    }

    @Override
    @Transactional
    public void deleteById(Integer productId) throws ProductNotFoundException, IOException {
        Product product = this.iProductRepository.findById(productId).orElse(null);
        if (product == null) {
            throw new ProductNotFoundException("Not found Product " + productId);
        }
        List<Image> imagesDelete = this.iImageRepository.findAllByProductId(product.getId());
        if (!imagesDelete.isEmpty()) {
            String deleteDirectory = fileStoreLocation + imagesDelete.get(0).getStorePath();
            MultiPartFileImpl.deleteDirectory(imagesDelete, fileStoreLocation);
            Files.deleteIfExists(Paths.get(deleteDirectory));
        }
        this.iImageRepository.deleteAllByProductId(product.getId());
        this.iProductRepository.delete(product);
    }

    @Override
    public ProductResponse findByName(String productName) throws ProductNotFoundException {
        Product product = this.iProductRepository.findByName(productName);
        if (product == null) {
            throw new ProductNotFoundException("Not found product " + productName);
        }
        return ProductResponse.builder()
                .id(product.getId())
                .name(product.getName())
                .description(product.getDescription())
                .images(
                        this.iImageRepository
                                .findAllByProductId(product.getId())
                                .stream()
                                .map(img -> Image.mapToImageResponse(img, productImgStaticPath))
                                .toList())
                .build();
    }

    @Override
    public List<ProductPrice> findAllProductPrices(List<String> productNames) {
        return this.iProductRepository.findAllProductPriceByProductName(productNames);
    }

    public ProductResponse findById(Integer productId) throws ProductNotFoundException {
        Product product = this.iProductRepository.findById(productId).orElse(null);
        if (product == null) {
            throw new ProductNotFoundException("Not found product " + productId);
        }
        return ProductResponse.builder()
                .id(product.getId())
                .name(product.getName())
                .description(product.getDescription())
                .images(
                        this.iImageRepository
                                .findAllByProductId(product.getId())
                                .stream()
                                .map(img -> Image.mapToImageResponse(img, productImgStaticPath))
                                .toList())
                .build();
    }

    private List<Image> handleUploadImage(Product product, ProductDTO productDTO) throws FileNotFoundException {
        boolean isUploadMainImage = productDTO.getMainImage() != null &&
                !productDTO.getMainImage().isEmpty();
        boolean isUploadDetailImages = productDTO.getDetailImages() != null &&
                productDTO.getDetailImages().length >= 1 &&
                !productDTO.getDetailImages()[0].isEmpty();
        List<Image> images = new ArrayList<>();
        if (isUploadMainImage) {
            /* nếu có ảnh đại diện -> upload và lưu vào db */
            Image mainImage = MultiPartFileImpl
                    .generateFile(productDTO.getMainImage(), fileStoreLocation, product.getId(), IS_MAIN_IMAGE);
            mainImage.setProduct(product);
            images.add(mainImage);
        }
        if (isUploadDetailImages) {
            for (MultipartFile img : productDTO.getDetailImages()) {
                if (img != null && !img.isEmpty()) {
                    Image detailImage = MultiPartFileImpl
                            .generateFile(img, this.fileStoreLocation, product.getId(), IS_DETAIL_IMAGE);
                    detailImage.setProduct(product);
                    images.add(detailImage);
                }
            }
        }
        return images;
    }
}
