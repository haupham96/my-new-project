package com.example.research.service;

import com.example.research.entity.Product;
import com.example.research.repository.IProductRepository;
import com.example.research.service.IProductService;
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
