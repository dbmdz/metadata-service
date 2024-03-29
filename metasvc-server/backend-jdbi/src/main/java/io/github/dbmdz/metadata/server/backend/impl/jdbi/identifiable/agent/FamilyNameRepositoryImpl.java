package io.github.dbmdz.metadata.server.backend.impl.jdbi.identifiable.agent;

import de.digitalcollections.cudami.model.config.CudamiConfig;
import de.digitalcollections.model.identifiable.agent.FamilyName;
import io.github.dbmdz.metadata.server.backend.api.repository.exceptions.RepositoryException;
import io.github.dbmdz.metadata.server.backend.api.repository.identifiable.IdentifierRepository;
import io.github.dbmdz.metadata.server.backend.api.repository.identifiable.agent.FamilyNameRepository;
import io.github.dbmdz.metadata.server.backend.api.repository.identifiable.alias.UrlAliasRepository;
import io.github.dbmdz.metadata.server.backend.impl.jdbi.identifiable.IdentifiableRepositoryImpl;
import org.jdbi.v3.core.Jdbi;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

@Repository
public class FamilyNameRepositoryImpl extends IdentifiableRepositoryImpl<FamilyName>
    implements FamilyNameRepository {

  private static final Logger LOGGER = LoggerFactory.getLogger(FamilyNameRepositoryImpl.class);

  public static final String MAPPING_PREFIX = "fn";
  public static final String TABLE_ALIAS = "f";
  public static final String TABLE_NAME = "familynames";

  public FamilyNameRepositoryImpl(
      Jdbi dbi,
      CudamiConfig cudamiConfig,
      IdentifierRepository identifierRepository,
      UrlAliasRepository urlAliasRepository) {
    super(
        dbi,
        TABLE_NAME,
        TABLE_ALIAS,
        MAPPING_PREFIX,
        FamilyName.class,
        cudamiConfig.getOffsetForAlternativePaging(),
        identifierRepository,
        urlAliasRepository);
  }

  @Override
  public FamilyName create() throws RepositoryException {
    return new FamilyName();
  }
}
