package com.example.mybatis;

import com.example.mybatis.entity.Product;
import com.example.mybatis.repository.IProductRepository;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlConfig;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertNotNull;

@ActiveProfiles("test")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Sql(config = @SqlConfig(encoding = "UTF-8"),scripts = "/test.sql")
class MybatisApplicationTests {

    @LocalServerPort
    private int port;

    @Mock
    private IProductRepository iProductRepository;

    private String baseUrl = "http://localhost:";

    private static RestTemplate restTemplate;

    @BeforeAll
    public static void init() {
        restTemplate = new RestTemplate();
    }

    @BeforeEach
    public void setUp() {
        baseUrl += port + "/api/v1/product";
    }

    @Test
    public void testCreateProduct() {
        Product product = Product.builder()
                .productName("Samsung S20")
                .productPrice(new BigDecimal(20000000))
                .productDescription("samsung")
                .categoryId(1)
                .build();

        Product response = restTemplate.postForObject(baseUrl, product, Product.class);
        assertNotNull(response.getProductId());


    }

}
