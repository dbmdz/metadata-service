package io.github.dbmdz.metadata.server.backend.impl.jdbi.identifiable;

import de.digitalcollections.cudami.model.config.CudamiConfig;
import de.digitalcollections.model.identifiable.IdentifierType;
import io.github.dbmdz.metadata.server.backend.api.repository.exceptions.RepositoryException;
import io.github.dbmdz.metadata.server.backend.api.repository.identifiable.IdentifierTypeRepository;
import io.github.dbmdz.metadata.server.backend.impl.jdbi.UniqueObjectRepositoryImpl;
import java.util.Arrays;
import java.util.List;
import org.jdbi.v3.core.Jdbi;
import org.springframework.stereotype.Repository;

@Repository
public class IdentifierTypeRepositoryImpl extends UniqueObjectRepositoryImpl<IdentifierType>
    implements IdentifierTypeRepository {

  public static final String MAPPING_PREFIX = "idt";
  public static final String TABLE_ALIAS = "idt";
  public static final String TABLE_NAME = "identifiertypes";

  public IdentifierTypeRepositoryImpl(Jdbi dbi, CudamiConfig cudamiConfig) {
    super(
        dbi,
        TABLE_NAME,
        TABLE_ALIAS,
        MAPPING_PREFIX,
        IdentifierType.class,
        cudamiConfig.getOffsetForAlternativePaging());
  }

  @Override
  public IdentifierType create() throws RepositoryException {
    return new IdentifierType();
  }

  @Override
  protected List<String> getAllowedOrderByFields() {
    List<String> allowedOrderByFields = super.getAllowedOrderByFields();
    allowedOrderByFields.addAll(Arrays.asList("label", "namespace", "pattern"));
    return allowedOrderByFields;
  }

  @Override
  public IdentifierType getByNamespace(String namespace) throws RepositoryException {
    final String sql = "SELECT * FROM " + tableName + " WHERE namespace = :namespace";

    IdentifierType identifierType =
        dbi.withHandle(
            h ->
                h.createQuery(sql)
                    .bind("namespace", namespace)
                    .mapToBean(IdentifierType.class)
                    .findOne()
                    .orElse(null));

    return identifierType;
  }

  @Override
  public String getColumnName(String modelProperty) {
    if (modelProperty == null) {
      return null;
    }
    switch (modelProperty) {
      case "label":
        return tableAlias + ".label";
      case "namespace":
        return tableAlias + ".namespace";
      case "pattern":
        return tableAlias + ".pattern";
      default:
        return super.getColumnName(modelProperty);
    }
  }

  @Override
  public List<IdentifierType> getRandom(int count) throws RepositoryException {
    throw new UnsupportedOperationException(); // TODO: not yet implemented
  }

  @Override
  protected String getSqlInsertFields() {
    return super.getSqlInsertFields() + ", label, namespace, pattern";
  }

  @Override
  protected String getSqlInsertValues() {
    return super.getSqlInsertValues() + ", :label, :namespace, :pattern";
  }

  @Override
  public String getSqlSelectAllFields(String tableAlias, String mappingPrefix) {
    return getSqlSelectReducedFields(tableAlias, mappingPrefix);
  }

  @Override
  public String getSqlSelectReducedFields(String tableAlias, String mappingPrefix) {
    return super.getSqlSelectReducedFields(tableAlias, mappingPrefix)
        + ", "
        + tableAlias
        + ".label "
        + mappingPrefix
        + "_label, "
        + tableAlias
        + ".namespace "
        + mappingPrefix
        + "_namespace, "
        + tableAlias
        + ".pattern "
        + mappingPrefix
        + "_pattern";
  }

  @Override
  protected String getSqlUpdateFieldValues() {
    // do not update/left out from statement (not changed since insert):
    // uuid, created
    return super.getSqlUpdateFieldValues()
        + ", label=:label, namespace=:namespace, pattern=:pattern";
  }

  @Override
  protected boolean supportsCaseSensitivityForProperty(String modelProperty) {
    switch (modelProperty) {
      case "label":
        return true;
      default:
        return false;
    }
  }
}
