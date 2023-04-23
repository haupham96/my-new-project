package com.example.research.controller;

import com.example.research.dto.response.AwsS3Response;
import com.example.research.service.AwsS3Service;
import java.io.IOException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/v1/aws")
public class AwsS3RestController {

  @Autowired
  private AwsS3Service awsS3Service;

  @GetMapping(value = "", produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
  public ResponseEntity<?> getFile(@RequestParam String filename) {
    ByteArrayResource resource = awsS3Service.getFile(filename);
    return ResponseEntity.ok()
        .header(HttpHeaders.CONTENT_DISPOSITION,
            "\"" +
                "attachment; filename=" + filename.substring(filename.indexOf("/") + 1) +
                "\""
        )
        .body(resource);
  }

  @PostMapping()
  public ResponseEntity<?> uploadFile(@RequestBody MultipartFile file) throws IOException {
    AwsS3Response response = this.awsS3Service.uploadFile(file);
    return new ResponseEntity<>(response, HttpStatus.OK);
  }

  @DeleteMapping()
  public ResponseEntity<Void> deleteFile(@RequestParam String filename) {
    this.awsS3Service.deleteFile(filename);
    return ResponseEntity.ok().build();
  }
}
