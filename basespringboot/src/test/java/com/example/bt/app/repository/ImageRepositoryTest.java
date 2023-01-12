package com.example.bt.app.repository;

import com.example.bt.app.entity.Image;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @author HauPV
 * JUnit Test cho IImageRepository
 */
@SpringBootTest
public class ImageRepositoryTest {

    @Autowired
    private IImageRepository iImageRepository;

    /**
     * Trường hợp tìm theo product_id ko hợp lệ
     * -> return EmptyList
     */
    @Test
    void findAllByProductId_WithInvalidProductId() {
        List<Image> list = this.iImageRepository.findAllByProductId(0);
        assert list.isEmpty() : "list is not empty";
    }

    /**
     * Trường hợp tìm list image theo product_id thành công
     */
    @Test
    void findAllByProductId_Success() {
        List<Image> list = this.iImageRepository.findAllByProductId(14);
        assertFalse(list.isEmpty());
        assertEquals(6, list.size());
        assertTrue(list.stream().noneMatch(Image::isDelete));
    }

    /**
     * Trường hợp tìm ảnh đại diện với product_id ko hợp lệ
     * -> return EmptyList
     */
    @Test
    void findMainImageByProductId_WithInvalidProductId() {
        List<Image> mainImage = this.iImageRepository.findMainImageByProductId(0);
        assertTrue(mainImage.isEmpty());
    }

    /**
     * Trường hợp tìm ảnh đại diện với product_id hợp lệ
     */
    @Test
    void findMainImageByProductId_Success() {
        List<Image> mainImage = this.iImageRepository.findMainImageByProductId(14);
        assertEquals(1, mainImage.size());
        assertTrue(mainImage.get(0).isMainImage());
        assertFalse(mainImage.get(0).isDelete());
    }

    /**
     * Trường hợp gắn cờ xoá cho list id rỗng
     * -> Không có gì thay đổi
     */
    @Test
    void setDeleteFlagOn_WithEmptyListOfId() {
        assertDoesNotThrow(() -> {
            this.iImageRepository.setDeleteFlagOn(new ArrayList<>());
        });
    }

    /**
     * Trường hợp gắn cờ xoá cho list id có 1 id không hợp lệ
     * -> chỉ thay đổi những id hợp lệ
     */
    @Test
    void setDeleteFlagOn_WithOneInvalidElement() {
        /* setup data */
        List<Image> list = this.iImageRepository.findAllByProductId(14);
        assertFalse(list.isEmpty());

        /* testing */
        assertDoesNotThrow(() -> {
            /* list sẽ set flag delete */
            List<Integer> ids = list.stream().map(Image::getId).collect(Collectors.toList());
            assertEquals(ids.size(), list.size());
            /* thêm id ko hợp lệ */
            ids.add(0);
            this.iImageRepository.setDeleteFlagOn(ids);
            List<Image> updated = this.iImageRepository.findAllById(ids);
            assertEquals(6, updated.size());
            assertTrue(updated.stream().noneMatch(image -> image.getId() == 0));
            assertTrue(updated.stream().allMatch(Image::isDelete));
        });

        /* phục hồi data sau khi test */
        this.iImageRepository.saveAll(list);
    }

    /**
     * Trường hợp gắn cờ xoá cho list id thành công
     */
    @Test
    void setDeleteFlagOn_Success() {
        /* setup data */
        List<Image> list = this.iImageRepository.findAllByProductId(14);
        assertFalse(list.isEmpty());

        /* testing */
        assertDoesNotThrow(() -> {
            /* list sẽ set flag delete */
            List<Integer> ids = list.stream().map(Image::getId).collect(Collectors.toList());
            assertEquals(ids.size(), list.size());
            this.iImageRepository.setDeleteFlagOn(ids);
            List<Image> updated = this.iImageRepository.findAllById(ids);
            assertEquals(6, updated.size());
            assertTrue(updated.stream().allMatch(Image::isDelete));
        });

        /* phục hồi data sau khi test */
        this.iImageRepository.saveAll(list);
    }

}
