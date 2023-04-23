package com.example.research.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;

import com.example.research.dto.request.ProductRequest;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
public class ValidateControllerTest {

  @Autowired
  private MockMvc mockMvc;


  @Test
  void testValidate() throws Exception {
    ObjectMapper mapper = new ObjectMapper();
    ProductRequest req = new ProductRequest();
    req.setName("iPhone");
    req.setDescription("Des");
    req.setPrice("12312312");
    req.setValidate(true);
//    req.setZipCode("1");
//    req.setArea("2");
    this.mockMvc.perform(
            post("/api/v1/validate")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(req))
        )
        .andExpect(model().hasErrors());

  }

}
