package com.example.productservice.app.dto.response;


import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class ProductResponse {
    private int id;
    private String name;
    private long price;
    private String description;
    private List<ImageResponse> images;
}
