package com.example.vnpayservice.app.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VnPayOrderInfo {
    private Integer cartId;
    private Long totalPrice;
    private String note;
}
