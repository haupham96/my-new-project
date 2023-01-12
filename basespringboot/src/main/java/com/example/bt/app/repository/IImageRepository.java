package com.example.bt.app.repository;

import com.example.bt.app.entity.Image;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @author HauPV
 * repository cho table image
 * */
public interface IImageRepository extends JpaRepository<Image, Integer> {

    //  Tìm tất cả image theo product_id
    @Query(value = " SELECT image.id , created_at , file_length , is_delete , is_main_image , original_file_name , store_path , updated_at " +
            " FROM image " +
            " JOIN product_image " +
            " ON image.id = product_image.image_id " +
            " WHERE product_image.product_id = ? " +
            " AND image.is_delete = false ",
            nativeQuery = true)
    List<Image> findAllByProductId(int productId);

    // Tìm ảnh đại diện theo product_id
    @Query(value = " SELECT image.id , created_at , file_length , is_delete , is_main_image , original_file_name , store_path , updated_at " +
            " FROM image " +
            " JOIN product_image " +
            " ON image.id = product_image.image_id " +
            " WHERE product_image.product_id = ? " +
            " AND image.is_delete = false AND image.is_main_image = true ",
            nativeQuery = true)
    List<Image> findMainImageByProductId(int productId);

    //  set delete flag của image = true -> đã xoá
    @Modifying
    @Transactional
    @Query(value = " UPDATE image SET is_delete = true WHERE id IN :ids "
            , nativeQuery = true)
    void setDeleteFlagOn(@Param("ids") List<Integer> imagesDeleteId);
}
