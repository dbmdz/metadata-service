package io.github.dbmdz.metadata.server.backend.impl.jdbi.view;

import de.digitalcollections.cudami.model.config.CudamiConfig;
import de.digitalcollections.model.view.RenderingTemplate;
import io.github.dbmdz.metadata.server.backend.api.repository.exceptions.RepositoryException;
import io.github.dbmdz.metadata.server.backend.api.repository.view.RenderingTemplateRepository;
import io.github.dbmdz.metadata.server.backend.impl.jdbi.UniqueObjectRepositoryImpl;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import org.jdbi.v3.core.Jdbi;
import org.springframework.stereotype.Repository;

@Repository
public class RenderingTemplateRepositoryImpl extends UniqueObjectRepositoryImpl<RenderingTemplate>
    implements RenderingTemplateRepository {

  public static final String MAPPING_PREFIX = "rt";
  public static final String TABLE_ALIAS = "rt";
  public static final String TABLE_NAME = "rendering_templates";

  public RenderingTemplateRepositoryImpl(Jdbi dbi, CudamiConfig cudamiConfig) {
    super(
        dbi,
        TABLE_NAME,
        TABLE_ALIAS,
        MAPPING_PREFIX,
        RenderingTemplate.class,
        cudamiConfig.getOffsetForAlternativePaging());
  }

  @Override
  public RenderingTemplate create() throws RepositoryException {
    return new RenderingTemplate();
  }

  @Override
  protected List<String> getAllowedOrderByFields() {
    List<String> allowedOrderByFields = super.getAllowedOrderByFields();
    allowedOrderByFields.addAll(Arrays.asList("label", "name"));
    return allowedOrderByFields;
  }

  @Override
  public String getColumnName(String modelProperty) {
    if (modelProperty == null) {
      return null;
    }
    switch (modelProperty) {
      case "label":
        return tableAlias + ".label";
      case "name":
        return tableAlias + ".name";
      default:
        return super.getColumnName(modelProperty);
    }
  }

  @Override
  public List<Locale> getLanguages() {
    String query =
        "SELECT jsonb_object_keys("
            + tableAlias
            + ".label) as languages FROM "
            + tableName
            + " AS "
            + tableAlias
            + " UNION SELECT jsonb_object_keys("
            + tableAlias
            + ".description) FROM "
            + tableName
            + " AS "
            + tableAlias;
    return dbi.withHandle(h -> h.createQuery(query).mapTo(Locale.class).list());
  }

  @Override
  public List<RenderingTemplate> getRandom(int count) throws RepositoryException {
    throw new UnsupportedOperationException(); // TODO: not yet implemented
  }

  @Override
  protected String getSqlInsertFields() {
    return super.getSqlInsertFields() + ", description, label, name";
  }

  @Override
  protected String getSqlInsertValues() {
    return super.getSqlInsertValues() + ", :description::JSONB, :label::JSONB, :name";
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
        + ".description "
        + mappingPrefix
        + "_description, "
        + tableAlias
        + ".label "
        + mappingPrefix
        + "_label, "
        + tableAlias
        + ".name "
        + mappingPrefix
        + "_name";
  }

  @Override
  protected String getSqlUpdateFieldValues() {
    return super.getSqlUpdateFieldValues()
        + ", description=:description::JSONB, label=:label::JSONB, name=:name";
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
