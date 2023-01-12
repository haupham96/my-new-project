package com.example.bt.app.service.cart_product;

import com.example.bt.app.entity.CartProduct;
import com.example.bt.app.exception.AlreadyExistException;
import com.example.bt.app.exception.NullValueException;

import java.util.List;

/**
 * @author : HauPV
 * service cho cart_product
 */
public interface ICartProductService {
    CartProduct save(CartProduct cartProduct) throws NullValueException;

    List<CartProduct> findAllByCartId(int cartId);

    CartProduct findByCartIdAndProductId(int cartId, int productId);

    void saveAll(List<CartProduct> cartProductList) ;

    void removeCartProduct(CartProduct cartProduct) throws NullValueException;

    void removeAllCartProductByCartId(int cartId);

    void saveNew(int cartId, int productId, int quantity) throws AlreadyExistException;
}
