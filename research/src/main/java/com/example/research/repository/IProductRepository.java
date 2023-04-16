package com.example.mybatis.repository;

import com.example.mybatis.entity.Product;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface IProductRepository {

    @Select(" SELECT * FROM public.product ")
    List<Product> findAll();

    @Insert("insert into public.product(product_id, product_name, product_price, product_description)" +
            " values(#{po.productId}, #{po.productName}, #{po.productPrice}, #{po.productDescription})")
    void insert(@Param("po") Product product);
}
