package com.example.mybatis.controller;

import com.example.mybatis.request.LicenseRequest;
import com.example.mybatis.request.RecyclerBranchRequest;
import jakarta.validation.Valid;
import java.util.HashMap;
import java.util.Map;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/recycler")
public class GroupValidationRestController {

  @PostMapping(produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<?> createNewBranch(
      @RequestBody @Valid RecyclerBranchRequest recyclerBranchRequest
//      ,  LicenseRequest licenseRequest
      , BindingResult bindingResult
  ) {
    if (bindingResult.hasFieldErrors()) {
      Map<String, String> errors = new HashMap<>();
      bindingResult.getFieldErrors().forEach(fieldError -> {
        errors.put(fieldError.getField(), fieldError.getDefaultMessage());
      });
      return ResponseEntity.badRequest().body(errors);
    }
    return ResponseEntity.ok(recyclerBranchRequest);
  }
}
