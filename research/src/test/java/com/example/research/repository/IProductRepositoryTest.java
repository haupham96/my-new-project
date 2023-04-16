package com.example.research.repository;


import static org.assertj.core.api.Assertions.assertThat;

import com.example.research.entity.Product;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.mybatis.spring.boot.test.autoconfigure.MybatisTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;

@MybatisTest
@AutoConfigureTestDatabase(replace = Replace.NONE)
class IProductRepositoryTest {

  @Autowired
  private IProductRepository repository;

  @Test
  void findAll_returnList() {
    //  given
    List<Product> list = repository.findAll();
    //  when
    assertThat(list).isNotEmpty();
    //  then

  }

  @Test
  void insert() {
  }
}