package io.github.dbmdz.metadata.server.backend.impl.jdbi.identifiable.entity.geo.location;

import de.digitalcollections.cudami.model.config.CudamiConfig;
import de.digitalcollections.model.identifiable.entity.geo.location.GeoLocation;
import io.github.dbmdz.metadata.server.backend.api.repository.identifiable.IdentifierRepository;
import io.github.dbmdz.metadata.server.backend.api.repository.identifiable.alias.UrlAliasRepository;
import io.github.dbmdz.metadata.server.backend.api.repository.identifiable.entity.geo.location.GeoLocationRepository;
import io.github.dbmdz.metadata.server.backend.impl.jdbi.identifiable.entity.EntityRepositoryImpl;
import org.jdbi.v3.core.Jdbi;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository("geoLocationRepository")
public class GeoLocationRepositoryImpl<G extends GeoLocation> extends EntityRepositoryImpl<G>
    implements GeoLocationRepository<G> {

  private static final Logger LOGGER = LoggerFactory.getLogger(GeoLocationRepositoryImpl.class);

  public static final String MAPPING_PREFIX = "gl";
  public static final String TABLE_ALIAS = "g";
  public static final String TABLE_NAME = "geolocations";

  @Autowired
  public GeoLocationRepositoryImpl(
      Jdbi dbi,
      CudamiConfig cudamiConfig,
      IdentifierRepository identifierRepository,
      UrlAliasRepository urlAliasRepository) {
    this(
        dbi,
        TABLE_NAME,
        TABLE_ALIAS,
        MAPPING_PREFIX,
        GeoLocation.class,
        cudamiConfig.getOffsetForAlternativePaging(),
        identifierRepository,
        urlAliasRepository);
  }

  public GeoLocationRepositoryImpl(
      Jdbi dbi,
      String tableName,
      String tableAlias,
      String mappingPrefix,
      Class<? extends GeoLocation> geoLocationImplClass,
      int offsetForAlternativePaging,
      IdentifierRepository identifierRepository,
      UrlAliasRepository urlAliasRepository) {
    super(
        dbi,
        tableName,
        tableAlias,
        mappingPrefix,
        geoLocationImplClass,
        offsetForAlternativePaging,
        identifierRepository,
        urlAliasRepository);
  }

  @Override
  public String getColumnName(String modelProperty) {
    if (modelProperty == null) {
      return null;
    }
    switch (modelProperty) {
      case "coordinateLocation":
        return tableAlias + ".coordinate_location";
      case "geoLocationType":
        return tableAlias + ".geolocation_type";
      default:
        return super.getColumnName(modelProperty);
    }
  }

  @Override
  protected String getSqlInsertFields() {
    return super.getSqlInsertFields() + ", coordinate_location, geolocation_type";
  }

  /* Do not change order! Must match order in getSqlInsertFields!!! */
  @Override
  protected String getSqlInsertValues() {
    return super.getSqlInsertValues() + ", :coordinateLocation::JSONB, :geoLocationType";
  }

  @Override
  public String getSqlSelectAllFields(String tableAlias, String mappingPrefix) {
    return super.getSqlSelectAllFields(tableAlias, mappingPrefix)
        + ", "
        + tableAlias
        + ".coordinate_location "
        + mappingPrefix
        + "_coordinateLocation";
  }

  @Override
  public String getSqlSelectReducedFields(String tableAlias, String mappingPrefix) {
    return super.getSqlSelectReducedFields(tableAlias, mappingPrefix)
        + ", "
        + tableAlias
        + ".geolocation_type "
        + mappingPrefix
        + "_geoLocationType";
  }

  @Override
  protected String getSqlUpdateFieldValues() {
    return super.getSqlUpdateFieldValues() + ", coordinate_location=:coordinateLocation::JSONB";
  }
}
