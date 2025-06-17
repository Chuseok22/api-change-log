package com.chuseok22.apichangelog.config;

import com.chuseok22.apichangelog.service.ChangeLogOperationCustomizer;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties(ChangeLogProperties.class)
@ConditionalOnProperty(prefix = "chuseok22.api-change-log", name = "enabled", havingValue = "true", matchIfMissing = true)
public class ApiChangeLogAutoConfiguration {

  @Bean
  public ChangeLogOperationCustomizer changeLogOperationCustomizer(ChangeLogProperties properties) {
    return new ChangeLogOperationCustomizer(properties);
  }
}
