package com.example.research.config;

import java.nio.charset.StandardCharsets;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;

@Configuration
@Profile("dev")
@MapperScan("com.example.mybatis.repository")
public class AppConfig {

  @Bean
  MessageSource messageSource() {
    ReloadableResourceBundleMessageSource messageSource = new ReloadableResourceBundleMessageSource();
    messageSource.setBasename("classpath:messages");
    messageSource.setDefaultEncoding(StandardCharsets.UTF_8.name());
    return messageSource;
  }

//  @Bean
//  LocalValidatorFactoryBean getValidator(){
//    LocalValidatorFactoryBean bean = new LocalValidatorFactoryBean();
//    bean.setValidationMessageSource(messageSource());
//    return bean;
//  }
}
