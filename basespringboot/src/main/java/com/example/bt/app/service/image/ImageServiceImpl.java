package com.example.bt.app.service.image;

import com.example.bt.app.entity.Image;
import com.example.bt.app.repository.IImageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ImageServiceImpl implements IImageService {

    @Autowired
    private IImageRepository iImageRepository;

    @Override
    public void deleteImages(List<Image> images) {
        if (!images.isEmpty()) {
            iImageRepository.saveAll(images.stream().map(image -> {
                image.setDelete(true);
                return image;
            }).collect(Collectors.toList()));
        }
    }

    @Transactional(rollbackFor = {Exception.class, Throwable.class})
    @Override
    public void save(Image image) {
        this.iImageRepository.save(image);
    }

    @Override
    public List<Image> findAllByProductId(int productId) {
        return this.iImageRepository.findAllByProductId(productId);
    }

    @Override
    public List<Image> findMainImageByProductId(int productId) {
        return this.iImageRepository.findMainImageByProductId(productId);
    }

    @Transactional(rollbackFor = {Exception.class, Throwable.class})
    @Override
    public void deleteImagesByIds(List<Integer> imagesDeleteId) {
        this.iImageRepository.setDeleteFlagOn(imagesDeleteId);
    }
}
