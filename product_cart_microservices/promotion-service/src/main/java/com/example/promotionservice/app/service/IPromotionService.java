package com.example.promotionservice.app.service;

import com.example.promotionservice.app.dto.PromotionDTO;
import com.example.promotionservice.app.entity.Promotion;

import java.util.List;

public interface IPromotionService {
    List<PromotionDTO> findPromotionByNow();

    PromotionDTO findPromotionById(int id) throws Exception;
}
