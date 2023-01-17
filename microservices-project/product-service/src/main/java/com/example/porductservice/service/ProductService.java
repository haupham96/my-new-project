package com.example.porductservice.service;

import com.example.porductservice.dto.ProductRequest;
import com.example.porductservice.dto.ProductResponse;
import com.example.porductservice.model.Product;
import com.example.porductservice.repository.IProductRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class ProductService {

    private final IProductRepository iProductRepository;

    public void createProduct(ProductRequest productRequest) {
        Product product = Product.builder()
                .name(productRequest.getName())
                .description(productRequest.getDescription())
                .price(productRequest.getPrice())
                .build();
        this.iProductRepository.save(product);
        log.info("product created {}", product.getId());
    }

    public List<ProductResponse> getAllProduct() {
        return this.iProductRepository.findAll()
                .stream()
                .map(product -> ProductResponse.builder()
                        .name(product.getName())
                        .price(product.getPrice())
                        .description(product.getDescription()).build()
                )
                .collect(Collectors.toList());
    }
}
