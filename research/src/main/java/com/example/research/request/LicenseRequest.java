package com.example.mybatis.request;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LicenseRequest {

  @Length(min = 2,max = 3)
  @Pattern(regexp = "^\\d+$",message = "{E400}")
  private String licensedType;

  private String licensedFacilityCode;

  private String businessLicenseGovernmentOfficeCode;

  private String industryType;

  private String licensedIndustryCode;

  private List<String> licenseUuids;

  private Boolean isRubble;

  private Boolean isGlass;

  private Boolean isMetal;

  private Boolean isPlastic;

  private Boolean isPaper;

  private Boolean isWoodChips;

  private Boolean isLint;

  private Boolean isSludge;

  private Boolean isOther;

  private String otherName;

  private Boolean isAsbest;

  private Boolean isPcb;

  private Boolean isAcid;

  private Boolean isAlkali;

  private Boolean isOil;

  private Boolean isSpecialOther;

  private String specialOtherName;


}
