package com.example.research.controller;

import com.example.research.entity.Product;
import com.example.research.service.IProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/product")
public class ProductRestController {

    @Autowired
    private IProductService iProductService;

    @GetMapping
    public List<Product> getList() {
        return iProductService.getList();
    }

    @PostMapping
    public Product save(@RequestBody Product product) {
        this.iProductService.save(product);
        return product;
    }


}
