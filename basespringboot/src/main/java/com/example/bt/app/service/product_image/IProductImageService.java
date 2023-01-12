package com.example.bt.app.service.product_image;

import com.example.bt.app.entity.ProductImage;
import com.example.bt.app.exception.EmptyCollectionException;
import com.example.bt.app.exception.NullValueException;

import java.util.List;

/**
 * @author HauPV
 * Service cho ProductImage
 * */
public interface IProductImageService {
    void saveAll(List<ProductImage> productImages) throws EmptyCollectionException, NullValueException;

    void deleteAllProductImageByProductId(int productId) throws EmptyCollectionException;

    List<ProductImage> findAllByProductId(int productId);

    ProductImage findByProducIdAndMainImage(int productId, boolean main_image);

    List<ProductImage> findByProductIdAndDetailImage(int productId, boolean not_main_image);

    void deleteByProductIdAndImageId(int productId, int id) throws NullValueException;

    void deleteDetailImagesByProductId(int productId) throws EmptyCollectionException;

    void deleteAllByProductIdAndImageIds(int productId, List<Integer> imageDeleteIds);

}
