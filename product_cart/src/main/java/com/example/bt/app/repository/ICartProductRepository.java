
package com.example.bt.app.repository;

import com.example.bt.app.entity.CartProduct;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @author : HauPV
 * repository cho table cart_product
 */
public interface ICartProductRepository extends JpaRepository<CartProduct, Integer> {

    //    Lấy ra List<CartProduct> theo cart_id
    @Query(value = "select * from cart_product where cart_id = ?", nativeQuery = true)
    List<CartProduct> findAllByCartId(int cartId);

    //    Lấy ra 1 CartProduct theo cart_id và product_id
    @Query(value = " SELECT * FROM cart_product where cart_id = ? and product_id = ? ", nativeQuery = true)
    CartProduct findByCartIdAndProductId(int cartId, int productId);

    //    Xoá các sản phẩm trong giỏ theo cart_id trong db .
    @Modifying
    @Transactional
    @Query(value = " DELETE FROM cart_product where cart_id = ? ", nativeQuery = true)
    void deleteAllCartProductByCartId(int cartId);

    /* Thêm mới cart_product */
    @Modifying
    @Query(value = " INSERT INTO cart_product (cart_id , product_id , quantity) " +
            " VALUES ( ? , ? , ? ) ",
            nativeQuery = true)
    void saveNew(int cartId, int productId, int quantity);

    /* xoá cart_product theo product_id */
    @Modifying
    @Transactional
    @Query(value = " DELETE FROM cart_product WHERE product_id = ? ",
            nativeQuery = true)
    void deleteAllByProductId(int productId);

    /* Tìm danh sách cart_product theo product_id */
    List<CartProduct> findAllByProductId(int id);
}
