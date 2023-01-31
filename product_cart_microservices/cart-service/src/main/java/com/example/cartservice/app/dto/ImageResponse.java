package com.example.cartservice.app.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ImageResponse {
    private boolean isMainImage = false;
    private String imageSrc ;
}
