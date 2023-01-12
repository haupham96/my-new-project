
package com.example.bt.app.entity;

import com.example.bt.app.dto.CartDTO;
import com.example.bt.app.dto.ProductDTO;
import com.example.bt.app.service.image.IImageService;
import com.example.bt.common.TableName;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;


/**
 * @author : HauPV
 * class entity cho table carts
 */
@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(
        name = TableName.CARTS,
        uniqueConstraints = @UniqueConstraint(
                name = "UNIQUE_USER",
                columnNames = "user_id"
        )

)
public class Cart extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "cart_id")
    private int cartId;

    @OneToOne
    @JoinColumn(name = "user_id", referencedColumnName = "user_id", unique = true)
    private AppUser appUser;

    @Column(name = "total_price", columnDefinition = "decimal default 0.0")
    private BigDecimal totalPrice;

    @Column(name = "is_used_discount", columnDefinition = "boolean default false")
    private boolean isUsedDiscount;

    @ManyToOne
    @JoinColumn(name = "promotion_id", referencedColumnName = "id")
    private Promotion promotion;

    @OneToMany(mappedBy = "cart", fetch = FetchType.LAZY)
    private transient List<CartProduct> cartProducts;

    public static CartDTO mapToCartDTO(List<CartProduct> cartProductList, int cartId, IImageService iImageService) {
        CartDTO cartDTO = new CartDTO();
        cartDTO.setProducts(new HashMap<>());
        if (!cartProductList.isEmpty()) {
            cartProductList.forEach(cartProduct -> {
                ProductDTO productDTO = Product.mapToDTO(cartProduct.getProduct());
                List<Image> mainImage = iImageService.findMainImageByProductId(productDTO.getId());
                productDTO.setImages(mainImage);
                cartDTO.changeQuantity(productDTO, cartProduct.getQuantity());
            });
        }
        cartDTO.setCartId(cartId);
        return cartDTO;
    }

    @Override
    public int hashCode() {
        return Objects.hash(getCartId());
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) return false;
        if (!(obj instanceof Cart)) return false;
        if (this == obj) return true;
        return getCartId() == ((Cart) obj).getCartId();
    }
}
