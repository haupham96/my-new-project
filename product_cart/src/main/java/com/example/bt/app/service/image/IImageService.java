package com.example.bt.app.service.image;

import com.example.bt.app.entity.Image;

import java.util.List;

public interface IImageService {
    void deleteImages(List<Image> images);

    void save(Image imageDetail);

    List<Image> findAllByProductId(int id);

    List<Image> findMainImageByProductId(int productId);

    void deleteImagesByIds(List<Integer> imagesDeleteId);
}
