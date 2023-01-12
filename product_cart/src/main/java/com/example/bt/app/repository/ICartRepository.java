package com.example.bt.app.repository;

import com.example.bt.app.entity.Cart;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

/**
 * @author : HauPV
 * repository cho table carts
 */
public interface ICartRepository extends JpaRepository<Cart, Integer> {

    //    Tìm cart theo user_id
    @Query(value = "select * from carts where user_id = ?", nativeQuery = true)
    Cart findCartByUserId(int userId);

    //    Xoá discount của giỏ hàng theo cart_id .
    @Transactional(rollbackFor = {Exception.class, Throwable.class}, propagation = Propagation.REQUIRES_NEW)
    @Modifying
    @Query(value = " UPDATE carts SET is_used_discount = true WHERE cart_id = ? ", nativeQuery = true)
    void setUsedDiscount(int cartId);

    /* update lại giá tiền của giỏ hàng */
    @Modifying
    @Transactional
    @Query(value = " UPDATE carts SET total_price = :totalPayment WHERE cart_id = :cartId ",
            nativeQuery = true)
    void updateTotalPrice(@Param("cartId") int cartId, @Param("totalPayment") long totalPaymentWithDiscount);
}
