package com.example.research.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindingResult;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ValidateException extends RuntimeException {

  private BindingResult bindingResult;
  private String message;

}
