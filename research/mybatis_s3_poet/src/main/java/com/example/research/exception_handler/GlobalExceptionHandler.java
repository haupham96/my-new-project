package com.example.research.exception_handler;

import com.example.research.exception.ValidateException;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

@RestControllerAdvice
@RequiredArgsConstructor
public class GlobalExceptionHandler {

  private final MessageSource messageSource;

  @ExceptionHandler(ValidateException.class)
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  public Map<String, String> handleValidationException(ValidateException ex,
      WebRequest webRequest) {
    Map<String, String> errors = new LinkedHashMap<>();
    errors.put("error", ex.getMessage());
    Locale locale = webRequest.getLocale();
    List<FieldError> fieldErrors = ex.getBindingResult().getFieldErrors();
    for (FieldError fieldError : fieldErrors) {
      String message = messageSource.getMessage(fieldError, locale);
      errors.put(fieldError.getField(), message);
    }
    return errors;
  }

}
