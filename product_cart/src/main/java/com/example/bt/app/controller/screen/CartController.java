package com.example.bt.app.controller.screen;

import com.example.bt.app.dto.CartDTO;
import com.example.bt.app.dto.ProductDTO;
import com.example.bt.app.exception.*;
import com.example.bt.app.service.cart.ICartService;
import com.example.bt.app.service.product.IProductService;
import com.example.bt.app.service.promotion.IPromotionService;
import com.example.bt.utils.CookieUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.security.Principal;

/**
 * @author : HauPV
 * Controller cho chức năng giỏ hàng
 */
@Slf4j
@Controller
@RequestMapping("/cart")
public class CartController {
    @Autowired
    private IProductService iProductService;

    @Autowired
    private ICartService iCartService;

    @Autowired
    private IPromotionService iPromotionService;

    //    Controller cho xem thông tin toàn bộ hàng hoá có trong giỏ
    @GetMapping("")
    public String cartDetails(@CookieValue(value = "cart_id", defaultValue = "0") String cartId,
                              HttpServletRequest request, HttpServletResponse response,
                              Model model,
                              Principal principal) throws Exception {
        log.info(this.getClass().getSimpleName());
        log.info("mapping : GET /cart ");
        log.info("method : cartDetails()");
        CartDTO cartDTO = this.iCartService.getUserCart(principal, cartId, request, response);
        this.iPromotionService.findPromotion(cartDTO);
        model.addAttribute("cart", cartDTO);
        log.info("Kết thúc method : cartDetails()");
        return "cart/cart";
    }

    //    Controller Có chức năng thêm hàng hoá vào giỏ hàng
    @PostMapping("/add-product/{productId}")
    public String addToCart(@PathVariable Integer productId,
                            @RequestParam("quantity") int quantity,
                            @CookieValue(value = "cart_id", defaultValue = "0") String cartId,
                            HttpServletRequest request, HttpServletResponse response,
                            Model model,
                            Principal principal) throws IOException, ProductNotFoundException,
            InvalidQuantityException, NullValueException, AlreadyExistException {
        log.info(this.getClass().getSimpleName());
        log.info("mapping : POST /cart/add-product/{productId} ");
        log.info("method : addToCart()");
        ProductDTO productDTO = this.iProductService.findById(productId);
        CartDTO cartDTO = this.iCartService
                .handleAddProductToCart(cartId, productDTO, quantity, principal, request, response);
        this.iPromotionService.findPromotion(cartDTO);
        model.addAttribute("cart", cartDTO);

        log.info("Kết thúc method : addToCart()");
        return "cart/cart";

    }

    //    Controller có chức năng xoá 1 sản phầm trong giỏ hàng
    @GetMapping("/delete-product/{productId}")
    public String deleteProduct(@PathVariable int productId,
                                Principal principal,
                                @CookieValue(value = CookieUtils.CART_ID_KEY, defaultValue = "0") String cartId)
            throws IOException, ProductNotFoundException, NullValueException {
        log.info(this.getClass().getSimpleName());
        log.info("mapping : GET /cart/delete-product/{productId} ");
        log.info("method : deleteProduct()");
        this.iCartService.deleteProductIncart(principal, cartId, productId);
        log.info("Kết thúc method : deleteProduct()");
        return "redirect:/cart";
    }

    //  Xoá toàn bộ hàng trong giỏ .
    @GetMapping("/clear")
    public String clearCart(
            Principal principal,
            @CookieValue(value = CookieUtils.CART_ID_KEY, defaultValue = "0") String cartId,
            HttpServletResponse response) throws CartNotFoundException {
        log.info(this.getClass().getSimpleName());
        log.info("mapping : GET /cart/clear ");
        log.info("method : clearCart()");
        this.iCartService.deleteCart(principal, cartId, response);
        log.info("Kết thúc method : clearCart()");
        return "redirect:/cart";
    }
}
