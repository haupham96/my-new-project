package com.example.porductservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;

@SpringBootApplication
@EnableEurekaClient
public class PorductServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(PorductServiceApplication.class, args);
	}

}
