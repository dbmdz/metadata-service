package io.github.dbmdz.metadata.server.business.impl.service.identifiable.agent;

import de.digitalcollections.cudami.model.config.CudamiConfig;
import de.digitalcollections.cudami.server.backend.api.repository.identifiable.agent.FamilyNameRepository;
import de.digitalcollections.model.identifiable.agent.FamilyName;
import io.github.dbmdz.metadata.server.business.api.service.LocaleService;
import io.github.dbmdz.metadata.server.business.api.service.identifiable.IdentifierService;
import io.github.dbmdz.metadata.server.business.api.service.identifiable.agent.FamilyNameService;
import io.github.dbmdz.metadata.server.business.api.service.identifiable.alias.UrlAliasService;
import io.github.dbmdz.metadata.server.business.impl.service.identifiable.IdentifiableServiceImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

// @Transactional should not be set in derived class to prevent overriding, check base class instead
@Service
public class FamilyNameServiceImpl extends IdentifiableServiceImpl<FamilyName, FamilyNameRepository>
    implements FamilyNameService {

  private static final Logger LOGGER = LoggerFactory.getLogger(FamilyNameServiceImpl.class);

  public FamilyNameServiceImpl(
      FamilyNameRepository repository,
      IdentifierService identifierService,
      UrlAliasService urlAliasService,
      LocaleService localeService,
      CudamiConfig cudamiConfig) {
    super(repository, identifierService, urlAliasService, localeService, cudamiConfig);
  }
}
