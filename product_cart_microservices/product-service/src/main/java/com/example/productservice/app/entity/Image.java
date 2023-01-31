package com.example.productservice.app.entity;

import com.example.productservice.app.dto.response.ImageResponse;
import com.example.productservice.common.TableName;
import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
@EntityListeners(AuditingEntityListener.class)
@Entity
@Table(name = TableName.IMAGE)
public class Image extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "original_file_name", nullable = false)
    private String originalFileName;

    @Column(name = "store_path")
    private String storePath;

    @Column(name = "file_length")
    private long fileLength;

    @Column(name = "is_main_image", columnDefinition = "boolean default false")
    private boolean isMainImage;

    @Column(name = "is_delete", columnDefinition = "boolean default false")
    private boolean isDelete;

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @JsonBackReference
    @ManyToOne
    @JoinColumn(name = "product_id", referencedColumnName = "id")
    private Product product;

    public static ImageResponse mapToImageResponse(Image image, String staticPath) {
        String imageSrc = staticPath + image.getProduct().getId() + "/" + image.getOriginalFileName();
        return ImageResponse.builder()
                .isMainImage(image.isMainImage())
                .imageSrc(imageSrc)
                .build();
    }

}
