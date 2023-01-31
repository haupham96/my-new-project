package com.example.cartservice.app.controller;

import com.example.cartservice.app.dto.CartDTO;
import com.example.cartservice.app.dto.request.CartRequest;
import com.example.cartservice.app.exception.CartNotFoundException;
import com.example.cartservice.app.exception.InvalidRequestBodyException;
import com.example.cartservice.app.service.cart.ICartService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RestController
@CrossOrigin("http://localhost:8080")
@RequestMapping("/api/cart")
public class CartRestController {

    private final ICartService iCartService;

    /* Lấy thông tin sản phẩm của giỏ hàng theo cart_id */
    @ResponseStatus(HttpStatus.FOUND)
    @GetMapping("/{cartId}")
    public CartDTO getCartDetails(@PathVariable Integer cartId) throws CartNotFoundException {
        return this.iCartService.findCart(cartId);
    }

    /* tạo mới giỏ hàng */
    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("")
    public void createCart(@RequestBody CartRequest cartRequest) throws InvalidRequestBodyException {
        this.iCartService.handleCreateCart(cartRequest);
    }

    /* Xoá giỏ hàng */
    @ResponseStatus(HttpStatus.OK)
    @DeleteMapping("/{cartId}")
    public void deleteCart(@PathVariable int cartId) throws CartNotFoundException {
        this.iCartService.deleteCartById(cartId);
    }

    /* gán user cho giỏ hàng */
    @ResponseStatus(HttpStatus.OK)
    @PutMapping("/{cartId}")
    public CartDTO setCartUser(@PathVariable Integer cartId, @RequestBody CartRequest cartRequest)
            throws CartNotFoundException, InvalidRequestBodyException {
        return this.iCartService.setCartUser(cartId, cartRequest);
    }

    /* Thêm sản phẩm vào giỏ */
    @ResponseStatus(HttpStatus.OK)
    @PutMapping("/add")
    public void addProductToCart(@RequestBody CartRequest request) throws CartNotFoundException, InvalidRequestBodyException {
        this.iCartService.handleAddProductToCart(request);
    }

    /* xoá sản phẩm khỏi giỏ hàng */
    @ResponseStatus(HttpStatus.OK)
    @DeleteMapping("/product/{cartId}")
    public void deleteProductInCart(@PathVariable int cartId,
                                    @RequestBody List<String> productNames) throws CartNotFoundException {
        this.iCartService.deleteProductInCart(cartId, productNames);
    }

    /* Thay đổi số lượng sp có trong giỏ */
    @ResponseStatus(HttpStatus.OK)
    @PutMapping("/product")
    public void changeProductQuantity(@RequestBody CartRequest cartRequest) throws CartNotFoundException, InvalidRequestBodyException {
        this.iCartService.handleChangeProductQuantity(cartRequest);
    }

}
