package com.example.cartservice.app.service.cart;

import com.example.cartservice.app.dto.CartDTO;
import com.example.cartservice.app.dto.KeycloakUserDTO;
import com.example.cartservice.app.dto.ProductDTO;
import com.example.cartservice.app.dto.PromotionDTO;
import com.example.cartservice.app.dto.request.CartRequest;
import com.example.cartservice.app.entity.Cart;
import com.example.cartservice.app.entity.CartProduct;
import com.example.cartservice.app.entity.KeycloakUser;
import com.example.cartservice.app.exception.CartNotFoundException;
import com.example.cartservice.app.exception.InvalidRequestBodyException;
import com.example.cartservice.app.repository.ICartProductReposiroty;
import com.example.cartservice.app.repository.ICartRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
@RequiredArgsConstructor
public class CartServiceImpl implements ICartService {

    private final ICartRepository iCartRepository;
    private final ICartProductReposiroty iCartProductReposiroty;
    private final WebClient.Builder webClientBuidler;

    @Value("${api.gateway}")
    private String apiGateway;

    /* Tìm thông tin giỏ hàng theo id */
    @Override
    public CartDTO findCart(Integer cartId) throws CartNotFoundException {
        if (cartId == null) {
            throw new CartNotFoundException("empty request body");
        }
        Cart cart = this.iCartRepository.findById(cartId).orElse(null);
        if (cart == null) {
            throw new CartNotFoundException("Not found cart with id = " + cartId);
        }
        List<CartProduct> cartProducts = this.iCartProductReposiroty.findAllByCartId(cart.getId());
        CartDTO cartDTO = new CartDTO();
        cartDTO.setId(cart.getId());
        if (cart.getKeycloakUser() != null) {
            KeycloakUserDTO user = new KeycloakUserDTO();
            BeanUtils.copyProperties(cart.getKeycloakUser(), user);
            cartDTO.setUser(user);
        }
        List<ProductDTO> products = new ArrayList<>();
        if (!cartProducts.isEmpty()) {
            cartProducts.forEach(cartProduct -> {
                ProductDTO productDTO = ProductDTO.builder()
                        .productName(cartProduct.getProductName())
                        .productPrice(cartProduct.getProductPrice())
                        .quantity(cartProduct.getQuantity())
                        .build();
                products.add(productDTO);
            });
            cartDTO.setProducts(products);
        }

        if (!cart.isBlockDiscount()) {
            /* Tìm chương trình khuyến mãi nếu ko bị chặn khuyến mãi */
            List promotions;
            try {
                promotions = webClientBuidler.build()
                        .get()
                        .uri(apiGateway + "/api/promotion")
                        .retrieve()
                        .bodyToMono(List.class)
                        .block();
            } catch (Exception ex) {
                promotions = null;
            }
            if (promotions != null && !promotions.isEmpty()) {
                promotions.forEach(promotion -> {
                    Map<String, Object> map = (Map<String, Object>) promotion;
                    int id = (int) map.get("id");
                    PromotionDTO promotionDTO = PromotionDTO.builder()
                            .id(id)
                            .name((String) map.get("name"))
                            .value((Double) map.get("value"))
                            .from((String) map.get("from"))
                            .to((String) map.get("to"))
                            .productsInPromotion((List<String>) map.get("productsInPromotion"))
                            .build();
                    var productInCart = cartDTO.getProducts().stream().map(ProductDTO::getProductName).toList();
                    boolean isOnPromotion = productInCart.containsAll(promotionDTO.getProductsInPromotion());
                    if (isOnPromotion) {
                        cartDTO.setPromotionDTO(promotionDTO);
                        cart.setPromotionId(promotionDTO.getId());
                        this.iCartRepository.save(cart);
                    } else {
                        cartDTO.setPromotionDTO(null);
                        cart.setPromotionId(null);
                        this.iCartRepository.save(cart);
                    }
                });
            }
        }
        return cartDTO;
    }

    /* thêm sp vào giỏ */
    @Transactional
    @Override
    public void handleAddProductToCart(CartRequest request) throws CartNotFoundException, InvalidRequestBodyException {
        if (request == null || request.getProducts() == null || request.getProducts().isEmpty()) {
            throw new InvalidRequestBodyException("Empty request body");
        }
        Cart cart = this.iCartRepository.findById(request.getCartId()).orElse(null);
        if (cart == null) {
            throw new CartNotFoundException("Not found cart id = " + request.getCartId());
        }
        request.getProducts().forEach(product -> {
            CartProduct cartProduct = this.iCartProductReposiroty.findByCartIdAndProductName(cart.getId(), product.getProductName());
            if (cartProduct == null) {
                cartProduct = new CartProduct(product.getProductName(), product.getProductPrice(), product.getQuantity(), cart);
            } else {
                cartProduct.setQuantity(cartProduct.getQuantity() + product.getQuantity());
            }
            this.iCartProductReposiroty.save(cartProduct);
        });
    }

    /* gán user cho giỏ hàng */
    @Transactional
    @Override
    public CartDTO setCartUser(Integer cartId, CartRequest cartRequest) throws CartNotFoundException, InvalidRequestBodyException {
        Cart cart = this.iCartRepository.findById(cartId).orElse(null);
        if (cart == null) {
            throw new CartNotFoundException("Not found cart " + cartId);
        }
        if (!cartId.equals(cartRequest.getCartId())) {
            throw new InvalidRequestBodyException("path variable id : " + cartId + " != requestBodyId : " + cartRequest.getCartId());
        }
        /* set user for cart */
        if (cartRequest.getUser() != null) {
            KeycloakUser user = new KeycloakUser();
            BeanUtils.copyProperties(cartRequest.getUser(), user);
            cart.setKeycloakUser(user);
        }
        this.iCartRepository.save(cart);

        return this.findCart(cartId);
    }

    /* Tạo giỏ hàng mới , có thể có hoặc ko có sản phẩm */
    @Transactional
    @Override
    public void handleCreateCart(CartRequest cartRequest) throws InvalidRequestBodyException {
        if (cartRequest == null) {
            throw new InvalidRequestBodyException("Empty request body .");
        }
        int cartId = cartRequest.getCartId() != null ? cartRequest.getCartId() : 0;
        Cart cart = this.iCartRepository.findById(cartId).orElse(null);
        if (cart == null) {
            cart = new Cart();
        }
        if (cartRequest.getUser() != null) {
            KeycloakUser user = new KeycloakUser();
            BeanUtils.copyProperties(cartRequest.getUser(), user);
            cart.setKeycloakUser(user);
        }
        this.iCartRepository.save(cart);
        if (!cartRequest.getProducts().isEmpty()) {
            List<CartProduct> list = new ArrayList<>();
            Cart finalCart = cart;
            cartRequest.getProducts().forEach(product -> {
                CartProduct cartProduct = new CartProduct(product.getProductName(), product.getProductPrice(), product.getQuantity(), finalCart);
                list.add(cartProduct);
            });
            this.iCartProductReposiroty.saveAll(list);
        }
    }

    /* xoá sp khỏi giỏ hàng */
    @Transactional
    @Override
    public void deleteProductInCart(int cartId, List<String> productNames) throws CartNotFoundException {
        Cart cart = this.iCartRepository.findById(cartId).orElse(null);
        if (cart == null) {
            throw new CartNotFoundException("Not found cart with id - " + cartId);
        }
        this.iCartProductReposiroty.deleteAllByProductNameIn(productNames);
    }

    /* Thay đổi số lượng sản phẩm trong giỏ */
    @Override
    public void handleChangeProductQuantity(CartRequest cartRequest) throws CartNotFoundException, InvalidRequestBodyException {
        if (cartRequest == null || cartRequest.getProducts() == null) {
            throw new InvalidRequestBodyException("Empty request body");
        }
        Cart cart = this.iCartRepository.findById(cartRequest.getCartId()).orElse(null);
        if (cart == null) {
            throw new CartNotFoundException("Not found cart with id - " + cartRequest.getCartId());
        }
        if (!cartRequest.getProducts().isEmpty()) {
            var products = cartRequest.getProducts();
            products.forEach((product) -> {
                CartProduct cartProduct = this.iCartProductReposiroty.findByCartIdAndProductName(cart.getId(), product.getProductName());
                if (cartProduct != null) {
                    cartProduct.setQuantity(product.getQuantity());
                } else {
                    cartProduct = new CartProduct(product.getProductName(), product.getProductPrice(), product.getQuantity(), cart);
                }
                this.iCartProductReposiroty.save(cartProduct);
            });
        }

    }

    /* Xoá giỏ hàng */
    @Transactional
    @Override
    public void deleteCartById(int cartId) throws CartNotFoundException {
        Cart cart = this.iCartRepository.findById(cartId).orElse(null);
        if (cart == null) {
            throw new CartNotFoundException("Not found cart with Id - " + cartId);
        }
        this.iCartProductReposiroty.deleteAllByCartId(cartId);
        this.iCartRepository.deleteById(cartId);
    }

}
