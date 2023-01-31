package com.example.promotionservice.app.controller;

import com.example.promotionservice.app.dto.PromotionDTO;
import com.example.promotionservice.app.entity.Promotion;
import com.example.promotionservice.app.service.IPromotionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/promotion")
public class PromotionRestController {

    @Autowired
    private IPromotionService iPromotionService;

    @GetMapping("")
    @ResponseStatus(HttpStatus.OK)
    public List<PromotionDTO> findPromotion() {
        return this.iPromotionService.findPromotionByNow();
    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.FOUND)
    public PromotionDTO findPromotionById(@PathVariable int id) throws Exception {
        return this.iPromotionService.findPromotionById(id);
    }
}
