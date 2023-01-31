package com.example.promotionservice.app.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PromotionDTO {
    private int id;
    private String name;
    private double value;
    private String from;
    private String to;
    List<String> productsInPromotion;
}
