package com.example.bt.app.repository;

import com.example.bt.app.entity.ProductImage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @author : HauPV
 * repository cho table product_image
 */
public interface IProductImageRepository extends JpaRepository<ProductImage, Integer> {

    //  delete product_image theo product_id
    @Transactional(rollbackFor = {Exception.class, Throwable.class})
    @Modifying
    @Query(value = " DELETE FROM product_image WHERE product_id = ? ",
            nativeQuery = true)
    void deleteAllByProductId(int productId);

    //    Tìm list product_image theo product_id
    @Query(value = " SELECT * FROM product_image WHERE product_id = ? ",
            nativeQuery = true)
    List<ProductImage> findAllByProductId(int productId);

    //  Tìm 1 product_image là ảnh đại diện
    ProductImage findByProductIdAndImage_IsMainImage(int productId, boolean isMainImage);

    //  Tìm list product_image là các ảnh chi tiết
    List<ProductImage> findAllByProductIdAndImage_IsMainImage(int productId, boolean isNotMainImage);

    //  Xoá product_image theo product_id và image_id
    @Modifying(flushAutomatically = true)
    @Transactional
    void deleteByProductIdAndImageId(int productId, int imageId);

    //    Xoá product_image là các ảnh chi tiết
    @Modifying(flushAutomatically = true)
    @Transactional
    void deleteAllByProductIdAndImage_IsMainImage(int productId, boolean isDetailImage);

    //  Xoá product_image theo product_id và list các image_id
    @Modifying(flushAutomatically = true)
    @Transactional
    @Query(value = " DELETE FROM product_image WHERE product_id = :productId AND image_id in :imageDeleteIds ",
            nativeQuery = true)
    void deleteAllByProductIdAndImageIds(@Param("productId") int productId, @Param("imageDeleteIds") List<Integer> imageDeleteIds);

    /* tìm ProductImage theo product_id và image_id */
    ProductImage findByProductIdAndImage_Id(int productId, int imageId);

    /* tìm list product_image theo product_id và list image_id */
    @Query(value = "SELECT * FROM product_image WHERE product_id = :productId AND image_id IN :imageIds ",
            nativeQuery = true)
    List<ProductImage> findAllByProductIdAndImageIds(@Param("productId") int productId, @Param("imageIds") List<Integer> imageIds);
}
