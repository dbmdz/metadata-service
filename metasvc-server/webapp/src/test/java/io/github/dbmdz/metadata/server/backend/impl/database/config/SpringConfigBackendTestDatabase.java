package io.github.dbmdz.metadata.server.backend.impl.database.config;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.pool.HikariPool;
import de.digitalcollections.cudami.model.config.CudamiConfig;
import de.digitalcollections.cudami.model.config.CudamiConfig.Defaults;
import de.digitalcollections.cudami.model.config.CudamiConfig.UrlAlias;
import de.digitalcollections.cudami.model.config.IiifServerConfig.Identifier;
import de.digitalcollections.cudami.model.config.IiifServerConfig.Image;
import de.digitalcollections.cudami.model.config.IiifServerConfig.Presentation;
import io.github.dbmdz.metadata.server.config.SpringConfigBackendDatabase;
import java.util.ArrayList;
import java.util.Locale;
import javax.sql.DataSource;
import org.mockito.Mockito;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Primary;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.utility.DockerImageName;

@TestConfiguration(proxyBeanMethods = false)
@ComponentScan(
    basePackages = {"io.github.dbmdz.metadata.server.backend.impl.jdbi"},
    basePackageClasses = SpringConfigBackendDatabase.class)
public class SpringConfigBackendTestDatabase {

  private static HikariPool CONNECTION_POOL;
  private static CudamiConfig CUDAMI_CONFIG =
      new CudamiConfig(
          new Defaults("en", Locale.forLanguageTag("en-US")),
          5000,
          "/tmp/cudami/fileResources",
          null,
          new UrlAlias(new ArrayList<>(), 64),
          "");

  @ServiceConnection
  @Bean
  PostgreSQLContainer postgreSQLContainer() {
    return new PostgreSQLContainer(DockerImageName.parse("postgres:15-bookworm"));
  }

  /*
   * static { postgreSQLContainer = new
   * PostgreSQLContainer(DockerImageName.parse("postgres:15-bookworm"));
   * postgreSQLContainer.start(); }
   */

  @Bean
  @Primary
  public DataSource testDataSource(PostgreSQLContainer container) {
    if (CONNECTION_POOL == null) {
      HikariConfig config = new HikariConfig();
      config.setJdbcUrl(container.getJdbcUrl());
      config.setUsername("test");
      config.setPassword("test");
      config.setDriverClassName(container.getDriverClassName());
      config.setMaximumPoolSize(100);
      config.setMinimumIdle(10);
      CONNECTION_POOL = new HikariPool(config);
    }
    return CONNECTION_POOL.getUnwrappedDataSource();
  }

  @Bean
  @Primary
  CudamiConfig testCudamiConfig() {
    return CUDAMI_CONFIG;
  }

  @Bean
  @Primary
  Identifier testIiifServerConfigIdentifier() {
    return Mockito.mock(Identifier.class);
  }

  @Bean
  @Primary
  Image testIiifServerConfigImage() {
    return Mockito.mock(Image.class);
  }

  @Bean
  @Primary
  Presentation testIiifServerConfigPresentation() {
    return Mockito.mock(Presentation.class);
  }
}
