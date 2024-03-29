package io.github.dbmdz.metadata.server.config;

import org.flywaydb.core.Flyway;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.flyway.FlywayAutoConfiguration.FlywayConfiguration;
import org.springframework.boot.autoconfigure.flyway.FlywayMigrationInitializer;
import org.springframework.boot.autoconfigure.flyway.FlywayMigrationStrategy;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.context.annotation.Primary;

@Configuration
public class CudamiFlywayConfiguration extends FlywayConfiguration {

  @Override
  @Primary
  @Bean(name = "flywayInitializer")
  @DependsOn("springUtility")
  public FlywayMigrationInitializer flywayInitializer(
      Flyway flyway, ObjectProvider<FlywayMigrationStrategy> migrationStrategy) {
    return super.flywayInitializer(flyway, migrationStrategy);
  }
}
