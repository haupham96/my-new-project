package com.example.research.dto.request;

import com.example.research.annotation.BaseEnum;
import com.example.research.annotation.ValidateAll;
import com.example.research.enums.AreaCode;
import com.example.research.enums.ZipCode;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ValidateAll(fieldCheckName = "isValidate", fieldValueName = "price")

public class ProductRequest {

  @BaseEnum(baseEnum = AreaCode.class)
  private String area;
  private String name;
  private String description;
  private String price;
  @BaseEnum(baseEnum = ZipCode.class)
  private String zipCode;
  private boolean isValidate;

}
