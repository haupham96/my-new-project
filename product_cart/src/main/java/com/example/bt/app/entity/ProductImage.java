package com.example.bt.app.entity;


import com.example.bt.common.TableName;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = TableName.PRODUCT_IMAGE,
        uniqueConstraints =
        @UniqueConstraint(name = "PRODUCT_IMAGE_PK_UNIQUE", columnNames = {"product_id", "image_id"}))
public class ProductImage extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    @ManyToOne
    @JoinColumn(name = "product_id", referencedColumnName = "id", nullable = false)
    private Product product;

    @ManyToOne
    @JoinColumn(name = "image_id", referencedColumnName = "id", nullable = false)
    private Image image;

}
