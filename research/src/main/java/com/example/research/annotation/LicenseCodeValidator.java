package com.example.research.annotation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import lombok.RequiredArgsConstructor;
import org.hibernate.validator.internal.engine.constraintvalidation.ConstraintValidatorContextImpl;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;

@RequiredArgsConstructor
public class LicenseCodeValidator implements ConstraintValidator<LicenseCode, String> {

  private int min;
  private int max;
  private String message;

  private final MessageSource messageSource;

  @Override
  public void initialize(LicenseCode licenseCode) {
    min = licenseCode.min();
    max = licenseCode.max();
    message = licenseCode.message();
  }

  @Override
  public boolean isValid(String value, ConstraintValidatorContext context) {
    boolean isValid = false;
    ConstraintValidatorContextImpl contextImpl = (ConstraintValidatorContextImpl) context;
    if (value != null && value.matches("^\\d$")) {
      isValid = true;
    }
    if (!isValid) {
      // option 1 : DI Message-source
      String message = messageSource.getMessage(this.message, new Object[]{min, max},
          LocaleContextHolder.getLocale());
      context.disableDefaultConstraintViolation();
      context.buildConstraintViolationWithTemplate(message).addConstraintViolation();

      // option 2 : add message parameter but remember to form your messageCode into {} Ex : {E500}
//      contextImpl.addMessageParameter("0", min);
//      contextImpl.addMessageParameter("1", max);
    }
    return isValid;
  }
}
