package com.example.research.request;

import com.example.research.annotation.LicenseCode;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LicenseRequest {

  private final String messageCode = "EMinMax";
  @Length(min = 2, max = 3)
//  @Pattern(regexp = "^\\d+$",message = "{E400}")
  @LicenseCode(min = 1, max = 3, message = messageCode)
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
