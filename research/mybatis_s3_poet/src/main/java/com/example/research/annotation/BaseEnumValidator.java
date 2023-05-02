package com.example.research.annotation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import java.util.Arrays;
import lombok.SneakyThrows;

public class BaseEnumValidator implements ConstraintValidator<BaseEnum, String> {

  private Class<? extends Enum<?>> enumClass;

  @Override
  public void initialize(BaseEnum baseEnum) {
    ConstraintValidator.super.initialize(baseEnum);
    this.enumClass = baseEnum.baseEnum();
  }

  @SneakyThrows
  @Override
  public boolean isValid(String code, ConstraintValidatorContext constraintValidatorContext) {
    Enum<?>[] enums = this.enumClass.getEnumConstants();

    return Arrays.stream(enums)
        .anyMatch(e -> ((com.example.research.enums.BaseEnum) e).getCode().equals(code));
  }
}
