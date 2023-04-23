package com.example.research.controller;

import com.example.research.dto.request.ProductRequest;
import jakarta.validation.Valid;
import java.util.HashMap;
import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/validate")
public class ValidationRestController {

  @PostMapping()
  public ResponseEntity<?> validate(@Valid @RequestBody ProductRequest request,
      BindingResult bindingResult) {
    if (bindingResult.hasFieldErrors()) {
      Map<String, String> err = new HashMap<>();
      bindingResult.getFieldErrors().forEach(e -> {
        err.put(e.getField(), e.getDefaultMessage());
      });
      return new ResponseEntity<>(err, HttpStatus.BAD_REQUEST);
    }
    return ResponseEntity.ok().build();
  }

}
