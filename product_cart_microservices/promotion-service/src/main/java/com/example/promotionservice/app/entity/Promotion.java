package com.example.promotionservice.app.entity;

import com.example.promotionservice.common.TableName;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Calendar;
import java.util.List;
import java.util.Objects;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = TableName.PROMOTION)
public class Promotion {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private String name;

    @Column(name = "value", columnDefinition = "double default 0.0", nullable = false)
    private double value;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "start", nullable = false)
    private Calendar from;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "end", nullable = false)
    private Calendar to;

    @OneToMany(mappedBy = "promotion")
    private transient List<ProductPromotion> productPromotions;

    @Override
    public int hashCode() {
        return Objects.hash(getId());
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof Promotion)) return false;
        Promotion promotion = (Promotion) obj;
        return getId() == promotion.getId();
    }
}
