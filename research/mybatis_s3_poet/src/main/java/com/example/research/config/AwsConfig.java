package com.example.research.config;

import com.amazonaws.ClientConfiguration;
import com.amazonaws.Protocol;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.client.builder.AwsClientBuilder;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AwsConfig {

  @Value("${cloud.aws.access-key}")
  private String accessKey;
  @Value("${cloud.aws.secret-key}")
  private String secretKey;
  @Value("${cloud.aws.region}")
  private String region;
  @Value("${cloud.aws.endpoint}")
  private String s3Endpoint;

  @Bean
  public AmazonS3 s3Client() {
    BasicAWSCredentials credentials = new BasicAWSCredentials(accessKey, secretKey);

    ClientConfiguration clientConfig = new ClientConfiguration();
    clientConfig.setProtocol(Protocol.HTTP);
    clientConfig.setClientExecutionTimeout(10000);

    AwsClientBuilder.EndpointConfiguration endpointConfig = new AwsClientBuilder.EndpointConfiguration(
        s3Endpoint, region);

    return AmazonS3ClientBuilder.standard()
        .withCredentials(new AWSStaticCredentialsProvider(credentials))
        .withEndpointConfiguration(endpointConfig)
        .withClientConfiguration(clientConfig)
        .enablePathStyleAccess()
        .build();
  }

}
