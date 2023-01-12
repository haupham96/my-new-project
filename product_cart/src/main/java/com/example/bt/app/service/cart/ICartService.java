package com.example.bt.app.service.cart;

import com.example.bt.app.dto.CartDTO;
import com.example.bt.app.dto.ProductDTO;
import com.example.bt.app.entity.Cart;
import com.example.bt.app.exception.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.security.Principal;

/**
 * @author : HauPV
 * service cho cart
 */
public interface ICartService {
    Cart save(Cart cart);

    void handlePaymentSuccess(String cartId, HttpServletResponse response) throws CartNotFoundException;

    CartDTO getUserCart(Principal principal, String cartId, HttpServletRequest request, HttpServletResponse response) throws Exception;

    CartDTO handleAddProductToCart(String cartId, ProductDTO productDTO, int quantity, Principal principal, HttpServletRequest request, HttpServletResponse response) throws IOException, NullValueException, InvalidQuantityException, AlreadyExistException;

    void deleteProductIncart(Principal principal, String cartId, int productId) throws IOException, ProductNotFoundException, NullValueException;

    void deleteCart(Principal principal, String cartId, HttpServletResponse response) throws CartNotFoundException;

    Cart findByCartId(Integer cartId);

    void updateTotalPrice(int cartId, long totalPaymentWithDiscount);
}
