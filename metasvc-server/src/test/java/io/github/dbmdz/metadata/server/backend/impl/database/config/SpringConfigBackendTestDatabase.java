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
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Locale;
import java.util.concurrent.atomic.AtomicInteger;
import javax.sql.DataSource;
import org.mockito.Mockito;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Primary;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.utility.DockerImageName;

@TestConfiguration
@ComponentScan(
    basePackages = {"io.github.dbmdz.metadata.server"},
    basePackageClasses = SpringConfigBackendDatabase.class)
public class SpringConfigBackendTestDatabase {

  private static final int RESTART_CONTAINER_CONNECTION_COUNT = 30;

  private static HikariPool connectionPool;
  private static PostgreSQLContainer container;
  private static AtomicInteger connCount = new AtomicInteger(0);
  private static CudamiConfig cudamiConfig =
      new CudamiConfig(
          new Defaults("en", Locale.forLanguageTag("en-US")),
          5000,
          "/tmp/cudami/fileResources",
          null,
          new UrlAlias(new ArrayList<>(), 64),
          "");

  @Bean
  @Primary
  public DataSource testDataSource() throws SQLException, InterruptedException {
    if (connCount.get() >= RESTART_CONTAINER_CONNECTION_COUNT) {
      connectionPool.shutdown();
      connectionPool = null;
      container.stop();
      container = null;
      connCount.set(0);
    }
    if (container == null || !container.isRunning()) {
      container = new PostgreSQLContainer(DockerImageName.parse("postgres:15-bookworm"));
      container.start();
    }
    if (connectionPool == null || connectionPool.poolState == HikariPool.POOL_SHUTDOWN) {
      HikariConfig config = new HikariConfig();
      config.setJdbcUrl(container.getJdbcUrl());
      config.setUsername(container.getUsername());
      config.setPassword(container.getPassword());
      config.setDriverClassName(container.getDriverClassName());
      config.setMaximumPoolSize(100);
      config.setMinimumIdle(10);
      connectionPool = new HikariPool(config);
    }
    connCount.incrementAndGet();
    return connectionPool.getUnwrappedDataSource();
  }

  @Bean
  @Primary
  CudamiConfig testCudamiConfig() {
    return cudamiConfig;
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
