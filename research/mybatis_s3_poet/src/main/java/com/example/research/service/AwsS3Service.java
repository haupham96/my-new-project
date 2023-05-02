package com.example.research.service;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectInputStream;
import com.example.research.dto.response.AwsS3Response;
import java.io.IOException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class AwsS3Service {

  @Value("${cloud.aws.bucket}")
  private String bucket;

  @Autowired
  private AmazonS3 s3Client;

  public ByteArrayResource getFile(String filename) {
    S3Object s3Object = s3Client.getObject(bucket, filename);
    var metadata = s3Object.getObjectMetadata();
    try (S3ObjectInputStream is = s3Object.getObjectContent();) {
      return new ByteArrayResource(is.readAllBytes());
    } catch (IOException ex) {
      //TODO : do nothing
      return null;
    }
  }

  public AwsS3Response uploadFile(MultipartFile multipartFile) throws IOException {
    String uploadLocation =
        "/temp/" + System.currentTimeMillis() + "/" + multipartFile.getOriginalFilename();
    ObjectMetadata metadata = new ObjectMetadata();
    metadata.setContentLength(multipartFile.getBytes().length);
    metadata.setContentType(multipartFile.getContentType());
    metadata.setHeader(HttpHeaders.CONTENT_DISPOSITION,
        "\"" + "attachment; filename=" + multipartFile.getOriginalFilename() + "\"");
    PutObjectRequest req = new PutObjectRequest(bucket, uploadLocation,
        multipartFile.getInputStream(), metadata);
    var result = s3Client.putObject(req);
    String bytes = multipartFile.getBytes().length + "d";
    double fileSize = (Double.parseDouble(bytes) / 1024 / 1024);
    return new AwsS3Response(uploadLocation, fileSize + "MB");
  }

  public void deleteFile(String filePath) {
    s3Client.deleteObject(bucket, filePath);
  }

}
