package com.example.aws_s3_upload.app.controller;

import com.example.aws_s3_upload.app.service.StorageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

@RestController
public class AwsController {

    @Autowired
    private StorageService storageService;

    @GetMapping("/")
    public String hello() {
        String a = "World";
        return a + "Hello World !!";
    }

    @PostMapping("/upload")
    public ResponseEntity<String> uploadFile(@ModelAttribute("file") MultipartFile file) {
        String fileName = storageService.upload(file);
        return new ResponseEntity<>(fileName, HttpStatus.OK);
    }

    @GetMapping(value = "/download/{fileName}", produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
    public ResponseEntity<?> downloadFile(@PathVariable String fileName) throws IOException {
        String filePath = "D:\\training_beetech\\self_repository\\aws_s3_upload\\DB_TEMP.xlsx";
//        byte[] data = storageService.downloadFile(fileName);
        byte[] data = Files.readAllBytes(Paths.get(filePath));
        ByteArrayResource resource = new ByteArrayResource(data);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment ; filename=" + fileName + ".xlsx")
                .body(resource);
    }

    @DeleteMapping("/delete/{fileName}")
    public ResponseEntity<String> deleteFile(@PathVariable String fileName) {
        String info = storageService.deleteFile(fileName);
        return new ResponseEntity<>(info, HttpStatus.OK);
    }

}
