package com.example.productservice.app.repository;

import com.example.productservice.app.entity.Image;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface IImageRepository extends JpaRepository<Image, Integer> {
    List<Image> findAllByProductId(int id);

    List<Image> findByProductIdAndIsMainImage(Integer id, boolean isMainImage);

    void deleteAllByProductId(Integer id);
}
