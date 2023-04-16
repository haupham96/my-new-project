package com.example.research.controller;

import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.example.research.entity.Product;
import com.example.research.enums.Type;
import com.example.research.service.IProductService;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.math.BigDecimal;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

@WebMvcTest
class CategoryRestControllerTest {

  // if your test case does not depend on spring ioc container bean -> mock
  // if depend on spring ioc bean -> mockbean

  /* @Mock and @MockBean
   * dependent -> @MockBean
   * independent -> @Mock
   * */

  /* @InjectMocks
   * it will inject the mock into it
   * Example :
   * @Mock repository
   * @InjectMocks service
   * -> it will mock repo and then inject into service
   * */

  @Autowired
  private MockMvc mockMvc; // deprecated -> use RestTemplate instead

  @MockBean
  private IProductService iProductService;

  @Test
  void postMockMvc() throws Exception {
    // given
    Product product = Product.builder()
        .categoryId(1)
        .productName("Samsung 123")
        .productPrice(BigDecimal.valueOf(123123))
        .productType(Type.SYSTEM)
        .productDescription("test")
        .build();

    ObjectMapper objectMapper = new ObjectMapper();
    this.mockMvc.perform(
            post("/product")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(objectMapper.writeValueAsString(product))
        )
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.name", is(product.getProductName())))
        .andExpect(jsonPath("$.categoryId", is(product.getCategoryId())));
  }

  @Test
  void getIdPathMockMvc() throws Exception {
    // given
    Product product = Product.builder()
        .categoryId(1)
        .productName("Samsung 123")
        .productPrice(BigDecimal.valueOf(123123))
        .productType(Type.SYSTEM)
        .productDescription("test")
        .build();

    ObjectMapper objectMapper = new ObjectMapper();
    this.mockMvc.perform(
            MockMvcRequestBuilders.get("/product/{id}",1)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(objectMapper.writeValueAsString(product))
        )
        .andExpect(MockMvcResultMatchers.status().isOk())
        .andExpect(MockMvcResultMatchers.jsonPath("$.name").value("value"))
        .andExpect(
            MockMvcResultMatchers.jsonPath("$.categoryId", Matchers.is(product.getCategoryId())));
  }


}

