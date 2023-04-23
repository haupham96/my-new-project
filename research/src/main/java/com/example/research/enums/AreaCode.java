package com.example.research.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum AreaCode implements BaseEnum {
  VIETNAM("1", "Vietnamese"),
  US_UK("2", "The US - UK"),
  KOREA("3", "Korean");
  private String code;
  private String description;

  @Override
  public String getCode() {
    return this.code;
  }

  @Override
  public String getDescription() {
    return this.description;
  }
}
