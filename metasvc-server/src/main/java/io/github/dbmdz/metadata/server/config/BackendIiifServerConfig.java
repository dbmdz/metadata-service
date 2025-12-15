package io.github.dbmdz.metadata.server.config;

import de.digitalcollections.cudami.model.config.IiifServerConfig;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "iiif")
public class BackendIiifServerConfig extends IiifServerConfig {}
