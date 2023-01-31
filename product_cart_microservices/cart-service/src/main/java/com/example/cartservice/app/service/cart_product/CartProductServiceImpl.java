package com.example.cartservice.app.service.cart_product;

import com.example.cartservice.app.repository.ICartProductReposiroty;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class CartProductServiceImpl implements ICartProductService {
    private final ICartProductReposiroty iCartProductReposiroty;
}
