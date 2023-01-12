
package com.example.bt.app.dto;

import com.example.bt.app.entity.Cart;
import com.example.bt.app.entity.CartProduct;
import com.example.bt.app.entity.Promotion;
import com.example.bt.app.exception.EntryNotFoundException;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * @author : HauPV
 * class CartDTO cho xử lý giỏ hàng ở session
 */
@Slf4j
@Data
@NoArgsConstructor
public class CartDTO implements Serializable {

    private int cartId;
    private boolean isUsedDiscount = false;

    private Promotion promotion;
    private Map<ProductDTO, Integer> products = new LinkedHashMap<>();

    //    Lấy ra sản phẩm kèm số lượng với key là ProductDTO
    public Map.Entry<ProductDTO, Integer> selectItemInCart(ProductDTO product) throws EntryNotFoundException {
        log.info(this.getClass().getSimpleName());
        log.info("method : selectItemInCart()");
        for (Map.Entry<ProductDTO, Integer> entry : products.entrySet()) {
            if (entry.getKey().getId() == product.getId()) {
                log.info("Khối if : entry.getKey().getId() == product.getId() ");
//                Nếu id sản phẩm truyền vào = id của key -> trả về 1 entry
                return entry;
            }
        }
        log.info("Kết thúc method : selectItemInCart()");
        throw new EntryNotFoundException("Not Found entry");
    }

    //    Thay đổi số lượng sản phẩm
    public void changeQuantity(ProductDTO product, int quantity) {
        log.info(this.getClass().getSimpleName());
        log.info("method : changeQuantity()");
        if (products.containsKey(product)) {
            log.info("Khối if : products.containsKey(product)");
//            Nếu tìm thấy productDTO trong map -> thay đổi số lượng
            int newValue = products.get(product) + quantity;
            products.replace(product, newValue);
            log.info("Kết thúc khối if : products.containsKey(product)");
        } else {
            log.info("khối else : !products.containsKey(product)");
//            Nếu không có key productDTO trong map -> tạo mới entry
            products.put(product, quantity);
            log.info("Kết thúc khối else : !products.containsKey(product)");
        }
        log.info("Kết thúc method : changeQuantity()");
    }

    //    Tính tổng số tiền của giỏ hàng
    public long getTotalPayment() {
        log.info(this.getClass().getSimpleName());
        log.info("method : getTotalPayment()");
        long total = 0;
        for (Map.Entry<ProductDTO, Integer> entry : products.entrySet()) {
            total += entry.getKey().getPrice() * entry.getValue();
        }
        log.info("kết thúc method : getTotalPayment()");
        return total;
    }

    //    Tính tổng tiền kèm theo giảm giá
    public long getTotalPaymentWithDiscount() {
        log.info(this.getClass().getSimpleName());
        log.info("method : getTotalPaymentWithDiscount()");
        if (this.getPromotion() != null && !this.isUsedDiscount) {
            /* Nếu có khuyến mãi */
            return (long) (this.getTotalPayment() - this.getTotalPayment() * this.getPromotion().getValue());
        }
        log.info("kết thúc method : getTotalPaymentWithDiscount()");
        return this.getTotalPayment();
    }

    //    Chuyểnn đổi tất cả entry trong Map giỏ hàng sang List<CartProduct>
    public List<CartProduct> getListCartProduct(Cart cart) throws IOException {
        log.info(this.getClass().getSimpleName());
        log.info("method : getListCartProduct()");
        List<CartProduct> cartProductList = new ArrayList<>();
        for (Map.Entry<ProductDTO, Integer> entry : products.entrySet()) {
            CartProduct cartProduct = new CartProduct();
            cartProduct.setCart(cart);
            cartProduct.setQuantity(entry.getValue());
            cartProduct.setProduct(ProductDTO.mapToEntity(entry.getKey()));
            cartProductList.add(cartProduct);
        }

        log.info("kết thúc method : getListCartProduct()");
        return cartProductList;
    }

    @Override
    public String toString() {
        return "{" +
                "cartId=" + cartId +
                ", products=" + products +
                '}';
    }
}
