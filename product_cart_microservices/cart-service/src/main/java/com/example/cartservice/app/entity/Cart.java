package com.example.cartservice.app.entity;

import com.example.cartservice.common.TableName;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;
import java.util.Objects;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Builder
@Entity
@Table(name = TableName.CART)
public class Cart {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private Integer promotionId;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "username", referencedColumnName = "username", unique = true)
    private KeycloakUser keycloakUser;

    @Column(name = "is_block_discount", columnDefinition = "boolean default false")
    private boolean isBlockDiscount;
    @OneToMany(mappedBy = "cart")
    private transient List<CartProduct> cartProducts;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Cart cart)) return false;
        return getId() == cart.getId();
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId());
    }
}
