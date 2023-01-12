package com.example.bt.app.entity;

import com.example.bt.common.TableName;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@Entity
@EntityListeners(AuditingEntityListener.class)
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

    @Column(name = "isMainImage", columnDefinition = "boolean default false")
    private boolean isMainImage;

    @Column(name = "is_delete", columnDefinition = "boolean default false")
    private boolean isDelete;

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @OneToMany(mappedBy = "image")
    private transient List<ProductImage> productImages;

    public String getImageSrc() {
        return this.storePath + this.originalFileName;
    }

}
