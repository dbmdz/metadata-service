package io.github.dbmdz.metadata.server.config;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ComponentScan(
    basePackages = {
      "de.digitalcollections.commons.springboot.actuator",
      "de.digitalcollections.commons.springboot.contributor",
      "de.digitalcollections.commons.springboot.monitoring"
    })
@EnableConfigurationProperties(ApplicationConfig.class)
public class SpringConfig {}
