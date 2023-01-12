package com.example.bt.app.repository;

import com.example.bt.app.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author : HauPV
 * repository cho table product
 */
public interface IProductRepository extends JpaRepository<Product, Integer> {
    //    Tìm product theo tên
    Product findByName(String name);

    //  Method ứng dụng Propagation để lưu sản phẩm từ file csv vào trong db
    @Transactional(
            rollbackFor = {Exception.class, Throwable.class},
            propagation = Propagation.REQUIRES_NEW)
    @Modifying
    @Query(value = " insert into product(name,price,description) " +
            " values (:#{#product.name},:#{#product.price},:#{#product.description}) " ,
            nativeQuery = true)
    void saveOne(Product product);

}
