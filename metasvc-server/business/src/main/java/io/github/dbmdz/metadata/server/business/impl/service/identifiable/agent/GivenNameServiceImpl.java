package io.github.dbmdz.metadata.server.business.impl.service.identifiable.agent;

import de.digitalcollections.cudami.model.config.CudamiConfig;
import de.digitalcollections.model.identifiable.agent.GivenName;
import io.github.dbmdz.metadata.server.backend.api.repository.identifiable.agent.GivenNameRepository;
import io.github.dbmdz.metadata.server.business.api.service.LocaleService;
import io.github.dbmdz.metadata.server.business.api.service.identifiable.IdentifierService;
import io.github.dbmdz.metadata.server.business.api.service.identifiable.agent.GivenNameService;
import io.github.dbmdz.metadata.server.business.api.service.identifiable.alias.UrlAliasService;
import io.github.dbmdz.metadata.server.business.impl.service.identifiable.IdentifiableServiceImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

// @Transactional should not be set in derived class to prevent overriding, check base class instead
@Service
public class GivenNameServiceImpl extends IdentifiableServiceImpl<GivenName, GivenNameRepository>
    implements GivenNameService {

  private static final Logger LOGGER = LoggerFactory.getLogger(GivenNameServiceImpl.class);

  public GivenNameServiceImpl(
      GivenNameRepository repository,
      IdentifierService identifierService,
      UrlAliasService urlAliasService,
      LocaleService localeService,
      CudamiConfig cudamiConfig) {
    super(repository, identifierService, urlAliasService, localeService, cudamiConfig);
  }
}
