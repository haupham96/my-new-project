package com.example.bt.app.entity;

import com.example.bt.common.TableName;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;
import java.util.Calendar;
import java.util.List;
import java.util.Objects;

@Entity
@NoArgsConstructor
@Getter
@Setter
@ToString
@Table(name = TableName.PROMOTION)
public class Promotion extends BaseEntity {

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
    private transient List<Cart> cart;

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
