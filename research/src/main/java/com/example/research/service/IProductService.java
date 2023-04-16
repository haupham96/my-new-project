package com.example.mybatis.service;

import com.example.mybatis.entity.Product;

import java.util.List;

public interface IProductService {
    List<Product> getList();

    void save(Product product);
}
