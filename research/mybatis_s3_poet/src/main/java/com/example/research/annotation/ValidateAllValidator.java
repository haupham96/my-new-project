package com.example.research.annotation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicReference;
import lombok.SneakyThrows;

public class ValidateAllValidator implements ConstraintValidator<ValidateAll, Object> {

  private String fieldCheckName;
  private String fieldValueName;

  @Override
  public void initialize(ValidateAll validateAll) {
    ConstraintValidator.super.initialize(validateAll);
    this.fieldCheckName = validateAll.fieldCheckName();
    this.fieldValueName = validateAll.fieldValueName();
  }

  @SneakyThrows
  @Override
  public boolean isValid(Object obj, ConstraintValidatorContext ctx) {
    Field fieldCheck = obj.getClass().getDeclaredField(this.fieldCheckName);
    fieldCheck.setAccessible(true);
    Boolean isValidateAll = (Boolean) fieldCheck.get(obj);
    AtomicReference<String> nullField = new AtomicReference<>("");
    boolean isAllHaveValue = Arrays.stream(obj.getClass().getDeclaredFields())
        .allMatch(f -> {
          try {
            f.setAccessible(true);
            Object val = f.get(obj);
            boolean isNotNull = Objects.nonNull(val);
            if (!isNotNull) {
              nullField.set(f.getName());
            }
            return isNotNull;
          } catch (IllegalAccessException e) {
            return false;
          }
        });
    ctx.disableDefaultConstraintViolation();
    ctx.buildConstraintViolationWithTemplate("required not null").addPropertyNode(nullField.get())
        .addConstraintViolation();
    return isValidateAll && isAllHaveValue;
  }
}
