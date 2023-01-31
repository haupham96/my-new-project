package com.example.productservice.common;

import com.example.productservice.app.entity.Image;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

@Slf4j
public class MultiPartFileImpl implements MultipartFile {
    private final String fileName;
    private final String contentType;
    private final byte[] content;
    public static final String JPG_EXTENSION = ".jpg";

    public MultiPartFileImpl(String fileName, String contentType, byte[] content) {
        this.fileName = fileName;
        this.content = content;
        this.contentType = contentType;
    }

    @Override
    public String getName() {
        return this.fileName;
    }

    @Override
    public String getOriginalFilename() {
        return this.fileName + this.contentType;
    }

    @Override
    public String getContentType() {
        return this.contentType;
    }

    @Override
    public boolean isEmpty() {
        return content == null || content.length == 0;
    }

    @Override
    public long getSize() {
        return content.length;
    }

    @Override
    public byte[] getBytes() {
        return content;
    }

    @Override
    public InputStream getInputStream() {
        return new ByteArrayInputStream(content);
    }

    @Override
    public void transferTo(File dest) throws IllegalStateException, IOException {
        if (content != null && content.length > 0) {
            FileCopyUtils.copy(content, dest);
        }
    }

    public static Image generateFile(MultipartFile multipartFile, String storeLocation, int productId, boolean isMainImage)
            throws FileNotFoundException {
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
                image.setStorePath(relativeStorePath);
                image.setFileLength(multipartFile.getSize());
                return image;
            } catch (IOException ex) {
                log.error("Lỗi ghi file : {}", ex.getMessage());
            } catch (Exception ex) {
                log.error("error : {} ", ex.getMessage());
            }
        } else {
            throw new FileNotFoundException("File is empty");
        }
        return image;
    }

    public static void deleteDirectory(List<Image> imagesDelete, String fileStoreLocation) throws IOException {
        for (Image image : imagesDelete) {
            String imageSrc = fileStoreLocation + image.getStorePath() + image.getOriginalFileName();
            Files.deleteIfExists(Paths.get(imageSrc));
        }
    }
}
