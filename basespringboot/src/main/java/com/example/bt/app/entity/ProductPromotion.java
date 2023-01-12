package com.example.bt.app.entity;

import com.example.bt.common.TableName;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = TableName.PRODUCT_PROMOTION,
        uniqueConstraints = @UniqueConstraint(
                name = "UNIQUE_FK_PRODUCT_ID_PROMOTION_ID",
                columnNames = {"product_id", "promotion_id"}))
public class ProductPromotion {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @ManyToOne
    @JoinColumn(name = "product_id", referencedColumnName = "id", nullable = false)
    private Product product;

    @ManyToOne
    @JoinColumn(name = "promotion_id", referencedColumnName = "id", nullable = false)
    private Promotion promotion;
}
