package com.example.productservice.app.controller;

import com.example.productservice.app.dto.request.ProductDTO;
import com.example.productservice.app.dto.response.ProductPrice;
import com.example.productservice.app.dto.response.ProductResponse;
import com.example.productservice.app.exception.ConflictException;
import com.example.productservice.app.exception.ProductExistedException;
import com.example.productservice.app.exception.ProductNotFoundException;
import com.example.productservice.app.service.product.IProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;

@CrossOrigin("http://localhost:8080")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/product")
public class ProductRestController {
    private final IProductService iProductService;

    @GetMapping("")
    @ResponseStatus(HttpStatus.OK)
    public List<ProductResponse> findAll() {
        return this.iProductService.findAll();
    }

    @PostMapping("/price")
    @ResponseStatus(HttpStatus.OK)
    public List<ProductPrice> getAllProductPrices(@RequestBody List<String> productNames) {
        return this.iProductService.findAllProductPrices(productNames);
    }

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping(value = "", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    /* for form : @ModelAttribute . for json object : @RequestBody */
    public void createProduct(@Validated @ModelAttribute ProductDTO productDTO)
            throws ProductExistedException, FileNotFoundException {
        this.iProductService.createProduct(productDTO);
    }

    @GetMapping("/name/{productName}")
    public ProductResponse findProductByName(@PathVariable String productName) throws ProductNotFoundException {
        return this.iProductService.findByName(productName);
    }

    @ResponseStatus(HttpStatus.OK)
    @PutMapping("/{productId}")
    public ProductResponse editProduct(
            @PathVariable Integer productId,
            /* for form : @ModelAttribute . for json object : @RequestBody */
            @Validated @ModelAttribute ProductDTO productDTO)
            throws ConflictException, ProductExistedException, ProductNotFoundException, IOException {
        return this.iProductService.editProduct(productId, productDTO);
    }

    @GetMapping("/{productId}")
    @ResponseStatus(HttpStatus.FOUND)
    public ProductResponse findProductById(@PathVariable Integer productId)
            throws ProductNotFoundException {
        return this.iProductService.findById(productId);
    }

    @DeleteMapping("/{productId}")
    @ResponseStatus(HttpStatus.OK)
    public void deleteById(@PathVariable Integer productId) throws IOException, ProductNotFoundException {
        this.iProductService.deleteById(productId);
    }
}
