package io.github.dbmdz.metadata.server.backend.impl.jdbi.identifiable.entity.geo.location;

import de.digitalcollections.cudami.model.config.CudamiConfig;
import de.digitalcollections.model.identifiable.entity.geo.location.Canyon;
import io.github.dbmdz.metadata.server.backend.api.repository.exceptions.RepositoryException;
import io.github.dbmdz.metadata.server.backend.api.repository.identifiable.IdentifierRepository;
import io.github.dbmdz.metadata.server.backend.api.repository.identifiable.alias.UrlAliasRepository;
import io.github.dbmdz.metadata.server.backend.api.repository.identifiable.entity.geo.location.CanyonRepository;
import org.jdbi.v3.core.Jdbi;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

@Repository
public class CanyonRepositoryImpl extends GeoLocationRepositoryImpl<Canyon>
    implements CanyonRepository {

  private static final Logger LOGGER = LoggerFactory.getLogger(CanyonRepositoryImpl.class);

  public static final String MAPPING_PREFIX = "geo_cy";
  public static final String TABLE_ALIAS = "geo_cy";
  public static final String TABLE_NAME = "geo_canyons";

  public CanyonRepositoryImpl(
      Jdbi dbi,
      CudamiConfig cudamiConfig,
      IdentifierRepository identifierRepository,
      UrlAliasRepository urlAliasRepository) {
    super(
        dbi,
        TABLE_NAME,
        TABLE_ALIAS,
        MAPPING_PREFIX,
        Canyon.class,
        cudamiConfig.getOffsetForAlternativePaging(),
        identifierRepository,
        urlAliasRepository);
  }

  @Override
  public Canyon create() throws RepositoryException {
    return new Canyon();
  }
}
