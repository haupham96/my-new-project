package com.example.promotionservice.app.entity;

import com.example.promotionservice.common.TableName;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = TableName.PRODUCT_PROMOTION,
        uniqueConstraints = @UniqueConstraint(
                name = "PRODUCT_PROMOTION_UNQ",
                columnNames = {"productName", "promotion_id"}))
public class ProductPromotion {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(nullable = false)
    private String productName;

    @ManyToOne
    @JoinColumn(name = "promotion_id", referencedColumnName = "id", nullable = false)
    private Promotion promotion;
}
