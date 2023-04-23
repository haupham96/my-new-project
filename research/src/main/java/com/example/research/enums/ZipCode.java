package com.example.research.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum ZipCode implements BaseEnum {

  HCM("1", "Ho Chi Minh"),
  HN("2", "Ha Noi"),
  DN("3", "Da Nang");

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
