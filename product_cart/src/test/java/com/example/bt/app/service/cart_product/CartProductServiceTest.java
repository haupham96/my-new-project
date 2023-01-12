package com.example.bt.app.service.cart_product;

import com.example.bt.app.entity.Cart;
import com.example.bt.app.entity.CartProduct;
import com.example.bt.app.entity.Product;
import com.example.bt.app.exception.AlreadyExistException;
import com.example.bt.app.exception.NullValueException;
import com.example.bt.app.repository.ICartProductRepository;
import com.example.bt.app.repository.ICartRepository;
import com.example.bt.app.repository.IProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataIntegrityViolationException;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @author HauPV
 * JUnit test cho CartProductService
 * => OK
 */
@SpringBootTest
class CartProductServiceTest {

    @Autowired
    private ICartProductService iCartProductService;
    @Autowired
    private ICartRepository iCartRepository;
    @Autowired
    private ICartProductRepository iCartProductRepository;
    @Autowired
    private IProductRepository iProductRepository;

    private Cart cart;
    private CartProduct cartProduct;
    private Product product;

    /**
     * Setup data trước mỗi test case
     */
    @BeforeEach
    public void setup() {
        this.product = this.iProductRepository.findById(14).orElse(null);
        assertNotNull(product);
        this.cart = new Cart();
    }

    /**
     * Trường hợp tạo mới thất bại với product hoặc cart bị gán giá trị null -> ThrowException
     */
    @Test
    @DisplayName("saveFail_WithNullCartOrProduct")
    void saveFail_WithNullCartOrProduct() {
//        Setup giá trị cho cartProduct để test
        cartProduct = new CartProduct();
        cartProduct.setProduct(null);
        cartProduct.setCart(null);
        assertThrows(DataIntegrityViolationException.class, () -> {
            this.cartProduct = this.iCartProductService.save(cartProduct);
        });
//        Clear dữ liệu trong DB sau khi test thành công
        this.iCartRepository.delete(cart);
    }

    /**
     * Trường hợp tạo mới thất bại với giá trị null
     */
    @Test
    @DisplayName("saveFail_WithNullCartProduct")
    void saveFail_WithNullCartProduct() {
//        Setup giá trị cho cartProduct để test
        cartProduct = null;
        assertThrows(NullValueException.class, () -> {
            this.cartProduct = this.iCartProductService.save(cartProduct);
        });
    }

    /**
     * Trường hợp tạo mới thành công
     */
    @Test
    @DisplayName("save_Success")
    void save_Success() {
        this.iCartRepository.save(cart);
//        Setup giá trị cho cartProduct để test
        cartProduct = new CartProduct();
        cartProduct.setProduct(product);
        cartProduct.setCart(this.cart);
        cartProduct.setQuantity(2);

        assertDoesNotThrow(() -> {
            this.cartProduct = this.iCartProductService.save(cartProduct);
        });

//        Clear dữ liệu trong DB sau khi test thành công
        this.iCartProductRepository.delete(cartProduct);
        this.iCartRepository.delete(cart);
    }

    /**
     * Trường hợp tìm CartProduct thành công với cart_id hợp lệ
     */
    @Test
    @DisplayName("findAll_WithValidCartId")
    void findAll_WithValidCartId() {
        this.iCartRepository.save(cart);
//        Setup giá trị cho cartProduct để test
        cartProduct = new CartProduct();
        cartProduct.setProduct(product);
        cartProduct.setCart(this.cart);
        cartProduct.setQuantity(2);
        this.iCartProductRepository.save(cartProduct);

        List<CartProduct> cartProductList = this.iCartProductRepository.findAllByCartId(this.cart.getCartId());
        assertFalse(cartProductList.isEmpty());

//        Clear dữ liệu trong DB sau khi test thành công
        this.iCartProductRepository.delete(cartProduct);
        this.iCartRepository.delete(cart);
    }

    /**
     * Trường hợp tìm CartProduct thất bại với cart_id không hợp lệ
     */
    @Test
    @DisplayName("List<CartProduct> findAllByCartId(int cartId)")
    void findAll_WithInvalidCartId() {
        this.iCartRepository.save(cart);
//        Setup giá trị cho cartProduct để test
        cartProduct = new CartProduct();
        cartProduct.setProduct(product);
        cartProduct.setCart(this.cart);
        cartProduct.setQuantity(2);
        this.iCartProductRepository.save(cartProduct);

        List<CartProduct> cartProductList;
        cartProductList = this.iCartProductRepository.findAllByCartId(1000);
        assertTrue(cartProductList.isEmpty());

//        Clear dữ liệu trong DB sau khi test thành công
        this.iCartProductRepository.delete(cartProduct);
        this.iCartRepository.delete(cart);
    }

    /**
     * Trường hợp tìm thấy CartProduct với cartId và productId hợp lệ
     */
    @Test
    @DisplayName("findBy_ValidCartIdAndProductId")
    void findBy_ValidCartIdAndProductId() {
        this.iCartRepository.save(cart);
//        Setup giá trị cho cartProduct để test
        cartProduct = new CartProduct();
        cartProduct.setProduct(product);
        cartProduct.setCart(this.cart);
        cartProduct.setQuantity(2);
        this.iCartProductRepository.save(cartProduct);

        CartProduct cartProductFond = this.iCartProductRepository
                .findByCartIdAndProductId(this.cart.getCartId(), this.product.getId());
        /* Kiểm tra với các giá trị đã được gán trước đó */
        assertNotNull(cartProductFond);
        assertEquals(this.product.getName(), cartProductFond.getProduct().getName());
        assertEquals(cartProduct.getQuantity(), cartProductFond.getQuantity());
        assertEquals(cartProduct.getCart().getCartId(), cartProductFond.getCart().getCartId());

//        Clear dữ liệu trong DB sau khi test thành công
        this.iCartProductRepository.delete(cartProduct);
        this.iCartRepository.delete(cart);
    }

    /**
     * Trường hợp không tìm thấy CartProduct với cartId hoặc productId không hợp lệ
     * -> trả về null
     */
    @Test
    @DisplayName("findBy_InValidCartIdAndProductId")
    void findBy_InValidCartIdAndProductId() {
        this.iCartRepository.save(cart);
//        Setup giá trị cho cartProduct để test
        cartProduct = new CartProduct();
        cartProduct.setProduct(product);
        cartProduct.setCart(this.cart);
        cartProduct.setQuantity(2);
        this.iCartProductRepository.save(cartProduct);

//        cartId = 1 && productId = 1 không có trong table cart_product
        CartProduct cartProductFond = this.iCartProductRepository
                .findByCartIdAndProductId(1, 1);
        assertNull(cartProductFond);

//        Clear dữ liệu trong DB sau khi test thành công
        this.iCartProductRepository.delete(cartProduct);
        this.iCartRepository.delete(cart);
    }

    /**
     * Trường hợp thêm mới đồng loạt nhiều object CartProduct với dữ liệu hợp lệ
     */
    @Test
    @DisplayName("saveAll_Success")
    void saveAll_Success() throws Exception {
        this.iCartRepository.save(cart);
//        Setup giá trị cho List<CartProduct> để test
        cartProduct = new CartProduct();
        cartProduct.setProduct(product);
        cartProduct.setCart(this.cart);
        cartProduct.setQuantity(2);

        Product product2 = this.iProductRepository.findById(15).orElse(null);
        assertNotNull(product2);
        CartProduct cartProduct2 = new CartProduct();
        cartProduct2.setProduct(product2);
        cartProduct2.setCart(this.cart);
        cartProduct2.setQuantity(2);

        List<CartProduct> cartProductList = new ArrayList<>();
        cartProductList.add(cartProduct);
        cartProductList.add(cartProduct2);

        this.iCartProductService.saveAll(cartProductList);
        List<CartProduct> cartProducts = this.iCartProductRepository.findAllByCartId(cart.getCartId());
        /* cart_product sau khi thêm vào db sẽ có dữ liệu */
        assertFalse(cartProducts.isEmpty());
        assertEquals(cartProductList.size(), cartProducts.size());

//        Sau khi thêm thành công , các CartProdct trong list sẽ có id .
        assertTrue(cartProductList.get(0).getId() > 0);
        assertTrue(cartProductList.get(1).getId() > 0);

//        Clear dữ liệu trong DB sau khi test thành công
        this.iCartProductRepository.delete(cartProduct2);
        this.iCartProductRepository.delete(cartProduct);
        this.iCartRepository.delete(cart);
    }

    /**
     * Trường hợp thêm mới đồng loạt nhiều CartProduct với dữ liệu ko hợp lệ
     * -> bị null hoặc có thuộc tính null
     * => throw Exception
     */
    @Test
    @DisplayName("saveAll_Fail")
    void saveAll_Fail() {
//          Tạo list ảo với các giá trị không hợp lệ
        List<CartProduct> cartProductList = new ArrayList<>();
        cartProductList.add(new CartProduct());
        cartProductList.add(null);

        assertThrows(Exception.class, () -> {
            this.iCartProductService.saveAll(cartProductList);
        });

        // Clear dữ liệu trong DB sau khi test thành công
        this.iCartRepository.delete(cart);
    }

    /**
     * Trường hợp xoá CartProduct thành công với dữ liệu hợp lệ
     */
    @Test
    @DisplayName("removeCartProduct(CartProduct cartProduct)")
    void removeCartProduct_Success() {
        this.iCartRepository.save(cart);
//        Setup giá trị cho List<CartProduct> để test
        cartProduct = new CartProduct();
        cartProduct.setProduct(product);
        cartProduct.setCart(this.cart);
        cartProduct.setQuantity(2);
        this.iCartProductRepository.save(cartProduct);

        assertDoesNotThrow(() -> {
            this.iCartProductService.removeCartProduct(cartProduct);
        });
        assertNull(this.iCartProductRepository.findById(this.cartProduct.getId()).orElse(null));

//        Clear dữ liệu trong DB sau khi test thành công
        this.iCartRepository.delete(cart);
    }

    /**
     * Trường hợp xoá thất bại với dữ liệu ko hợp lệ
     * -> chứa giá trị bị null
     * => throw Exception .
     */
    @Test
    @DisplayName("removeCartProduct_WithInvalidCartProduct")
    void removeCartProduct_WithNullValueException() {
//        Tạo 1 CartProduct không hợp lệ để test
        cartProduct = new CartProduct();
        assertThrows(NullValueException.class, () -> {
            this.iCartProductService.removeCartProduct(cartProduct);
        });
    }

    /**
     * Xoá tất cả cart_product theo cart_id
     * -> Nếu tìm thấy theo cart_id thì sẽ xoá
     * -> Không tìm thấy thì không có gì thay đổi
     */
    @Test
    @DisplayName("removeAllCartProductByCartId")
    void removeAllCartProductByCartId() {
        this.iCartRepository.save(cart);
//        Setup giá trị cho List<CartProduct> để test
        cartProduct = new CartProduct();
        cartProduct.setProduct(product);
        cartProduct.setCart(this.cart);
        cartProduct.setQuantity(2);
        this.iCartProductRepository.save(cartProduct);
        this.iCartProductService.removeAllCartProductByCartId(cart.getCartId());
        /* Kiểm tra xem giá trị vừa xoá óc còn trong db ko */
        assertNull(this.iCartProductRepository.findByCartIdAndProductId(cart.getCartId(), product.getId()));
        this.iCartRepository.deleteById(cart.getCartId());
    }

    /**
     * Trường hợp thêm cart_product thất bại
     * -> unique (cart_id , product_id)
     */
    @Test
    @DisplayName("saveNew_FailWithAlreadyExistException")
    void saveNew_FailWithAlreadyExistException() {
        this.iCartRepository.save(cart);
        //  Setup giá trị cho List<CartProduct> để test
        cartProduct = new CartProduct();
        cartProduct.setProduct(product);
        cartProduct.setCart(this.cart);
        cartProduct.setQuantity(2);
        this.iCartProductRepository.save(cartProduct);
        assertThrows(AlreadyExistException.class, () -> {
            this.iCartProductService.saveNew(cart.getCartId(), product.getId(), 2);
        });
        /* clear data sau khi test */
        this.iCartProductRepository.deleteAllCartProductByCartId(cart.getCartId());
        this.iCartRepository.deleteById(cart.getCartId());
    }

    /**
     * Trường hợp thêm cart_product thành công
     */
    @Test
    @DisplayName("saveNew_FailWithAlreadyExistException")
    void saveNew_Success() {
        this.iCartRepository.save(cart);
        //  Setup giá trị cho List<CartProduct> để test
        assertDoesNotThrow(() -> {
            this.iCartProductService.saveNew(cart.getCartId(), product.getId(), 2);
        });
        CartProduct cartProductNew = this.iCartProductRepository
                .findByCartIdAndProductId(cart.getCartId(), product.getId());
        /* kiểm tra dữ liệu mới thêm có tồn tại trong db chưa */
        assertNotNull(cartProductNew);
        /* clear data sau khi test */
        this.iCartProductRepository.deleteAllCartProductByCartId(cart.getCartId());
        this.iCartRepository.deleteById(cart.getCartId());
    }
}
