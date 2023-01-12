package com.example.bt.app.service.promotion;

import com.example.bt.app.dto.CartDTO;
import com.example.bt.app.dto.ProductDTO;
import com.example.bt.app.entity.Cart;
import com.example.bt.app.entity.Product;
import com.example.bt.app.entity.ProductPromotion;
import com.example.bt.app.entity.Promotion;
import com.example.bt.app.repository.IPromotionRepository;
import com.example.bt.app.service.cart.ICartService;
import com.example.bt.app.service.product_promotion.IProductPromotionService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;

/**
 * @author : HauPV
 * service cho promotion
 */
@Slf4j
@Service
public class PromotionServiceImpl implements IPromotionService {

    @Autowired
    private IPromotionRepository iPromotionRepository;
    @Autowired
    private IProductPromotionService iProductPromotionService;
    @Autowired
    private ICartService iCartService;

    /* Tìm chương trình khuyến mãi */
    @Transactional(rollbackFor = {Exception.class, Throwable.class})
    @Override
    public void findPromotion(CartDTO cartDTO) {
        log.info("class : {}", this.getClass().getSimpleName());
        log.info("method : findPromotion");
        AtomicBoolean isExistPromotion = new AtomicBoolean(false);
        if (!cartDTO.getProducts().isEmpty()) {
            /* Nếu trong giỏ hàng có sản phẩm */
            log.info("khối if : !cartDTO.getProducts().isEmpty()");

            List<Integer> productIds = cartDTO.getProducts().keySet()
                    .stream()
                    .map(ProductDTO::getId)
                    .collect(Collectors.toList());
            if (!productIds.isEmpty()) {
                /* có sản phẩm trong giỏ hàng -> tìm chương trình khuyến mãi hiện có */
                log.info("khối if : !productIds.isEmpty()");

                /* danh sách các chương trình khuyến mãi sắp xếp tăng dần theo % giảm giá */
                List<Promotion> promotions = this.iPromotionRepository.findPromotionByNow();
                if (!promotions.isEmpty()) {
                    /* Nếu hiện đang có chương trình khuyến mãi -> tìm điều kiện để được nhận khuyến mãi */
                    log.info("khối if : promotion.isPresent()");

                    promotions.forEach(promotion -> {
                        List<ProductPromotion> productPromotions = this.iProductPromotionService
                                .findAllByPromotionId(promotion.getId());
                        if (!productPromotions.isEmpty()) {
                            /* Danh sách các sản phẩm được hưởng khuyến mãi khi mua cùng nhau */
                            log.info("khối if : !productPromotions.isEmpty()");

                            List<Product> productsInPromotion = productPromotions
                                    .stream()
                                    .map(ProductPromotion::getProduct)
                                    .collect(Collectors.toList());
                            /* Kiểm tra các sản phẩm trong giỏ hàng có nằm trong chương trình khuyến mãi */
                            boolean isInPromotion = productsInPromotion
                                    .stream()
                                    .allMatch(product -> productIds.contains(product.getId()));
                            if (isInPromotion && !cartDTO.isUsedDiscount()) {
                                /* Nếu có áp dụng khuyến mãi -> lưu khuyến mãi vào giỏ hàng và update lại db */
                                log.info("khối if : isInPromotion");

                                cartDTO.setPromotion(promotion);
                                Cart cart = this.iCartService.findByCartId(cartDTO.getCartId());
                                if (cart != null) {
                                    /* trường hợp tìm thấy giỏ hàng theo id */
                                    log.info("khối if : cart != null");

                                    cart.setTotalPrice(BigDecimal.valueOf(cartDTO.getTotalPaymentWithDiscount()));
                                    cart.setPromotion(promotion);
                                    this.iCartService.save(cart);
                                    isExistPromotion.set(true);

                                    log.info("kết thúc khối if : cart != null");
                                    log.info("kết thúc khối if : isInPromotion");
                                }
                            }
                            log.info("kết thúc khối if : !productPromotions.isEmpty()");
                        }
                    });
                    log.info("kết thúc khối if : promotion.isPresent()");
                }
                log.info("kết thúc khối if : !productIds.isEmpty()");
            }
            log.info("kết thúc khối if : !cartDTO.getProducts().isEmpty()");
        }
        if (!isExistPromotion.get()) {
            /* trường hợp ko có chương trình KM -> xoá promotion */
            log.info("khối if : !isExistPromotion.get()");
            Cart cart = this.iCartService.findByCartId(cartDTO.getCartId());
            if (cart != null) {
                log.info("khối if : cart != null -> remove promotion");
                cart.setPromotion(null);
                this.iCartService.save(cart);
                log.info("kết thúc khối if : cart != null");
            }
            log.info("kết thúc khối if : !isExistPromotion.get()");
        }
        log.info("kết thúc method : findPromotion");
    }

    @Override
    public Promotion findPromotionByProductId(int productId) {
        return this.iPromotionRepository.findPromotionByProductId(productId);
    }
}
