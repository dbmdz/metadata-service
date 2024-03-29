package io.github.dbmdz.metadata.server.business.impl.service.identifiable.entity.geo.location;

import de.digitalcollections.cudami.model.config.CudamiConfig;
import de.digitalcollections.model.identifiable.entity.geo.location.GeoLocation;
import io.github.dbmdz.metadata.server.backend.api.repository.identifiable.entity.geo.location.GeoLocationRepository;
import io.github.dbmdz.metadata.server.business.api.service.LocaleService;
import io.github.dbmdz.metadata.server.business.api.service.identifiable.IdentifierService;
import io.github.dbmdz.metadata.server.business.api.service.identifiable.alias.UrlAliasService;
import io.github.dbmdz.metadata.server.business.api.service.identifiable.entity.geo.location.GeoLocationService;
import io.github.dbmdz.metadata.server.business.impl.service.identifiable.entity.EntityServiceImpl;
import io.github.dbmdz.metadata.server.config.HookProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

// @Transactional should not be set in derived class to prevent overriding, check base class instead
@Service
public class GeoLocationServiceImpl<G extends GeoLocation> extends EntityServiceImpl<G>
    implements GeoLocationService<G> {

  private static final Logger LOGGER = LoggerFactory.getLogger(GeoLocationServiceImpl.class);

  @Autowired
  public GeoLocationServiceImpl(
      @Qualifier("geoLocationRepository") GeoLocationRepository<G> repository,
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
