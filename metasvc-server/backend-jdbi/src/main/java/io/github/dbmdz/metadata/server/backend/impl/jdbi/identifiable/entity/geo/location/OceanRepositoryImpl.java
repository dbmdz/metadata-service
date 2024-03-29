package io.github.dbmdz.metadata.server.backend.impl.jdbi.identifiable.entity.geo.location;

import de.digitalcollections.cudami.model.config.CudamiConfig;
import de.digitalcollections.model.identifiable.entity.geo.location.Ocean;
import io.github.dbmdz.metadata.server.backend.api.repository.exceptions.RepositoryException;
import io.github.dbmdz.metadata.server.backend.api.repository.identifiable.IdentifierRepository;
import io.github.dbmdz.metadata.server.backend.api.repository.identifiable.alias.UrlAliasRepository;
import io.github.dbmdz.metadata.server.backend.api.repository.identifiable.entity.geo.location.OceanRepository;
import org.jdbi.v3.core.Jdbi;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

@Repository
public class OceanRepositoryImpl extends GeoLocationRepositoryImpl<Ocean>
    implements OceanRepository {

  private static final Logger LOGGER = LoggerFactory.getLogger(OceanRepositoryImpl.class);

  public static final String MAPPING_PREFIX = "geo_oc";
  public static final String TABLE_ALIAS = "geo_oc";
  public static final String TABLE_NAME = "geo_oceans";

  public OceanRepositoryImpl(
      Jdbi dbi,
      CudamiConfig cudamiConfig,
      IdentifierRepository identifierRepository,
      UrlAliasRepository urlAliasRepository) {
    super(
        dbi,
        TABLE_NAME,
        TABLE_ALIAS,
        MAPPING_PREFIX,
        Ocean.class,
        cudamiConfig.getOffsetForAlternativePaging(),
        identifierRepository,
        urlAliasRepository);
  }

  @Override
  public Ocean create() throws RepositoryException {
    return new Ocean();
  }
}
