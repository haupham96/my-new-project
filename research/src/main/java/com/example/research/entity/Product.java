package com.example.research.entity;

import com.example.research.enums.Type;
import java.math.BigDecimal;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Product {

  @EqualsAndHashCode.Include
  private UUID productId;
  private String productName;
  private BigDecimal productPrice;
  private String productDescription;
  private Type productType;
  private int categoryId;

}
