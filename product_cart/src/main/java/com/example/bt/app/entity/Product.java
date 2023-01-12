
package com.example.bt.app.entity;

import com.example.bt.app.dto.ProductDTO;
import com.example.bt.common.TableName;
import com.example.bt.utils.Base64DecodedMultipartFile;
import lombok.*;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.annotations.Type;
import org.springframework.beans.BeanUtils;
import org.springframework.web.multipart.MultipartFile;

import javax.persistence.*;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @author : HauPV
 * class entity cho table product
 */
@Slf4j
@Entity
@Getter
@Setter
@ToString
@NoArgsConstructor
@Table(name = TableName.PRODUCT)
public class Product extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(unique = true)
    private String name;

    @Column(nullable = false)
    private long price;

    @Column(nullable = false)
    private String description;

    @OneToMany(mappedBy = "product", fetch = FetchType.LAZY)
    private transient List<CartProduct> cartProducts;

    @OneToMany(mappedBy = "product", fetch = FetchType.LAZY)
    private transient List<ProductImage> productImages;

    @OneToMany(mappedBy = "product")
    private transient List<ProductPromotion> productPromotions;

    public Product(String name, long price, String description) {
        this.name = name;
        this.price = price;
        this.description = description;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Product)) return false;
        Product product = (Product) o;
        return getId() == product.getId();
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId());
    }

    //    Chuyển đổi từ kiểu Product sang kiểu ProductDTO
    public static ProductDTO mapToDTO(Product product) {
        log.info("class - Product");
        log.info("method : mapToDTO()");
        ProductDTO productDTO = new ProductDTO();
        BeanUtils.copyProperties(product, productDTO);
        log.info("Kết thúc method : mapToDTO()");
        return productDTO;
    }
}
