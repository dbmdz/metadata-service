package io.github.dbmdz.metadata.server.business.impl.service.identifiable.entity.geo.location;

import de.digitalcollections.cudami.model.config.CudamiConfig;
import de.digitalcollections.model.identifiable.entity.geo.location.Sea;
import io.github.dbmdz.metadata.server.backend.api.repository.identifiable.entity.geo.location.SeaRepository;
import io.github.dbmdz.metadata.server.business.api.service.LocaleService;
import io.github.dbmdz.metadata.server.business.api.service.identifiable.IdentifierService;
import io.github.dbmdz.metadata.server.business.api.service.identifiable.alias.UrlAliasService;
import io.github.dbmdz.metadata.server.business.api.service.identifiable.entity.geo.location.SeaService;
import io.github.dbmdz.metadata.server.config.HookProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class SeaServiceImpl extends GeoLocationServiceImpl<Sea> implements SeaService {

  private static final Logger LOGGER = LoggerFactory.getLogger(SeaServiceImpl.class);

  public SeaServiceImpl(
      SeaRepository repository,
      IdentifierService identifierService,
      UrlAliasService urlAliasService,
      HookProperties hookProperties,
      LocaleService localeService,
      CudamiConfig cudamiConfig) {
    super(
        repository,
        identifierService,
        urlAliasService,
        hookProperties,
        localeService,
        cudamiConfig);
  }
}
