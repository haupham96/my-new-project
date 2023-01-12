package com.example.bt.utils;

import com.example.bt.app.entity.Image;
import com.example.bt.app.exception.EmptyFileException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.Objects;

@Slf4j
public class Base64DecodedMultipartFile implements MultipartFile, Serializable {

    private final byte[] imgageContent;
    public static final String BASE64_SRC_HEADER = "data:image/png;base64, ";
    public static final String PNG_EXTENSION = ".png";
    public static final String JPG_EXTENSION = ".jpg";
    private final String name;

    @Override
    public String getName() {
        return this.name;
    }

    public Base64DecodedMultipartFile(byte[] imgageContent, String name) {
        this.imgageContent = imgageContent;
        this.name = name;
    }

    @Override
    public String getOriginalFilename() {
        return this.name + PNG_EXTENSION;
    }

    @Override
    public String getContentType() {
        return PNG_EXTENSION;
    }

    @Override
    public boolean isEmpty() {
        return this.imgageContent == null || this.imgageContent.length == 0;
    }

    @Override
    public long getSize() {
        return this.imgageContent.length;
    }

    @Override
    public byte[] getBytes() {
        return this.imgageContent;
    }

    @Override
    public InputStream getInputStream() {
        return new ByteArrayInputStream(imgageContent);
    }

    @Override
    public void transferTo(File dest) throws IllegalStateException {
        // Chưa sử dụng tới
    }

    public String getBase64Src() {
        String src = Base64.getEncoder().encodeToString(this.imgageContent);
        return BASE64_SRC_HEADER + src;
    }

    public static Image generateFile(MultipartFile multipartFile, String storeLocation, int productId, boolean isMainImage) throws EmptyFileException {
        Image image = new Image();
        if (multipartFile != null && !multipartFile.isEmpty()) {
            try {
                String relativeStorePath = "product\\" + productId + "\\";
                String fileName = Objects.hash(LocalDateTime.now()) + JPG_EXTENSION;
                Files.createDirectories(Paths.get(storeLocation + relativeStorePath));
                FileCopyUtils.copy(multipartFile.getBytes(),
                        new File(storeLocation + relativeStorePath + fileName));
                image.setMainImage(isMainImage);
                image.setOriginalFileName(fileName);
                image.setFileLength(multipartFile.getSize());
                image.setStorePath(relativeStorePath);
                return image;
            } catch (IOException ex) {
                log.error("Lỗi ghi file : {}", ex.getMessage());
            } catch (Exception ex) {
                log.error("error : {} ", ex.getMessage());
            }
        } else {
            throw new EmptyFileException("File is empty");
        }
        return image;
    }

    public static boolean checkExistImageName(String path) {
        File fileCheck = new File(path);
        return fileCheck.exists();
    }
}
