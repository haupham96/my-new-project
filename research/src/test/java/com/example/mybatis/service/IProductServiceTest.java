package com.example.mybatis.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.internal.verification.VerificationModeFactory.times;

import com.example.mybatis.entity.Product;
import com.example.mybatis.enums.Type;
import com.example.mybatis.repository.IProductRepository;
import java.math.BigDecimal;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.Sql.ExecutionPhase;

@ExtendWith(MockitoExtension.class)
class IProductServiceTest {

  @Mock
  private IProductRepository iProductRepository;

  private IProductService iProductService;

  @BeforeEach
  void setUp() {
    iProductService = new ProductServiceImpl(iProductRepository);
  }

  @Test
  void getList() {
    //  given
    var list = iProductService.getList();
    //  when

    //  then
    verify(iProductRepository).findAll();
  }

  @Test
  void save_ok() {
    // given
    Product product = Product.builder()
        .categoryId(1)
        .productName("Samsung 123")
        .productPrice(BigDecimal.valueOf(123123))
        .productType(Type.SYSTEM)
        .productDescription("test")
        .build();

    // when
    iProductService.save(product);

    // then
    ArgumentCaptor<Product> argsCaptor = ArgumentCaptor.forClass(Product.class);
    verify(iProductRepository).insert(argsCaptor.capture());
    var cap = argsCaptor.getValue();

    // assert
    assertThat(product).isEqualTo(cap);


  }

  @Test
  void save_throws() {
    // given
    Product product = Product.builder()
        .categoryId(1)
        .productName("Samsung 123")
        .productPrice(BigDecimal.valueOf(123123))
        .productType(Type.SYSTEM)
        .productDescription("test")
        .build();

    // when
    // for insert has param
    doThrow(new RuntimeException("exc"))
        .when(iProductRepository)
        .insert(any());

    // for no param
    // given(iProductRepository.findAll()).willThrow(new RuntimeException("ex"));

//    iProductService.save(product);

    // then
    assertThatThrownBy(() -> iProductService.save(product))
        .isInstanceOf(RuntimeException.class)
        .hasMessageContaining("exc");

    // verify

    //verify(iProductRepository, never()).insert(any()); // repo never call insert
    verify(iProductRepository, times(1)).insert(any()); // repo call insert once
  }

  @Test
  @Sql(statements = "insert into some thing where something = true", executionPhase = ExecutionPhase.BEFORE_TEST_METHOD)
  @Sql(statements = "delete from some thing insert at the line above", executionPhase = ExecutionPhase.AFTER_TEST_METHOD)
  void save_template() {
    // given
    Product product = Product.builder()
        .categoryId(1)
        .productName("Samsung 123")
        .productPrice(BigDecimal.valueOf(123123))
        .productType(Type.SYSTEM)
        .productDescription("test")
        .build();
  }
}