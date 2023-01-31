package com.example.cartservice.app.repository;

import com.example.cartservice.app.entity.Cart;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ICartRepository extends JpaRepository<Cart,Integer> {
}
