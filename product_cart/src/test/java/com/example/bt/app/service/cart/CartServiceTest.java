package com.example.bt.app.service.cart;

import com.example.bt.app.dto.CartDTO;
import com.example.bt.app.dto.ProductDTO;
import com.example.bt.app.entity.AppUser;
import com.example.bt.app.entity.Cart;
import com.example.bt.app.entity.CartProduct;
import com.example.bt.app.entity.Product;
import com.example.bt.app.exception.AlreadyExistException;
import com.example.bt.app.exception.CartNotFoundException;
import com.example.bt.app.exception.InvalidQuantityException;
import com.example.bt.app.exception.NullValueException;
import com.example.bt.app.repository.IAppUserRepository;
import com.example.bt.app.repository.ICartProductRepository;
import com.example.bt.app.repository.ICartRepository;
import com.example.bt.app.repository.IProductRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.InvalidDataAccessApiUsageException;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import java.io.IOException;
import java.math.BigDecimal;
import java.security.Principal;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @author HauPV
 * JUnit Test cho CartServiceImpl
 * => OK
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class CartServiceTest {

    @Autowired
    private CartServiceImpl iCartService;
    @Autowired
    private ICartRepository iCartRepository;
    @Autowired
    private IProductRepository iProductRepository;
    @Autowired
    private IAppUserRepository iAppUserRepository;
    @Autowired
    private ICartProductRepository iCartProductRepository;

    Cart cart;
    Principal principal;
    MockHttpServletRequest request;
    MockHttpServletResponse response;

    @BeforeEach
    void setUp() {
        cart = new Cart();
        request = new MockHttpServletRequest();
        response = new MockHttpServletResponse();
        principal = () -> "user";
    }

    /**
     * trường hợp thêm Cart vào db thất bại
     * -> null value
     */
    @Test
    @DisplayName("save_FailWithNullValue")
    void save_failWithNullValue() {
        assertThrows(Exception.class, () -> this.iCartService.save(null));
    }

    /**
     * trường hợp thêm Cart vào db thành công
     */
    @Test
    @DisplayName("save_Success")
    void save_success() {
        cart = this.iCartService.save(cart);
        Assertions.assertNotNull(cart);
        Assertions.assertTrue(cart.getCartId() > 0);
        this.iCartRepository.delete(cart);
    }

    /**
     * Trường hợp user không có giỏ hàng và đồng bộ thành công với giỏ hàng ở cookie
     */
    @Test
    @DisplayName("getUserCart_SuccessWithSynchronizedCookieCart")
    void getUserCart_successWithCookieCart() throws Exception {
        cart = this.iCartService.save(cart);
        String cartCookieId = String.valueOf(cart.getCartId());

        CartDTO cartDTO = this.iCartService.getUserCart(principal, cartCookieId, request, response);

        /* sau khi đồng bộ -> tìm giỏ hàng của user vừa đồng bộ */
        AppUser user = this.iAppUserRepository.findByUsername(principal.getName());
        Cart cartOfUSer = this.iCartRepository.findCartByUserId(user.getUserId());

        Assertions.assertNotNull(cartDTO);
        /* kiểm tra sau khi đồng bộ thì tìm theo user hay tìm theo id đều là 1 giỏ hàng */
        Assertions.assertEquals(cart, cartOfUSer);
        /* kiểm tra cartDTO có được gán id vào chưa */
        Assertions.assertEquals(cartDTO.getCartId(), cart.getCartId());

        /* xoá data sau khi test xong */
        this.iCartRepository.delete(cart);
    }

    /**
     * Trường hợp user đã có giỏ hàng và cookie cũng có hàng
     */
    @Test
    @DisplayName("getUserCart_SuccessWithSynchronizedCookieCart")
    void getUserCart_SuccessWithUserCartAndCookieCart() throws Exception {
        Principal principal = () -> "user";
        cart = this.iCartService.save(cart);

        AppUser appUser = this.iAppUserRepository.findByUsername(principal.getName());
        Assertions.assertNotNull(appUser);
        Product product = this.iProductRepository.findById(14).orElse(null);
        Assertions.assertNotNull(product);

        /* setup UserCart */
        Cart cartOfUser = new Cart();
        cartOfUser.setAppUser(appUser);

        /* thêm sản phẩm vào giỏ hàng : table cart_product */
        CartProduct cartProduct = new CartProduct();
        this.iCartRepository.save(cartOfUser);
        Assertions.assertTrue(cartOfUser.getCartId() > 0);
        cartProduct.setCart(cartOfUser);
        cartProduct.setProduct(product);
        cartProduct.setQuantity(2);
        this.iCartProductRepository.save(cartProduct);
        assertTrue(cartProduct.getId() > 0);

        String cartCookieId = String.valueOf(cart.getCartId());

        CartDTO cartDTO = this.iCartService.getUserCart(principal, cartCookieId, request, response);

        /* sau khi đồng bộ -> tìm giỏ hàng của user vừa đồng bộ */
        AppUser user = this.iAppUserRepository.findByUsername(principal.getName());
        Cart cartOfUSer = this.iCartRepository.findCartByUserId(user.getUserId());

        Assertions.assertNotNull(cartDTO);
        /* kiểm tra tổng tiền */
        assertEquals(cartOfUSer.getTotalPrice().longValue(), cartDTO.getTotalPaymentWithDiscount());
        /* kiểm tra số lượng sp trong giỏ */
        assertEquals(cartDTO.getProducts().get(Product.mapToDTO(product)), cartProduct.getQuantity());
        /* kiểm tra sau khi đồng bộ có xoá giỏ hàng ở cookie chưa */
        assertNull(this.iCartRepository.findById(Integer.valueOf(cartCookieId)).orElse(null));

        /* xoá data sau khi test xong */
        this.iCartProductRepository.deleteById(cartProduct.getId());
        this.iCartRepository.delete(cartOfUSer);
    }

    /**
     * Trường hợp không có user login và cookie có giỏ hàng
     */
    @Test
    @DisplayName("getUserCart_WithNoLoginUser")
    void getUserCart_WithNoLoginUser() throws Exception {
        cart.setTotalPrice(new BigDecimal(0));
        cart = this.iCartService.save(cart);
        String cartCookieId = String.valueOf(cart.getCartId());

        CartDTO cartDTO = this.iCartService.getUserCart(null, cartCookieId, request, response);

        Assertions.assertNotNull(cartDTO);
        /* kiểm tra đồng bộ id chưa */
        Assertions.assertEquals(cart.getCartId(), cartDTO.getCartId());
        /* kiểm tra đồng bộ tổng tiền */
        Assertions.assertEquals(cart.getTotalPrice().longValue(), cartDTO.getTotalPaymentWithDiscount());

        /* xoá data sau khi test xong */
        this.iCartRepository.delete(cart);
    }

    /**
     * Trường hợp user và cookie ko có giỏ hàng
     */
    @Test
    @DisplayName("getUserCart_WithNoLoginUser")
    void getUserCart_WithNoCartOfUserAndCookie() throws Exception {
        Principal principal = () -> "user";
        AppUser appUser = this.iAppUserRepository.findByUsername(principal.getName());
        Assertions.assertNotNull(appUser);
        String cartCookieId = String.valueOf(0);
        Cart cartOfUser = this.iCartRepository.findCartByUserId(appUser.getUserId());
        Assertions.assertNull(cartOfUser);

        CartDTO cartDTO = this.iCartService.getUserCart(principal, cartCookieId, request, response);
        /* kiểm tra giá trị mặc định trả về khi không có giỏ hàng */
        Assertions.assertEquals(cartDTO, new CartDTO());
    }

    /**
     * Trường hợp user đăng nhập và có giỏ hàng
     */
    @Test
    @DisplayName("handleException_WithLoginUser")
    void handleException_WithLoginUser() {
        cart.setTotalPrice(new BigDecimal(0));
        AppUser appUser = this.iAppUserRepository.findByUsername(principal.getName());
        assertNotNull(appUser);
        cart.setAppUser(appUser);
        this.iCartRepository.save(cart);

        CartDTO cartDTO = this.iCartService.handleExceptionWhenGetCart(cart.getCartId(), principal, request, response);
        /* kiểm tra đã gắn cờ sử dụng discount rồi */
        assertTrue(cartDTO.isUsedDiscount());

        /* xoá data sau khi test xong */
        this.iCartRepository.deleteById(cart.getCartId());
    }

    /**
     * Trường hợp user chưa đăng nhập
     * -> kiểm tra giỏ hàng của cookie
     */
    @Test
    @DisplayName("handleException_WithNoLoginUser")
    void handleException_WithNoLoginUser() {
        cart.setTotalPrice(new BigDecimal(0));
        this.iCartRepository.save(cart);

        CartDTO cartDTO = this.iCartService.handleExceptionWhenGetCart(cart.getCartId(), null, request, response);
        /* kiểm tra đã gắn cờ sử dụng discount rồi */
        assertTrue(cartDTO.isUsedDiscount());

        /* xoá data sau khi test xong */
        this.iCartRepository.deleteById(cart.getCartId());
    }

    /**
     * Trường hợp thêm sp vào giỏ hàng thất bại
     * -> Số lượng sản phẩm < 1
     */
    @Test
    @DisplayName("handleAddProductToCart_WithException")
    void handleAddProductToCart_WithException() {
        this.cart.setTotalPrice(new BigDecimal(0));
        this.iCartRepository.save(cart);
        int quantity = 0;
        Product product = this.iProductRepository.findById(14).orElse(null);
        assertNotNull(product);
        /* check throw Exception */
        assertThrows(InvalidQuantityException.class, () -> {
            iCartService.handleAddProductToCart(String.valueOf(cart.getCartId()),
                    Product.mapToDTO(product), quantity, null, request, response);
        });

        /* xoá data sau khi test xong */
        this.iCartRepository.deleteById(cart.getCartId());
    }

    /**
     * Trường hợp thêm sp vào giỏ hàng với user đã đăng nhập
     * Trường hợp user đã có giỏ hàng
     */
    @Test
    @DisplayName("handleAddProductToCart_WithLoginUser")
    void handleAddProductToCart_WithLoginUserCart() throws InvalidQuantityException, IOException, NullValueException, AlreadyExistException {
        AppUser appUser = this.iAppUserRepository.findByUsername(principal.getName());
        assertNotNull(appUser);
        this.cart.setTotalPrice(new BigDecimal(0));
        this.cart.setAppUser(appUser);
        this.iCartRepository.save(cart);
        int quantity = 2;
        Product product = this.iProductRepository.findById(14).orElse(null);
        assertNotNull(product);

        CartDTO cartDTO = this.iCartService.handleAddProductToCart(
                String.valueOf(cart.getCartId()), Product.mapToDTO(product),
                quantity, principal, request, response);
        /* cartDTO không trả về giá trị default */
        assertNotEquals(new CartDTO(), cartDTO);
        /* số lượng thêm vào bằng số lượng trong giỏ hàng */
        assertEquals(quantity, cartDTO.getProducts().get(Product.mapToDTO(product)));
        /* giá trong giỏ hàng hợp lệ */
        assertEquals(product.getPrice() * quantity, cartDTO.getTotalPayment());

        /* xoá data sau khi test xong */
        this.iCartProductRepository.deleteAllCartProductByCartId(cart.getCartId());
        this.iCartRepository.deleteById(cart.getCartId());
    }

    /**
     * Trường hợp thêm sp vào giỏ hàng với user đã đăng nhập
     * Trường hợp user chưa có giỏ hàng + Cookie có giỏ hàng
     */
    @Test
    @DisplayName("handleAddProductToCart_WithLoginUser")
    void handleAddProductToCart_WithLoginWithCartInCookieAndNoUserNoCart()
            throws InvalidQuantityException, IOException, NullValueException, AlreadyExistException {
        /* setUp trước giỏ hàng của cookies -> 2 cái iPhone11 */
        Product product = this.iProductRepository.findById(14).orElse(null);
        assertNotNull(product);
        ProductDTO iPhone11 = Product.mapToDTO(product);
        int quantityOf2 = 2;
        this.cart.setTotalPrice(new BigDecimal(iPhone11.getPrice() * 2));
        this.iCartRepository.save(cart);
        CartProduct cartProduct = new CartProduct();
        cartProduct.setProduct(product);
        cartProduct.setCart(cart);
        cartProduct.setQuantity(quantityOf2);
        this.iCartProductRepository.save(cartProduct);

        CartDTO cartDTO = this.iCartService.handleAddProductToCart(String.valueOf(this.cart.getCartId()),
                iPhone11, quantityOf2, principal, request, response);
        /* kiểm tra giỏ hàng có được update giá trị */
        assertNotEquals(new CartDTO(), cartDTO);
        /* kiểm tra số lượng mới thêm vào có được tăng lên */
        assertEquals(4, cartDTO.getProducts().get(iPhone11));
        /* kiểm tra giá tiền có được update lại */
        assertEquals(iPhone11.getPrice() * 4, cartDTO.getTotalPayment());

        /* xoá data sau khi test xong */
        this.iCartProductRepository.deleteAllCartProductByCartId(cart.getCartId());
        this.iCartRepository.deleteById(cart.getCartId());

    }

    /**
     * Trường hợp thêm sp vào giỏ hàng với user đã đăng nhập
     * Trường hợp user chưa có giỏ hàng + Cookie không có giỏ hàng
     */
    @Test
    @DisplayName("handleAddProductToCart_WithLoginUser")
    void handleAddProductToCart_WithLoginWithNoCartInCookieAndNoUserNoCart()
            throws InvalidQuantityException, IOException, NullValueException, AlreadyExistException {
        AppUser appUser = this.iAppUserRepository.findByUsername(principal.getName());
        assertNotNull(appUser);
        Product product = this.iProductRepository.findById(14).orElse(null);
        assertNotNull(product);
        ProductDTO iPhone11 = Product.mapToDTO(product);
        int quantityOf2 = 2;
        CartDTO cartDTO = this.iCartService.handleAddProductToCart("0", iPhone11, quantityOf2,
                principal, request, response);
        /* kiểm tra giỏ hàng có được update không */
        assertNotEquals(new CartDTO(), cartDTO);
        /* kiểm tra số lượng sau khi thêm */
        assertEquals(quantityOf2, cartDTO.getProducts().get(iPhone11));
        /* kiểm tra giá tiền sau khi thêm */
        assertEquals(iPhone11.getPrice() * quantityOf2, cartDTO.getTotalPayment());

        /* xoá các data mồi sau khi hoàn thành */
        this.iCartProductRepository.deleteAllByProductId(product.getId());
        Cart cartOfUser = this.iCartRepository.findCartByUserId(appUser.getUserId());
        assertNotNull(cartOfUser);
        this.iCartRepository.deleteById(cartOfUser.getCartId());

    }

    /**
     * Trường hợp thêm sp vào giỏ hàng với user không đăng nhập
     * Trường hợp cookie đã có giỏ hàng
     */
    @Test
    @DisplayName("handleAddProductToCart_WithLoginUser")
    void handleAddProductToCart_WithCookieCart() throws InvalidQuantityException, IOException, NullValueException, AlreadyExistException {
        Product product = this.iProductRepository.findById(14).orElse(null);
        ProductDTO iPhone11 = Product.mapToDTO(product);
        int quantityOf2 = 2;
        this.iCartRepository.save(cart);
        CartDTO cartDTO = this.iCartService.handleAddProductToCart(String.valueOf(cart.getCartId()),
                iPhone11, quantityOf2,
                null, request, response);
        Cart cartInCookie = this.iCartRepository.findById(cart.getCartId()).orElse(null);
        assertNotNull(cartInCookie);
        /* kiểm tra giỏ hàng có được update không */
        assertNotEquals(new CartDTO(), cartDTO);
        /* kiểm tra số lượng sau khi thêm */
        assertEquals(quantityOf2, cartDTO.getProducts().get(iPhone11));
        /* kiểm tra giá tiền sau khi thêm */
        assertEquals(cartInCookie.getTotalPrice().longValue(), cartDTO.getTotalPayment());

        /* xoá data sau khi test xong */
        this.iCartProductRepository.deleteAllCartProductByCartId(cart.getCartId());
        this.iCartRepository.deleteById(cart.getCartId());
    }

    /**
     * Trường hợp thêm sp vào giỏ hàng với user không đăng nhập
     * Trường hợp cookie chưa có giỏ hàng
     */
    @Test
    @DisplayName("handleAddProductToCart_withLoginUser")
    void handleAddProductToCart_WithNoCookieCart() throws InvalidQuantityException, IOException, NullValueException, AlreadyExistException {
        Product product = this.iProductRepository.findById(14).orElse(null);
        ProductDTO iPhone11 = Product.mapToDTO(product);
        int quantityOf2 = 2;
        CartDTO cartDTO = this.iCartService.handleAddProductToCart("0",
                iPhone11, quantityOf2,
                null, request, response);
        Cart cartCookie = this.iCartRepository.findById(cartDTO.getCartId()).orElse(null);
        assertNotNull(cartCookie);
        /* kiểm tra giỏ hàng có được update không */
        assertNotEquals(new CartDTO(), cartDTO);
        /* kiểm tra số lượng sau khi thêm */
        assertEquals(quantityOf2, cartDTO.getProducts().get(iPhone11));
        /* kiểm tra giá tiền sau khi thêm */
        assertEquals(cartCookie.getTotalPrice().longValue(), cartDTO.getTotalPayment());

        /* xoá data sau khi test xong */
        this.iCartProductRepository.deleteAllCartProductByCartId(cartCookie.getCartId());
        this.iCartRepository.deleteById(cartCookie.getCartId());
    }

    /**
     * Tìm giỏ hàng theo Id
     * Trường hợp null -> Exception
     */
    @Test
    @DisplayName("findCartById")
    void findCartById_Null() {
        assertThrows(InvalidDataAccessApiUsageException.class, () -> {
            this.iCartService.findByCartId(null);
        });
    }

    /**
     * Tìm giỏ hàng theo Id
     * Trường hợp không tìm thấy
     */
    @Test
    @DisplayName("findCartById")
    void findCartById_NotFound() {
        Cart cartNull = this.iCartService.findByCartId(0);
        assertNull(cartNull);
    }

    /**
     * Tìm giỏ hàng theo id
     * Trường hợp tìm thấy giỏ hàng
     */
    @Test
    @DisplayName("findCartById")
    void findCartById_Success() {
        this.iCartRepository.save(cart);
        Cart cartNotNull = this.iCartService.findByCartId(cart.getCartId());
        assertNotNull(cartNotNull);
        this.iCartRepository.deleteById(cart.getCartId());
    }

    /**
     * Update lại giá tiền của giỏ hàng
     * Trường hợp tìm thấy giỏ hàng -> update
     * Trường hợp không tìm thấy -> ko làm gì
     */
    @Test
    @DisplayName("updateTotalPrice")
    void updateTotalPrice() {
        this.iCartRepository.save(cart);
        long newTotalPrice = 1000000;
        this.iCartService.updateTotalPrice(cart.getCartId(), newTotalPrice);
        cart = this.iCartRepository.findById(cart.getCartId()).orElse(null);
        assertNotNull(cart);
        assertEquals(newTotalPrice, cart.getTotalPrice().longValue());

        /* xoá data sau khi test xong */
        this.iCartRepository.deleteById(cart.getCartId());
    }

    /**
     * Xoá giỏ hàng
     * Trường hợp user login + không tìm thấy giỏ hàng
     * -> Exception
     */
    @Test
    @DisplayName("deleteCart_FailWitCartNotFound")
    void deleteCart_FailWitCartNotFound() {
        assertThrows(CartNotFoundException.class, () -> {
            this.iCartService.deleteCart(principal, "0", response);
        });
    }

    /**
     * Xoá giỏ hàng
     * Trường hợp user login có giỏ hàng
     * -> xoá thành công
     */
    @Test
    @DisplayName("deleteCart_Success")
    void deleteCart_Success() {
        AppUser appUser = this.iAppUserRepository.findByUsername(principal.getName());
        assertNotNull(appUser);
        cart.setAppUser(appUser);
        this.iCartRepository.save(cart);
        assertDoesNotThrow(() -> {
            this.iCartService.deleteCart(principal, String.valueOf(cart.getCartId()), response);
        });
        Cart cartDeleted = this.iCartRepository.findById(cart.getCartId()).orElse(null);
        assertNull(cartDeleted);
    }

    /**
     * Xoá giỏ hàng
     * Trường hợp login + cookie ko có giỏ hàng
     * -> Exception
     */
    @Test
    @DisplayName("deleteCartCookie_FailWitCartNoCart")
    void deleteCartCookie_FailWitCartNoCart() {
        assertThrows(CartNotFoundException.class, () -> {
            this.iCartService.deleteCart(principal, "0", response);
        });
    }

    /**
     * Xoá giỏ hàng
     * Trường hợp ko login có giỏ hàng trong cookie
     * -> xoá thành công
     */
    @Test
    @DisplayName("deleteCartCookie_Success")
    void deleteCartCookie_Success() {
        this.iCartRepository.save(cart);
        assertDoesNotThrow(() -> {
            this.iCartService.deleteCart(null, String.valueOf(cart.getCartId()), response);
        });
        Cart cartDeleted = this.iCartRepository.findById(cart.getCartId()).orElse(null);
        assertNull(cartDeleted);
    }

    /**
     * Xoá sp trong giỏ hàng
     * Trường hợp login không tìm thấy sản phẩm trong giỏ hàng
     * -> Exception
     */
    @Test
    @DisplayName("deleteProductInUserCart_FailWithNullValueException")
    void deleteProductInUserCart_FailWithNullValueException() {
        AppUser appUser = this.iAppUserRepository.findByUsername(principal.getName());
        assertNotNull(appUser);
        this.cart.setAppUser(appUser);
        this.iCartRepository.save(cart);
        Product product = this.iProductRepository.findById(14).orElse(null);
        assertNotNull(product);
        assertThrows(NullValueException.class, () -> {
            this.iCartService.deleteProductIncart(principal, String.valueOf(cart.getCartId()), product.getId());
        });

        /* xoá data sau khi test */
        this.iCartRepository.deleteById(cart.getCartId());
    }

    /**
     * Xoá sp trong giỏ hàng
     * Trường hợp login + tìm thấy sản phẩm trong giỏ hàng
     * -> Xoá
     */
    @Test
    @DisplayName("deleteProductInUserCart_Success")
    void deleteProductInUserCart_Success() {
        /* setup cart */
        AppUser appUser = this.iAppUserRepository.findByUsername(principal.getName());
        assertNotNull(appUser);
        cart.setAppUser(appUser);
        this.iCartRepository.save(cart);
        Product product = this.iProductRepository.findById(14).orElse(null);
        assertNotNull(product);
        /* setup cart_product */
        CartProduct cartProduct = new CartProduct();
        cartProduct.setCart(cart);
        cartProduct.setProduct(product);
        cartProduct.setQuantity(2);
        this.iCartProductRepository.save(cartProduct);

        /* Nếu ko có Exception -> tìm và xoá đc cart_product */
        assertDoesNotThrow(() -> {
            this.iCartService.deleteProductIncart(principal, String.valueOf(this.cart.getCartId()), product.getId());
        });
        /* kiểm tra giá trị vừa xoá có còn trong db hay ko */
        assertNull(this.iCartProductRepository.findByCartIdAndProductId(cart.getCartId(), product.getId()));

        /* xoá dữ liệu sau khi test xong */
        this.iCartRepository.deleteById(this.cart.getCartId());
    }

    /**
     * Xoá sp trong giỏ hàng
     * Trường hợp ko login + ko thấy sản phẩm trong giỏ hàng ( table cart_product )
     * -> Exception
     */
    @Test
    @DisplayName("deleteProductInCookieCart_FailWithNullValueException")
    void deleteProductInCookieCart_FailWithNullValueException() {
        this.iCartRepository.save(cart);
        assertThrows(NullValueException.class, () -> {
            this.iCartService.deleteProductIncart(null, String.valueOf(cart.getCartId()), 14);
        });
        /* xoá dữ liệu sau khi tets xong */
        this.iCartRepository.deleteById(this.cart.getCartId());
    }

    /**
     * Xoá sp trong giỏ hàng
     * Trường hợp ko login + tìm thấy sản phẩm trong giỏ hàng
     * -> Xoá
     */
    @Test
    @DisplayName("deleteProductInCookieCart_Success")
    void deleteProductInCookieCart_Success() {
        this.iCartRepository.save(cart);
        Product product = this.iProductRepository.findById(14).orElse(null);
        assertNotNull(product);
        /* setup cart_product */
        CartProduct cartProduct = new CartProduct();
        cartProduct.setCart(cart);
        cartProduct.setProduct(product);
        cartProduct.setQuantity(2);
        this.iCartProductRepository.save(cartProduct);

        /* Có tìm thấy cart_product ko -> Nếu có exception : ko tồn tại cart_product cần tìm */
        assertDoesNotThrow(() -> {
            this.iCartService.deleteProductIncart(null, String.valueOf(cart.getCartId()), product.getId());
        });
        /* check gía trị vừa xoá có còn trong db ko */
        assertNull(this.iCartProductRepository.findByCartIdAndProductId(cart.getCartId(), product.getId()));

        /* xoá dữ liệu sau khi test xong */
        this.iCartRepository.deleteById(cart.getCartId());
    }

    /**
     * Trường hợp không tìm thấy giỏ hàng
     * -> Exception
     */
    @Test
    @DisplayName("handlePaymentSuccess_FailWithCartNotFoundException")
    void handlePaymentSuccess_FailWithCartNotFoundException() {
        assertThrows(CartNotFoundException.class, () -> {
            this.iCartService.handlePaymentSuccess("0", response);
        });
    }

    /**
     * Trường tìm thấy giỏ hàng
     * -> xoá
     */
    @Test
    @DisplayName("handlePaymentSuccess")
    void handlePaymentSuccess_Ok() {
        this.iCartRepository.save(cart);
        Product product = this.iProductRepository.findById(14).orElse(null);
        assertNotNull(product);
        /* setup cart_product */
        CartProduct cartProduct = new CartProduct();
        cartProduct.setCart(cart);
        cartProduct.setProduct(product);
        cartProduct.setQuantity(2);
        this.iCartProductRepository.save(cartProduct);

        assertDoesNotThrow(() -> {
            this.iCartService.handlePaymentSuccess(String.valueOf(cart.getCartId()), response);
        });
        /* kiểm tra toàn bộ thông tin trong giỏ hàng được xoá chưa */
        assertNull(this.iCartRepository.findById(cart.getCartId()).orElse(null));
        assertNull(this.iCartProductRepository.findById(cartProduct.getId()).orElse(null));
    }

}
