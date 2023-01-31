package com.example.vnpayservice.app.dto;

import lombok.*;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PayRequest {
    private int cartId;
    private String note;
}
