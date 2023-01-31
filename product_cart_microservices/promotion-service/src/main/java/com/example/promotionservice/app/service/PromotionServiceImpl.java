package com.example.promotionservice.app.service;

import com.example.promotionservice.app.dto.PromotionDTO;
import com.example.promotionservice.app.entity.ProductPromotion;
import com.example.promotionservice.app.entity.Promotion;
import com.example.promotionservice.app.repository.IProductPromotionRepository;
import com.example.promotionservice.app.repository.IPromotionRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class PromotionServiceImpl implements IPromotionService {

    @Autowired
    private IPromotionRepository iPromotionRepository;
    @Autowired
    private IProductPromotionRepository iProductPromotionRepository;


    @Override
    public List<PromotionDTO> findPromotionByNow() {
        List<PromotionDTO> promotionDTOList = new ArrayList<>();
        List<Promotion> promotions = this.iPromotionRepository.findPromotionByNow();
        if (!promotions.isEmpty()) {
            SimpleDateFormat fmt = new SimpleDateFormat("HH:mm:ss dd/MM/yyyy");
            promotions.forEach(promotion -> {
                List<ProductPromotion> productPromotions = this.iProductPromotionRepository.findAllByPromotionId(promotion.getId());
                List<String> products = productPromotions.stream().map(ProductPromotion::getProductName).toList();
                PromotionDTO promotionDTO = PromotionDTO.builder()
                        .id(promotion.getId())
                        .name(promotion.getName())
                        .value(promotion.getValue())
                        .from(fmt.format(promotion.getFrom().getTime()))
                        .to(fmt.format(promotion.getTo().getTime()))
                        .productsInPromotion(products)
                        .build();
                promotionDTOList.add(promotionDTO);
            });
        }
        return promotionDTOList;
    }

    @Override
    public PromotionDTO findPromotionById(int id) throws Exception {
        Promotion promotion = this.iPromotionRepository.findById(id).orElse(null);
        if (promotion == null) {
            return null;
        }
        SimpleDateFormat fmt = new SimpleDateFormat("HH:mm:ss dd/MM/yyyy");
        List<ProductPromotion> productPromotions = this.iProductPromotionRepository.findAllByPromotionId(promotion.getId());
        List<String> products = productPromotions.stream().map(ProductPromotion::getProductName).toList();
        if (products.isEmpty()) {
            throw new Exception(" Not found products in promotion ");
        }
        return PromotionDTO.builder()
                .id(promotion.getId())
                .name(promotion.getName())
                .value(promotion.getValue())
                .from(fmt.format(promotion.getFrom().getTime()))
                .to(fmt.format(promotion.getTo().getTime()))
                .productsInPromotion(products)
                .build();
    }
}
