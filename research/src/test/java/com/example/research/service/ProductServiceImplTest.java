package com.example.research.service;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.example.research.entity.Product;
import com.example.research.repository.IProductRepository;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Disabled;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ContextConfiguration(classes = {ProductServiceImpl.class})
@ExtendWith(SpringExtension.class)
class ProductServiceImplTest {
    @MockBean
    private IProductRepository iProductRepository;

    @Autowired
    private ProductServiceImpl productServiceImpl;

    /**
     * Method under test: {@link ProductServiceImpl#getList()}
     */
    @Test
    void testGetList() {
        ArrayList<Product> productList = new ArrayList<>();
        when(iProductRepository.findAll()).thenReturn(productList);
        List<Product> actualList = productServiceImpl.getList();
        assertSame(productList, actualList);
        assertTrue(actualList.isEmpty());
        verify(iProductRepository).findAll();
    }

    /**
     * Method under test: {@link ProductServiceImpl#save(Product)}
     */
    @Test
    void testSave() {
        doNothing().when(iProductRepository).insert((Product) any());
        productServiceImpl.save(new Product());
        verify(iProductRepository).insert((Product) any());
    }

    /**
     * Method under test: {@link ProductServiceImpl#save(Product)}
     */
    @Test
    @Disabled("TODO: Complete this test")
    void testSave2() {
        // TODO: Complete this test.
        //   Reason: R013 No inputs found that don't throw a trivial exception.
        //   Diffblue Cover tried to run the arrange/act section, but the method under
        //   test threw
        //   java.lang.NullPointerException: Cannot invoke "com.example.mybatis.entity.Product.setProductId(java.util.UUID)" because "product" is null
        //       at com.example.mybatis.service.ProductServiceImpl.save(ProductServiceImpl.java:25)
        //   See https://diff.blue/R013 to resolve this issue.

        doNothing().when(iProductRepository).insert((Product) any());
        productServiceImpl.save(null);
    }
}

