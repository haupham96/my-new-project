package com.example.bt.app.service.product_image;

import com.example.bt.app.entity.ProductImage;
import com.example.bt.app.exception.EmptyCollectionException;
import com.example.bt.app.exception.NullValueException;
import com.example.bt.app.repository.IProductImageRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

/**
 * @author HauPV
 * Service cho product_image
 */
@Slf4j
@Service
public class ProductImageServiceImpl implements IProductImageService {

    @Autowired
    private IProductImageRepository iProductImageRepository;

    /* Lưu một tập hợp product_image */
    @Transactional(rollbackFor = {Exception.class, Throwable.class})
    @Override
    public void saveAll(List<ProductImage> productImages) throws EmptyCollectionException, NullValueException {
        log.info("class : {}", this.getClass().getSimpleName());
        log.info("method : saveAll");
        if (productImages.isEmpty()) {
            log.info("khối if productImages.isEmpty() -> throw EmptyCollectionException");
            throw new EmptyCollectionException("Empty Collection");
        } else if (productImages.stream().anyMatch(Objects::isNull)) {
            log.info("khối else-if productImages.stream().anyMatch(Objects::isNull) -> NullValueException");
            log.info("list ProductImage có phần tử null");
            throw new NullValueException("List contain null value");
        }
        this.iProductImageRepository.saveAllAndFlush(productImages);
        log.info("kết thúc method : saveAll");
    }

    /* Xoá tất cả product_image theo product_id */
    @Override
    public void deleteAllProductImageByProductId(int productId) throws EmptyCollectionException {
        log.info("class : {}", this.getClass().getSimpleName());
        log.info("method : deleteAllProductImageByProductId");
        List<ProductImage> list = this.iProductImageRepository.findAllByProductId(productId);
        if (list.isEmpty()) {
            log.info("khối if list.isEmpty() -> throw EmptyCollectionException");
            log.info("không tìm thấy product_image theo product_id {}", productId);
            throw new EmptyCollectionException("Not found ProductImageList");
        }
        this.iProductImageRepository.deleteAllByProductId(productId);
        log.info("kết thúc method : deleteAllProductImageByProductId");
    }

    /* Tìm tất cả product_image theo product_id */
    @Override
    public List<ProductImage> findAllByProductId(int productId) {
        log.info("class : {}", this.getClass().getSimpleName());
        log.info("method : findAllByProductId -> return List<ProductImage>");
        return this.iProductImageRepository.findAllByProductId(productId);
    }

    /* Tìm product_image theo product_id và is_main_image */
    @Override
    public ProductImage findByProducIdAndMainImage(int productId, boolean isMainImage) {
        log.info("class : {}", this.getClass().getSimpleName());
        log.info("method : findByProducIdAndMainImage -> return ProductImage");
        return this.iProductImageRepository.findByProductIdAndImage_IsMainImage(productId, isMainImage);
    }

    /* tìm danh sách product_image theo product_id và chỉ lấy record của các ảnh chi tiết */
    @Override
    public List<ProductImage> findByProductIdAndDetailImage(int productId, boolean isNotMainImage) {
        log.info("class : {}", this.getClass().getSimpleName());
        log.info("method : findByProductIdAndDetailImage -> return List<ProductImage> detailImages");
        return this.iProductImageRepository.findAllByProductIdAndImage_IsMainImage(productId, isNotMainImage);
    }

    /* Xoá product_image theo product_id và image_id */
    @Transactional(rollbackFor = {Exception.class, Throwable.class})
    @Override
    public void deleteByProductIdAndImageId(int productId, int imageId) throws NullValueException {
        log.info("class : {}", this.getClass().getSimpleName());
        log.info("method : deleteByProductIdAndImageId");
        ProductImage productMainImage = this.iProductImageRepository.findByProductIdAndImage_Id(productId, imageId);
        if (productMainImage == null) {
            log.info("khối if productMainImage == null -> throw NullValueException");
            log.info("không tìm thấy product_image với product_id : {} , image_id : {}", productId, imageId);
            throw new NullValueException("ProductImage Not found");
        }
        this.iProductImageRepository.deleteByProductIdAndImageId(productId, imageId);
        log.info("kết thúc method : deleteByProductIdAndImageId");

    }

    /* Xoá tất cả các record là ảnh chi tiết trong product_image */
    @Transactional(rollbackFor = {Exception.class, Throwable.class})
    @Override
    public void deleteDetailImagesByProductId(int productId) throws EmptyCollectionException {
        log.info("class : {}", this.getClass().getSimpleName());
        log.info("method : deleteDetailImagesByProductId");
        final boolean DETAIL_IMAGE = false;
        List<ProductImage> productDetailImages = this.iProductImageRepository
                .findAllByProductIdAndImage_IsMainImage(productId, DETAIL_IMAGE);
        if (productDetailImages.isEmpty()) {
            log.info("khối if productDetailImages.isEmpty() -> throw EmptyCollectionException");
            log.info("không tìm thấy List<ProductImage> với product_id = {} , is_main_image = false", productId);
            throw new EmptyCollectionException("Empty product_detail Images");
        }
        this.iProductImageRepository.deleteAllByProductIdAndImage_IsMainImage(productId, DETAIL_IMAGE);
        log.info("kết thúc method : deleteDetailImagesByProductId");
    }

    /* Xoá tất cả theo product_id và imageId */
    @Transactional(rollbackFor = {Exception.class, Throwable.class})
    @Override
    public void deleteAllByProductIdAndImageIds(int productId, List<Integer> imageDeleteIds) {
        this.iProductImageRepository.deleteAllByProductIdAndImageIds(productId, imageDeleteIds);
        log.info("kết thúc method : deleteAllByProductIdAndImageIds");
    }

}
