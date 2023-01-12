package com.example.bt.app.service.cart_product;

import com.example.bt.app.entity.CartProduct;
import com.example.bt.app.exception.AlreadyExistException;
import com.example.bt.app.exception.NullValueException;
import com.example.bt.app.repository.ICartProductRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @author : HauPV
 * service cho cart_product
 */
@Slf4j
@Service
public class CartProductServiceImpl implements ICartProductService {

    @Autowired
    private ICartProductRepository iCartProductRepository;

    //    Lưu thông tin CartProduct vào db
    @Transactional
    @Override
    public CartProduct save(CartProduct cartProduct) throws NullValueException {
        log.info(this.getClass().getSimpleName());
        log.info("method - save()");
        if (cartProduct == null) {
            throw new NullValueException("cart_product : null");
        }
        this.iCartProductRepository.saveAndFlush(cartProduct);
        log.info("kết thúc method - save()");
        return cartProduct;
    }

    //    Lấy ra List<CartProduct> theo cart_id
    @Override
    public List<CartProduct> findAllByCartId(int cartId) {
        log.info(this.getClass().getSimpleName());
        log.info("method - findAllByCartId()");
        log.info("kết thúc method - findAllByCartId()");
        return this.iCartProductRepository.findAllByCartId(cartId);
    }

    //    Lấy ra CartProduct theo cart_id và product_id
    @Override
    public CartProduct findByCartIdAndProductId(int cartId, int productId) {
        log.info(this.getClass().getSimpleName());
        log.info("method - findByCartIdAndProductId()");
        log.info("kết thúc method - findByCartIdAndProductId()");
        return this.iCartProductRepository.findByCartIdAndProductId(cartId, productId);
    }

    //    Lưu 1 danh sách các CartProduct vào db
    @Transactional(rollbackFor = {Exception.class, Throwable.class})
    @Override
    public void saveAll(List<CartProduct> cartProductList) {
        log.info(this.getClass().getSimpleName());
        log.info("method - removeCartProductByCartId()");
        if (cartProductList != null && !cartProductList.isEmpty()) {
//          Nếu có dữ liệu thì inset vào db
            log.info("Khối if cartProductList != null && !cartProductList.isEmpty()");
            this.iCartProductRepository.saveAllAndFlush(cartProductList);
            log.info("kết thúc khối if cartProductList != null && !cartProductList.isEmpty()");
        }
        log.info("kết thúc method - removeCartProductByCartId()");
    }

    //    Xoá 1 CartProduct trong db
    @Transactional(rollbackFor = {Exception.class, Throwable.class})
    @Override
    public void removeCartProduct(CartProduct cartProduct) throws NullValueException {
        log.info(this.getClass().getSimpleName());
        log.info("method - removeCartProduct()");
        if (cartProduct == null || cartProduct.getProduct() == null || cartProduct.getCart() == null) {
            throw new NullValueException("cartProduct is null or contains null properties");
        }
        this.iCartProductRepository.delete(cartProduct);
        log.info("kết thúc method - removeCartProduct()");
    }

    //  Xoá toàn bộ sản phẩm trong giỏ ở trong db theo cart_id .
    @Transactional(rollbackFor = {Exception.class, Throwable.class})
    @Override
    public void removeAllCartProductByCartId(int cartId) {
        this.iCartProductRepository.deleteAllCartProductByCartId(cartId);
    }

    //  Thêm mới cart_product
    @Transactional(rollbackFor = {Exception.class, Throwable.class})
    @Override
    public void saveNew(int cartId, int productId, int quantity) throws AlreadyExistException {
        CartProduct cartProduct = this.iCartProductRepository.findByCartIdAndProductId(cartId, productId);
        if (cartProduct != null) {
            log.info("khối if : cartProduct != null " +
                    "-> AlreadyExistException cartProduct : cart_id = " + cartId + " product_id = " + productId);
            throw new AlreadyExistException("Đã tồn tại cart_product");
        }
        this.iCartProductRepository.saveNew(cartId, productId, quantity);
    }

}
