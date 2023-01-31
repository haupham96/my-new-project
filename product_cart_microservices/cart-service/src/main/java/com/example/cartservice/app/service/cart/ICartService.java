package com.example.cartservice.app.service.cart;

import com.example.cartservice.app.dto.CartDTO;
import com.example.cartservice.app.dto.request.CartRequest;
import com.example.cartservice.app.exception.CartNotFoundException;
import com.example.cartservice.app.exception.InvalidRequestBodyException;

import java.util.List;

public interface ICartService {
    CartDTO findCart(Integer cartId) throws CartNotFoundException;

    void handleAddProductToCart(CartRequest request) throws CartNotFoundException, InvalidRequestBodyException;

    CartDTO setCartUser(Integer cartId, CartRequest cartRequest) throws CartNotFoundException, InvalidRequestBodyException;

    void handleCreateCart(CartRequest cartRequest) throws InvalidRequestBodyException;

    void deleteProductInCart(int cartId, List<String> productNames) throws CartNotFoundException;

    void handleChangeProductQuantity(CartRequest cartRequest) throws CartNotFoundException, InvalidRequestBodyException;

    void deleteCartById(int cartId) throws CartNotFoundException;
}
