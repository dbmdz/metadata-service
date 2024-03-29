package io.github.dbmdz.metadata.server.backend.impl.jdbi.identifiable.versioning;

import de.digitalcollections.cudami.model.config.CudamiConfig;
import de.digitalcollections.model.identifiable.versioning.Version;
import io.github.dbmdz.metadata.server.backend.api.repository.exceptions.RepositoryException;
import io.github.dbmdz.metadata.server.backend.api.repository.identifiable.versioning.VersionRepository;
import io.github.dbmdz.metadata.server.backend.impl.jdbi.JdbiRepositoryImpl;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import org.jdbi.v3.core.Jdbi;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
public class VersionRepositoryImpl extends JdbiRepositoryImpl implements VersionRepository {

  private static final Logger LOGGER = LoggerFactory.getLogger(VersionRepositoryImpl.class);

  public static final String MAPPING_PREFIX = "ver";
  public static final String TABLE_ALIAS = "v";
  public static final String TABLE_NAME = "versions";

  @Autowired
  public VersionRepositoryImpl(Jdbi dbi, CudamiConfig cudamiConfig) {
    super(
        dbi, TABLE_NAME, TABLE_ALIAS, MAPPING_PREFIX, cudamiConfig.getOffsetForAlternativePaging());
  }

  @Override
  protected List<String> getAllowedOrderByFields() {
    throw new UnsupportedOperationException("Not supported yet.");
  }

  @Override
  public Version getByInstanceversionKey(String instVersionKey) {
    final String sql =
        "SELECT * FROM " + tableName + " WHERE instance_version_key = :instance_version_key";

    Version version =
        dbi.withHandle(
            h ->
                h.createQuery(sql)
                    .bind("instance_version_key", instVersionKey)
                    .mapToBean(Version.class)
                    .findFirst()
                    .orElse(null));
    return version;
  }

  @Override
  public Version getByUuid(UUID uuid) {
    final String sql = "SELECT * FROM " + tableName + " WHERE uuid = :uuid";

    Version version =
        dbi.withHandle(
            h ->
                h.createQuery(sql)
                    .bind("uuid", uuid)
                    .mapToBean(Version.class)
                    .findOne()
                    .orElse(null));
    return version;
  }

  @Override
  public String getColumnName(String modelProperty) {
    throw new UnsupportedOperationException("Not supported yet.");
  }

  @Override
  protected String getUniqueField() {
    return "uuid";
  }

  @Override
  public void save(Version version) throws RepositoryException {
    version.setUuid(UUID.randomUUID());
    version.setCreated(new Date());

    final String sql =
        "INSERT INTO "
            + tableName
            + "(uuid, version_value, type_key, instance_key, instance_version_key, description, created, status) "
            + "VALUES (:uuid, :versionValue, :typeKey, :instanceKey, :instanceVersionKey, :description, :created, :status)";

    dbi.useHandle(h -> h.createUpdate(sql).bindBean(version).execute());
  }

  @Override
  protected boolean supportsCaseSensitivityForProperty(String modelProperty) {
    return false;
  }

  @Override
  public void update(Version version) throws RepositoryException {
    // digitalObject.setLastModified(LocalDateTime.now());
    final String sql = "UPDATE " + tableName + " SET status=:status WHERE uuid=:uuid";

    dbi.useHandle(h -> h.createUpdate(sql).bindBean(version).execute());
  }
}
