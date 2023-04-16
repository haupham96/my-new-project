package com.example.mybatis.request;

import jakarta.validation.Valid;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RecyclerBranchRequest {

  private Boolean isPublic;
  private String branchName;
  private String prefectureCode;
  private String cityCode;
  private String representName;
  private String zipCode;
  private String address;
  private String tel;
  private String fax;
  private String mailAddress;
  private String tantoName;
  private String siteArea;
  private String areaDesignationCode;
  private String buildingArea;
  private String buildingPermitCode;
  private String compatibilityCode;
  private String mapLocation;
  private String municipalityCode;
  private String confirmedDate;
  private String url;

  @Valid
  private List<LicenseRequest> licenses;

}
