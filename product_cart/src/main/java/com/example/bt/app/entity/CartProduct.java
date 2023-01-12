
package com.example.bt.app.entity;

import com.example.bt.common.TableName;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

/**
 * @author : HauPV
 * class entity cho table cart_product
 */
@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(
        name = TableName.CART_PRODUCT,
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "CART_ID_PRODUCT_ID_UNIQUE",
                        columnNames = {"cart_id", "product_id"})
        }
)
public class CartProduct extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @ManyToOne()
    @JoinColumn(name = "cart_id", referencedColumnName = "cart_id", nullable = false)
    private Cart cart;


    @ManyToOne()
    @JoinColumn(name = "product_id", referencedColumnName = "id", nullable = false)
    private Product product;

    @Column(name = "quantity")
    private int quantity;

}
