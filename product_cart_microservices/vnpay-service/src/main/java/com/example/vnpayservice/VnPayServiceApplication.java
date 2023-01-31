package com.example.vnpayservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDiscoveryClient
public class VnPayServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(VnPayServiceApplication.class, args);
    }
}
