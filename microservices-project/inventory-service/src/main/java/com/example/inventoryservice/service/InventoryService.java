package com.example.inventoryservice.service;

import com.example.inventoryservice.dto.InventoryResponse;
import com.example.inventoryservice.model.Inventory;
import com.example.inventoryservice.repository.IInvemtoryRepository;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class InventoryService {

    private final IInvemtoryRepository iInvemtoryRepository;

    @Transactional(readOnly = true)
    @SneakyThrows
    public List<InventoryResponse> isInStock(List<String> skuCode) {
//        log.info("Wait started");
//        Thread.sleep(10000);
//        log.info("Wait ended");
        List<Inventory> inventory = iInvemtoryRepository.findBySkuCodeIn(skuCode);
        return inventory
                .stream()
                .map(in -> InventoryResponse.builder()
                        .skuCode(in.getSkuCode())
                        .isInStock(in.getQuantity() > 1)
                        .build())
                .collect(Collectors.toList());
    }

}
