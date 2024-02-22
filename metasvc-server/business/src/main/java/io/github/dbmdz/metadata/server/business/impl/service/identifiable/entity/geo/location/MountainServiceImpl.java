package io.github.dbmdz.metadata.server.business.impl.service.identifiable.entity.geo.location;

import de.digitalcollections.cudami.model.config.CudamiConfig;
import de.digitalcollections.model.identifiable.entity.geo.location.Mountain;
import io.github.dbmdz.metadata.server.backend.api.repository.identifiable.entity.geo.location.MountainRepository;
import io.github.dbmdz.metadata.server.business.api.service.LocaleService;
import io.github.dbmdz.metadata.server.business.api.service.identifiable.IdentifierService;
import io.github.dbmdz.metadata.server.business.api.service.identifiable.alias.UrlAliasService;
import io.github.dbmdz.metadata.server.business.api.service.identifiable.entity.geo.location.MountainService;
import io.github.dbmdz.metadata.server.config.HookProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class MountainServiceImpl extends GeoLocationServiceImpl<Mountain>
    implements MountainService {

  private static final Logger LOGGER = LoggerFactory.getLogger(MountainServiceImpl.class);

  public MountainServiceImpl(
      MountainRepository repository,
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
