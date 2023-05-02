package com.example.research.service;


import com.example.research.entity.Product;
import java.util.List;

public interface IProductService {
    List<Product> getList();

    void save(Product product);
}
