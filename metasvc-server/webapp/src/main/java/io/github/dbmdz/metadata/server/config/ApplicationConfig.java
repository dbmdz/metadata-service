package io.github.dbmdz.metadata.server.config;

import de.digitalcollections.cudami.model.config.CudamiConfig;
import de.digitalcollections.cudami.model.config.TypeDeclarations;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.bind.ConstructorBinding;

@ConfigurationProperties(prefix = "application")
public class ApplicationConfig extends CudamiConfig {

  @ConstructorBinding
  public ApplicationConfig(
      Defaults defaults,
      int offsetForAlternativePaging,
      String repositoryFolderPath,
      TypeDeclarations typeDeclarations,
      UrlAlias urlAlias,
      String lobidUrl) {
    super(
        defaults,
        offsetForAlternativePaging,
        repositoryFolderPath,
        typeDeclarations,
        urlAlias,
        lobidUrl);
  }
}
