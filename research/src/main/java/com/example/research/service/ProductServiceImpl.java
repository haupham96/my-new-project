package com.example.mybatis.service;

import com.example.mybatis.entity.Product;
import com.example.mybatis.repository.IProductRepository;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements IProductService {

  private final IProductRepository productRepository;

  @Override
  public List<Product> getList() {
    var list = productRepository.findAll();
    return list;
  }

  @Override
  public void save(Product product) {
    product.setProductId(UUID.randomUUID());
    this.productRepository.insert(product);
  }


}
