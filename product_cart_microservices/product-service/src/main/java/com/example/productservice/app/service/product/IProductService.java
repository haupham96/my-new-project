package com.example.productservice.app.service.product;

import com.example.productservice.app.dto.request.ProductDTO;
import com.example.productservice.app.dto.response.ProductPrice;
import com.example.productservice.app.dto.response.ProductResponse;
import com.example.productservice.app.exception.ConflictException;
import com.example.productservice.app.exception.ProductExistedException;
import com.example.productservice.app.exception.ProductNotFoundException;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;

public interface IProductService {
    List<ProductResponse> findAll();

    void createProduct(ProductDTO productDTO) throws ProductExistedException, FileNotFoundException;

    ProductResponse findById(Integer productId) throws ProductNotFoundException;

    ProductResponse editProduct(int productId, ProductDTO productDTO) throws ConflictException, ProductNotFoundException, ProductExistedException, IOException;

    void deleteById(Integer productId) throws ProductNotFoundException, IOException;

    ProductResponse findByName(String productName) throws ProductNotFoundException;

    List<ProductPrice> findAllProductPrices(List<String> productNames);
}
