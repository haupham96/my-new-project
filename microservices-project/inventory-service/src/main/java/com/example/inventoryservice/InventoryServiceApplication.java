package com.example.inventoryservice;

import com.example.inventoryservice.model.Inventory;
import com.example.inventoryservice.repository.IInvemtoryRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
@EnableEurekaClient
public class InventoryServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(InventoryServiceApplication.class, args);
    }

//    @Bean
//    public CommandLineRunner loadData(IInvemtoryRepository iInvemtoryRepository) {
//        return args -> {
//            Inventory inventory1 = new Inventory();
//            inventory1.setSkuCode("iPhone 11");
//            inventory1.setQuantity(100);
//
//            Inventory inventory2 = new Inventory();
//            inventory2.setSkuCode("iPhone 14");
//            inventory2.setQuantity(100);
//
//            iInvemtoryRepository.save(inventory1);
//            iInvemtoryRepository.save(inventory2);
//        };
//    }

}
