package com.example.research.controller;

import com.example.research.exception.ValidateException;
import com.example.research.request.RecyclerBranchRequest;
import jakarta.validation.Valid;
import java.util.HashMap;
import java.util.Map;
import org.springframework.http.HttpStatus;
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
      throw new ValidateException(bindingResult, "Bad request");
    }
    return ResponseEntity.ok(recyclerBranchRequest);
  }
}
