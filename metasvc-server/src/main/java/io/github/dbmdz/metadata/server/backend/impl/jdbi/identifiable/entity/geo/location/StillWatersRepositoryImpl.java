package io.github.dbmdz.metadata.server.backend.impl.jdbi.identifiable.entity.geo.location;

import de.digitalcollections.cudami.model.config.CudamiConfig;
import de.digitalcollections.model.identifiable.entity.geo.location.StillWaters;
import io.github.dbmdz.metadata.server.backend.api.repository.exceptions.RepositoryException;
import io.github.dbmdz.metadata.server.backend.api.repository.identifiable.IdentifierRepository;
import io.github.dbmdz.metadata.server.backend.api.repository.identifiable.alias.UrlAliasRepository;
import io.github.dbmdz.metadata.server.backend.api.repository.identifiable.entity.geo.location.StillWatersRepository;
import org.jdbi.v3.core.Jdbi;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

@Repository
public class StillWatersRepositoryImpl extends GeoLocationRepositoryImpl<StillWaters>
    implements StillWatersRepository {

  private static final Logger LOGGER = LoggerFactory.getLogger(StillWatersRepositoryImpl.class);

  public static final String MAPPING_PREFIX = "geo_stw";
  public static final String TABLE_ALIAS = "geo_stw";
  public static final String TABLE_NAME = "geo_stillwaters";

  public StillWatersRepositoryImpl(
      Jdbi dbi,
      CudamiConfig cudamiConfig,
      IdentifierRepository identifierRepository,
      UrlAliasRepository urlAliasRepository) {
    super(
        dbi,
        TABLE_NAME,
        TABLE_ALIAS,
        MAPPING_PREFIX,
        StillWaters.class,
        cudamiConfig.getOffsetForAlternativePaging(),
        identifierRepository,
        urlAliasRepository);
  }

  @Override
  public StillWaters create() throws RepositoryException {
    return new StillWaters();
  }
}
