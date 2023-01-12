package com.example.bt.app.service.cart;

import com.example.bt.app.dto.CartDTO;
import com.example.bt.app.dto.ProductDTO;
import com.example.bt.app.entity.AppUser;
import com.example.bt.app.entity.Cart;
import com.example.bt.app.entity.CartProduct;
import com.example.bt.app.exception.*;
import com.example.bt.app.repository.ICartRepository;
import com.example.bt.app.service.cart_product.ICartProductService;
import com.example.bt.app.service.image.IImageService;
import com.example.bt.app.service.product.IProductService;
import com.example.bt.app.service.user.IAppUserService;
import com.example.bt.utils.CookieUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.security.Principal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * @author : HauPV
 * service cho cart
 */
@Slf4j
@Service
public class CartServiceImpl implements ICartService {

    @Autowired
    private IProductService iProductService;

    @Autowired
    private ICartRepository iCartRepository;

    @Autowired
    private IAppUserService iAppUserService;

    @Autowired
    private ICartProductService iCartProductService;

    @Autowired
    private IImageService iImageService;

    //    Lưu Cart vào database
    @Transactional
    @Override
    public Cart save(Cart cart) {
        log.info(this.getClass().getSimpleName());
        log.info("method - save()");
        log.info("Kết thúc method - save()");
        return this.iCartRepository.save(cart);
    }

    //  Lấy thông tin giỏ hàng
    @Transactional(rollbackFor = {Exception.class, Throwable.class})
    @Override
    public CartDTO getUserCart(Principal principal,
                               String cartId,
                               HttpServletRequest request,
                               HttpServletResponse response) {
        log.info("class : {}", this.getClass().getSimpleName());
        try {
            if (principal != null && principal.getName() != null) {
                /* Trường hợp đã đăng nhập */
                log.info("khối if : principal != null && principal.getName() != null");
                AppUser appUser = this.iAppUserService.findByUsername(principal.getName());
                Cart cartInCookie = this.iCartRepository.findById(Integer.valueOf(cartId)).orElse(null);
                Cart cartOfUser = this.iCartRepository.findCartByUserId(appUser.getUserId());
                CartDTO cartDTO = new CartDTO();
                if (cartOfUser != null) {
                    log.info("khối if : cartOfUser != null");
                    /* Người đăng nhập đã có giỏ hàng */
                    cartDTO = this.mergeCookieCartAndUserCart(cartOfUser, cartInCookie);
                    CookieUtils.set(request, response, "cart_id", String.valueOf(cartDTO.getCartId()));
                    log.info("kết thúc khối if : cartOfUser != null");
//              /* Nhập cart_product ở cookie vào user -> xoá giỏ hàng của cookie và sử dụng giỏ hàng user */
                } else {
                    /* Trường hợp user chưa có giỏ hàng  */
                    log.info("khối else : cartOfUser != null");
                    if (cartInCookie != null) {
                        /* trong cookie có giỏ hàng -> đồng bộ giỏ hàng của cookie thành của user */
                        log.info("khối if : cartInCookie != null");
                        cartInCookie.setAppUser(appUser);
                        this.iCartRepository.saveAndFlush(cartInCookie);
                        List<CartProduct> cartProducts = this.iCartProductService.findAllByCartId(cartInCookie.getCartId());
                        cartDTO = Cart.mapToCartDTO(cartProducts, cartInCookie.getCartId(), iImageService);
                        log.info("kết thúc khối if : cartInCookie != null");
                        CookieUtils.set(request, response, "cart_id", String.valueOf(cartDTO.getCartId()));
                    }
                    log.info("khối else : cartOfUser != null");
                }
                /* Lưu id giỏ hàng vào cookie */
                log.info("kết thúc khối if : principal != null && principal.getName() != null");
                return cartDTO;
            } else {
                log.info("khối else : principal != null && principal.getName() != null");
                /* Trường hợp chưa đăng nhập */
                Optional<Cart> cart = this.iCartRepository.findById(Integer.valueOf(cartId));
                CartDTO cartDTO = new CartDTO();
                if (cart.isPresent()) {
                    /* Cookie có giỏ hàng -> sử dụng giỏ hàng đã có */
                    log.info("khối if cart.isPresent()");
                    if (cart.get().getAppUser() != null) {
                        /* Trường hợp đã hết phiên đăng nhập -> xoá cookies */
                        CookieUtils.delete(response, CookieUtils.CART_ID_KEY);
                    } else {
                        /* AppUser = null -> lấy giỏ hàng của cookie */
                        List<CartProduct> cartProductList = this.iCartProductService.findAllByCartId(cart.get().getCartId());
                        cartDTO = Cart.mapToCartDTO(cartProductList, cart.get().getCartId(), iImageService);
                        cartDTO.setUsedDiscount(cart.get().isUsedDiscount());
                        CookieUtils.set(request, response, "cart_id", String.valueOf(cartDTO.getCartId()));
                    }
                    log.info("kết thúc khối if cart.isPresent()");
                }
                log.info("kết thúc khối else : principal != null && principal.getName() != null + class {}", this.getClass().getSimpleName());
                /* Lưu id giỏ hàng vào cookie */
                return cartDTO;
            }
        } catch (Exception ex) {
            log.info("khối catch Exception ");
            log.error("Exception : {}", ex.getMessage());
            /* Huỷ giảm giá cho giỏ hàng khi có lỗi xảy ra */
            log.info("kết thúc khối catch + class : {}", this.getClass().getSimpleName());
            return this.handleExceptionWhenGetCart(Integer.parseInt(cartId), principal, request, response);
        }
    }

    //    Khi thanh toán thành công -> xoá giỏ hàng trong db
    @Transactional(rollbackFor = {Exception.class, Throwable.class})
    @Override
    public void handlePaymentSuccess(String cartId, HttpServletResponse response) throws CartNotFoundException {
        log.info(this.getClass().getSimpleName());
        log.info("method - handlePaymentSuccess()");
        Optional<Cart> cart = this.iCartRepository.findById(Integer.valueOf(cartId));
        if (cart.isPresent()) {
            /* Xoá toàn bộ thông tin giỏ hàng sau khi thanh toán thành công */
            log.info("khối if : value.isPresent ");
            this.iCartProductService.removeAllCartProductByCartId(cart.get().getCartId());
            this.iCartRepository.deleteById(cart.get().getCartId());
            CookieUtils.delete(response, CookieUtils.CART_ID_KEY);
            log.info("kết thúc khối if : value.isPresent ");
        } else {
            throw new CartNotFoundException("không tìm thấy giỏ hàng : " + cartId);
        }

        log.info("kết thúc method - handlePaymentSuccess()");
    }

    /* Xử lý thêm sản phẩm vào giỏ hàng */
    @Transactional(rollbackFor = {Exception.class, Throwable.class})
    @Override
    public CartDTO handleAddProductToCart(String cartId,
                                          ProductDTO productDTO,
                                          int quantity,
                                          Principal principal,
                                          HttpServletRequest request,
                                          HttpServletResponse response)
            throws IOException, NullValueException, InvalidQuantityException, AlreadyExistException {
        log.info("class : {}", this.getClass().getSimpleName());
        log.info("method : handleAddProductToCart");
        if (quantity < 1) {
            throw new InvalidQuantityException("Số lượng không hợp lệ : " + quantity);
        }
        if (principal != null && principal.getName() != null) {
            /* User đã đăng nhập vào hệ thống */
            CartDTO cartDTO;
            log.info("khối if : principal != null && principal.getName() != null");
            AppUser appUser = this.iAppUserService.findByUsername(principal.getName());
            Cart cartInCookie = this.iCartRepository.findById(Integer.valueOf(cartId)).orElse(null);
            Cart cartOfUser = this.iCartRepository.findCartByUserId(appUser.getUserId());

            cartDTO = this.handleAddProductToCartWithLoginUser(cartOfUser, cartInCookie, appUser, productDTO, quantity);
            /* Lưu id giỏ hàng vào cookie */
            CookieUtils.set(request, response, CookieUtils.CART_ID_KEY, String.valueOf(cartDTO.getCartId()));
            log.info("kết thúc method : handleAddProductToCart + khối if : principal != null && principal.getName() != null");
            return cartDTO;
        } else {
            /* Trường hợp user chưa đăng nhập */
            log.info("khối else : principal != null && principal.getName() != null");
            Cart cartInCookie = this.iCartRepository.findById(Integer.valueOf(cartId)).orElse(null);
            CartDTO cartDTO = this.handleAddProductToCartWithNoUserLogin(cartInCookie, productDTO, quantity);
            /* Lưu id giỏ hàng vào cookie */
            CookieUtils.set(request, response, CookieUtils.CART_ID_KEY, String.valueOf(cartDTO.getCartId()));
            log.info("kết thúc method : handleAddProductToCart + khối else : principal != null && principal.getName() != null");
            return cartDTO;
        }
    }

    @Override
    public void deleteProductIncart(Principal principal, String cartId, int productId)
            throws IOException, ProductNotFoundException, NullValueException {
        log.info("class : {}", this.getClass().getSimpleName());
        log.info("method : deleteProductIncart");
        CartProduct cartProduct = null;
        ProductDTO productDTO = this.iProductService.findById(productId);
        if (principal != null && principal.getName() != null) {
            log.info("khối if : principal != null && principal.getName() != null");
            /* Trường hợp user đăng nhập vào hệ thống */
            AppUser appUser = this.iAppUserService.findByUsername(principal.getName());
            Cart cartOfUser = this.iCartRepository.findCartByUserId(appUser.getUserId());
            if (cartOfUser != null) {
                /* Tìm thấy giỏ hàng -> Tìm sản phẩm cần xoá */
                log.info("khối if : cartOfUser != null");
                cartProduct = this.iCartProductService
                        .findByCartIdAndProductId(cartOfUser.getCartId(), productDTO.getId());
                log.info("kết thúc khối if : cartOfUser != null");
            }
            log.info("kết thúc khối if : principal != null && principal.getName() != null");
        } else {
            /* Trường hợp user chưa đăng nhập */
            log.info("khối else : principal != null && principal.getName() != null");
            Optional<Cart> cartInCookie = this.iCartRepository.findById(Integer.valueOf(cartId));
            if (cartInCookie.isPresent()) {
                /* Nếu có giỏ hàng của cookie -> tìm sản phẩm cần xoá */
                log.info("khối if : cartInCookie.isPresent()");
                cartProduct = this.iCartProductService
                        .findByCartIdAndProductId(cartInCookie.get().getCartId(), productDTO.getId());
                log.info("kết thúc khối if : cartInCookie.isPresent()");
            }
            log.info("kết thúc khối else : principal != null && principal.getName() != null");
        }
        if (cartProduct != null) {
            /* Xoá sản phẩm tìm được */
            log.info("khối if : cartProduct != null");
            this.iCartProductService.removeCartProduct(cartProduct);
            log.info("kết thúc khối if : cartProduct != null");
        } else {
            log.info("khối else : cartProduct != null " +
                    "-> throw NullValueException : cart_product not found => cart_id : {} , product_id :{}", cartId, productId);
            throw new NullValueException("Không tìm thấy sản phẩm này trong giỏ hàng");
        }
        log.info("kết thúc method : deleteProductIncart");
    }

    //  Xoá toàn bộ sản phẩm trong giỏ hàng
    @Transactional(rollbackFor = {Exception.class, Throwable.class})
    @Override
    public void deleteCart(Principal principal, String cartId, HttpServletResponse response) throws CartNotFoundException {
        log.info("class {}", this.getClass().getSimpleName());
        log.info("method  : deleteCart");
        Cart cartClear;
        if (principal != null && principal.getName() != null) {
            /* Trường hợp user đã login -> tìm giỏ hàng của user */
            log.info("khối if : principal != null && principal.getName() != null");
            AppUser appUser = this.iAppUserService.findByUsername(principal.getName());
            cartClear = this.iCartRepository.findCartByUserId(appUser.getUserId());
            if (cartClear == null) {
                log.info("khối if : cartClear == null -> throw CartNotFoundException");
                throw new CartNotFoundException("không tìm thấy giỏ hàng của user : " + principal.getName());
            }
            log.info("kết thúc khối if : principal != null && principal.getName() != null");
        } else {
            /* Trường hợp user ko login -> tìm giỏ hàng của cookie */
            log.info("khối else : principal != null && principal.getName() != null");
            cartClear = this.iCartRepository.findById(Integer.valueOf(cartId)).orElse(null);
            if (cartClear == null) {
                log.info("khối if : cartClear == null -> throw CartNotFoundException");
                throw new CartNotFoundException("Không tìm thấy giỏ hàng : " + cartId);
            }
            log.info("kết thúc khối else : principal != null && principal.getName() != null");
        }

        /* Xoá giỏ hàng */
        this.iCartProductService.removeAllCartProductByCartId(cartClear.getCartId());
        this.iCartRepository.deleteById(cartClear.getCartId());
        CookieUtils.delete(response, CookieUtils.CART_ID_KEY);
        log.info("kết thúc method  : deleteCart");
    }

    /* Tìm giỏ hàng theo id */
    @Override
    public Cart findByCartId(Integer cartId) {
        log.info("class : {}", this.getClass().getSimpleName());
        log.info("method : findByCartId");
        log.info("kết thúc method : findByCartId");
        return this.iCartRepository.findById(cartId).orElse(null);
    }

    /* Sửa tổng giá tiền của giỏ hàng */
    @Transactional(rollbackFor = {Exception.class, Throwable.class})
    @Override
    public void updateTotalPrice(int cartId, long totalPaymentWithDiscount) {
        log.info("class : {}", this.getClass().getSimpleName());
        log.info("method : updateTotalPrice");
        Optional<Cart> cart = this.iCartRepository.findById(cartId);
        cart.ifPresent(value -> this.iCartRepository.updateTotalPrice(value.getCartId(), totalPaymentWithDiscount));
        log.info("kết thúc method : updateTotalPrice");
    }

    /* thêm sản phẩm vào giỏ hàng cho user đã đăng nhập */
    private CartDTO handleAddProductToCartWithNoUserLogin(Cart cartInCookie, ProductDTO productDTO, int quantity) throws IOException, NullValueException {
        log.info("method : handleAddProductToCartWithNoUserLogin");
        CartDTO cartDTO;
        if (cartInCookie != null) {
            /* Cookie có sẵng giỏ hàng -> add sản shim vào */
            log.info("khối if : cartInCookie != null");
            CartProduct cartProduct = this.iCartProductService.findByCartIdAndProductId(cartInCookie.getCartId(), productDTO.getId());
            if (cartProduct != null) {
                /* Giỏ hàng của cookie có hàng -> chỉ cần thêm số lượng */
                log.info("khối if : cartProduct != null");
                cartProduct.setQuantity(cartProduct.getQuantity() + quantity);
                this.iCartProductService.save(cartProduct);
                log.info("kết thúc khối if : cartProduct != null");
            } else {
                /* giỏ của cookie chưa có hàng -> thêm mới record vào cart_product  */
                log.info("khối else : cartProduct != null");
                cartProduct = new CartProduct();
                cartProduct.setProduct(ProductDTO.mapToEntity(productDTO));
                cartProduct.setCart(cartInCookie);
                cartProduct.setQuantity(quantity);
                this.iCartProductService.save(cartProduct);
                log.info("kết thúc khối else : cartProduct != null");
            }

            /* mapping DTO trả về view và update total price */
            List<CartProduct> cartProductsCookie = this.iCartProductService.findAllByCartId(cartInCookie.getCartId());
            cartDTO = Cart.mapToCartDTO(cartProductsCookie, cartInCookie.getCartId(), iImageService);
            this.iCartRepository.updateTotalPrice(cartInCookie.getCartId(), cartDTO.getTotalPaymentWithDiscount());
            log.info("kết thúc khối if : cartInCookie != null");
        } else {
            /* Cookie chưa có giỏ hàng nào -> tạo mới giỏ và add sản phẩm vào */
            log.info("khối else : cartInCookie != null");
            Cart cart = this.iCartRepository.save(new Cart());
            CartProduct cartProduct = new CartProduct();
            cartProduct.setProduct(ProductDTO.mapToEntity(productDTO));
            cartProduct.setCart(cart);
            cartProduct.setQuantity(quantity);
            this.iCartProductService.save(cartProduct);

            /* Mapping lại DTO để trả về view và update total price */
            List<CartProduct> cartProducts = this.iCartProductService.findAllByCartId(cart.getCartId());
            cartDTO = Cart.mapToCartDTO(cartProducts, cart.getCartId(), iImageService);
            this.iCartRepository.updateTotalPrice(cart.getCartId(), cartDTO.getTotalPaymentWithDiscount());
            log.info("kết thúc khối else : cartInCookie != null");
        }
        log.info("kết thúc method : handleAddProductToCartWithNoUserLogin");
        return cartDTO;
    }

    /* thêm sản phẩm vào giỏ cho user chưa đăng nhập */
    private CartDTO handleAddProductToCartWithLoginUser(Cart cartOfUser, Cart cartInCookie, AppUser appUser, ProductDTO productDTO, int quantity) throws IOException, NullValueException, AlreadyExistException {
        log.info("method : handleAddProductToCartWithLoginUser");
        CartDTO cartDTO;
        if (cartOfUser != null) {
            /* User đăng nhập đã có giỏ hàng */
            log.info("khối if : cartOfUser != null");
            CartProduct cartProduct = this.iCartProductService
                    .findByCartIdAndProductId(cartOfUser.getCartId(), productDTO.getId());
            if (cartProduct != null) {
                /* Trong giỏ hàng của user đã có sản phẩm này -> tăng số lượng lên */
                log.info("khối if : cartProduct != null");
                cartProduct.setQuantity(cartProduct.getQuantity() + quantity);
                this.iCartProductService.save(cartProduct);
                log.info("kết thúc khối if : cartProduct != null");
            } else {
                /* Trong giỏ user chưa có sản phẩm này -> tạo mới record trong table cart_product */
                log.info("khối else : cartProduct != null");
                this.iCartProductService.saveNew(cartOfUser.getCartId(), productDTO.getId(), quantity);
                log.info("kết thúc khối else : cartProduct != null");
            }
            /* Mapping sang DTO và update lại tổng tiền */
            cartOfUser = this.iCartRepository.findById(cartOfUser.getCartId()).orElse(null);
            if (cartOfUser != null) {
                log.info("khối if : cartOfUser != null");
                cartDTO = this.mergeCookieCartAndUserCart(cartOfUser, cartInCookie);
                this.iCartRepository.updateTotalPrice(cartOfUser.getCartId(), cartDTO.getTotalPaymentWithDiscount());
                log.info("kết thúc khối if : cartOfUser != null");
            } else {
                log.info("khối else : cartOfUser != null -> Throw NullValueException : cartOfUser = null");
                throw new NullValueException("method : handleAddProductToCartWithLoginUser with null cartOfUser");
            }
            log.info("kết thúc khối if : cartOfUser != null");
        } else {
            /* User chưa có giỏ hàng */
            log.info("khối else : cartOfUser != null");
            if (cartInCookie != null) {
                /* có giỏ hàng trong cookie -> lấy giỏ hàng của cookie và set user và thêm sản phẩm vào giỏ  */
                log.info("khối if : cartInCookie != null");
                cartDTO = this.saveCartAndGetCartDTO(productDTO, quantity, appUser, cartInCookie);
                log.info("kết thúc khối if : cartInCookie != null");
            } else {
                /* Cả user và cookie đều không có giỏ hàng -> tạo mới và thêm sản phẩm vào giỏ */
                log.info("khối else : cartInCookie != null");
                Cart cart = new Cart();
                cartDTO = this.saveCartAndGetCartDTO(productDTO, quantity, appUser, cart);
                log.info("kết thúc khối else : cartInCookie != null");
            }
            log.info("kết thúc khối else : cartOfUser != null");
        }
        log.info("kết thúc method : handleAddProductToCartWithLoginUser");
        return cartDTO;
    }

    /* Lưu cart và mapping lại cartDTO */
    public CartDTO saveCartAndGetCartDTO(ProductDTO productDTO, int quantity, AppUser appUser, Cart cart) throws NullValueException, AlreadyExistException {
        log.info("method : saveCartAndGetCartDTO");
        CartDTO cartDTO;
        cart.setAppUser(appUser);
        this.iCartRepository.saveAndFlush(cart);
        /* Nếu có cart_id và product_id -> unique constraint exception */
        CartProduct cartProduct = this.iCartProductService.findByCartIdAndProductId(cart.getCartId(), productDTO.getId());
        if (cartProduct == null) {
            log.info("khối if : cartProduct == null");
            this.iCartProductService.saveNew(cart.getCartId(), productDTO.getId(), quantity);
            log.info("kết thúc khối if : cartProduct == null");
        } else {
            /* có data -> sửa lại quantity */
            log.info("khối else : cartProduct == null");
            cartProduct.setQuantity(cartProduct.getQuantity() + quantity);
            this.iCartProductService.save(cartProduct);
            log.info("kết thúc khối else : cartProduct == null");
        }

        /* Mapping lại DTO để trả về view và upate total price */
        List<CartProduct> cartProducts = this.iCartProductService.findAllByCartId(cart.getCartId());
        cartDTO = Cart.mapToCartDTO(cartProducts, cart.getCartId(), iImageService);
        this.iCartRepository.updateTotalPrice(cart.getCartId(), cartDTO.getTotalPaymentWithDiscount());
        log.info("kết thúc method : saveCartAndGetCartDTO");
        return cartDTO;
    }

    //      Xử lý huỷ giảm giá khi exception xảy ra
    public CartDTO handleExceptionWhenGetCart(int cartIdCookie,
                                              Principal principal,
                                              HttpServletRequest request,
                                              HttpServletResponse response) {
        log.info("method : handleExceptionWhenCreateCart");
        CartDTO cartDTO = new CartDTO();
        cartDTO.setCartId(cartIdCookie);
        if (principal != null && principal.getName() != null) {
            /* User đã đăng nhập vào hệ thống */
            log.info("khối if : principal != null && principal.getName() != null");
            AppUser appUser = this.iAppUserService.findByUsername(principal.getName());
            Cart cartOfUser = this.iCartRepository.findCartByUserId(appUser.getUserId());
            if (cartOfUser != null) {
                /* User đã có giỏ hàng -> set Flag ko được giảm giá */
                this.iCartRepository.setUsedDiscount(cartOfUser.getCartId());
                List<CartProduct> cartProductsOfUser = this.iCartProductService.findAllByCartId(cartOfUser.getCartId());
                cartDTO = Cart.mapToCartDTO(cartProductsOfUser, cartOfUser.getCartId(), iImageService);
                cartDTO.setUsedDiscount(true);
            }
            log.info("kết thúc khối if : principal != null && principal.getName() != null");
        } else {
            /* User chưa đăng nhập */
            log.info("khối else : principal != null && principal.getName() != null");
            Cart cartInCookie = this.iCartRepository.findById(cartIdCookie).orElse(null);
            if (cartInCookie != null) {
                /* Cookie có giỏ hàng -> set flag không giảm giá */
                log.info("khối if : cartInCookie != null");
                this.iCartRepository.setUsedDiscount(cartInCookie.getCartId());
                List<CartProduct> cartProducts = this.iCartProductService.findAllByCartId(cartInCookie.getCartId());
                cartDTO = Cart.mapToCartDTO(cartProducts, cartInCookie.getCartId(), iImageService);
                cartDTO.setUsedDiscount(true);
                log.info("kết thúc khối if : cartInCookie != null");
            }
            log.info("kết thúc khối else : principal != null && principal.getName() != null");
        }
        CookieUtils.set(request, response, CookieUtils.CART_ID_KEY, String.valueOf(cartDTO.getCartId()));
        /* Lưu id giỏ hàng vào cookie */
        log.info("kết thúc method : {}", "handleExceptionWhenCreateCart");
        return cartDTO;
    }

    /* Đồng bộ giỏ hàng của user và của cookie */
    public CartDTO mergeCookieCartAndUserCart(Cart cartOfUser, Cart cartInCookie) throws IOException {
        /* cartOfUser đã check null trước khi gọi method */
        log.info("Method : mergeCookieCartAndUserCart");
        CartDTO cartDTO;
        List<CartProduct> cartProductsOfUser = this.iCartProductService.findAllByCartId(cartOfUser.getCartId());
        if (cartInCookie != null && cartOfUser.getCartId() != cartInCookie.getCartId()) {
            /* cookie có giỏ hàng và user cũng có giỏ hàng -> nhập hết vào giỏ của user */
            log.info("khối if : cartInCookie != null && cartOfUser.getCartId() != cartInCookie.getCartId()");
            List<CartProduct> cartProductsInCookie = this.iCartProductService.findAllByCartId(cartInCookie.getCartId());
            cartProductsOfUser.addAll(cartProductsOfUser.size(), cartProductsInCookie);
            /* Sau khi đã nhập hết vào giỏ của user -> xoá giỏ hàng của cookie */
            this.iCartProductService.removeAllCartProductByCartId(cartInCookie.getCartId());
            this.iCartRepository.deleteById(cartInCookie.getCartId());
            log.info("kết thúc khối if : cartInCookie != null && cartOfUser.getCartId() != cartInCookie.getCartId()");
        }
        cartDTO = Cart.mapToCartDTO(cartProductsOfUser, cartOfUser.getCartId(), iImageService);
        this.iCartRepository.updateTotalPrice(cartDTO.getCartId(), cartDTO.getTotalPaymentWithDiscount());

        List<CartProduct> mergeList = cartDTO.getListCartProduct(cartOfUser);
        /* merge 2 giỏ hàng lại -> bao gồm cả những cái cũ đã có id và những cái mới chưa có id
         * -> Tìm id cho những cái đã có data trong table để ko gây ra lỗi unique ( cart_id , product_id ) */
        if (!mergeList.isEmpty()) {
            for (CartProduct cartProduct : mergeList) {
                CartProduct cartProductFound = this.iCartProductService
                        .findByCartIdAndProductId(cartProduct.getCart().getCartId(), cartProduct.getProduct().getId());
                if (cartProductFound != null) {
                    cartProduct.setId(cartProductFound.getId());
                }
            }
            this.iCartProductService.saveAll(mergeList);
        }
        cartDTO.setUsedDiscount(cartOfUser.isUsedDiscount());
        log.info("Kết thúc Method : mergeCookieCartAndUserCart");
        return cartDTO;
    }
}
