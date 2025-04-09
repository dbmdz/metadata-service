package io.github.dbmdz.metadata.server.backend.impl.jdbi.identifiable.entity.work;

import de.digitalcollections.cudami.model.config.CudamiConfig;
import de.digitalcollections.model.identifiable.entity.Entity;
import de.digitalcollections.model.identifiable.entity.agent.Agent;
import de.digitalcollections.model.identifiable.entity.manifestation.Manifestation;
import de.digitalcollections.model.list.ListRequest;
import de.digitalcollections.model.list.ListResponse;
import io.github.dbmdz.metadata.server.backend.api.repository.exceptions.RepositoryException;
import io.github.dbmdz.metadata.server.backend.api.repository.identifiable.IdentifierRepository;
import io.github.dbmdz.metadata.server.backend.api.repository.identifiable.alias.UrlAliasRepository;
import io.github.dbmdz.metadata.server.backend.api.repository.identifiable.entity.work.DigipressManifestationRepository;
import io.github.dbmdz.metadata.server.backend.impl.jdbi.identifiable.entity.EntityRepositoryImpl;
import io.github.dbmdz.metadata.server.backend.impl.jdbi.identifiable.entity.agent.AgentRepositoryImpl;
import io.github.dbmdz.metadata.server.backend.impl.jdbi.identifiable.entity.geo.location.HumanSettlementRepositoryImpl;
import io.github.dbmdz.metadata.server.backend.impl.jdbi.type.LocalDateRangeMapper;
import io.github.dbmdz.metadata.server.backend.impl.jdbi.type.MainSubTypeMapper.ExpressionTypeMapper;
import io.github.dbmdz.metadata.server.backend.impl.jdbi.type.TitleMapper;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.jdbi.v3.core.Jdbi;
import org.jdbi.v3.core.JdbiException;
import org.jdbi.v3.core.statement.StatementException;
import org.springframework.stereotype.Repository;

@Repository
public class DigipressManifestationRepositoryImpl extends ManifestationRepositoryImpl
    implements DigipressManifestationRepository {

  public DigipressManifestationRepositoryImpl(
      Jdbi jdbi,
      CudamiConfig cudamiConfig,
      IdentifierRepository identifierRepository,
      UrlAliasRepository urlAliasRepository,
      ExpressionTypeMapper expressionTypeMapper,
      LocalDateRangeMapper dateRangeMapper,
      TitleMapper titleMapper,
      EntityRepositoryImpl<Entity> entityRepository,
      AgentRepositoryImpl<Agent> agentRepository,
      HumanSettlementRepositoryImpl humanSettlementRepository) {
    super(
        jdbi,
        cudamiConfig,
        identifierRepository,
        urlAliasRepository,
        expressionTypeMapper,
        dateRangeMapper,
        titleMapper,
        entityRepository,
        agentRepository,
        humanSettlementRepository);
  }

  @Override
  public void refreshTable() {
    dbi.useHandle(h -> h.execute("refresh materialized view digipress;"));
  }

  @Override
  public String getColumnName(String modelProperty) {
    return switch (modelProperty) {
      case "lastModified" -> "mf_lastModified";
      case "identifiableObjectType" -> "mf_identifiableObjectType";
      case "nameLocalesOfOriginalScripts" -> "mf_nameLocalesOfOriginalScripts";
      case "identifiers.id" -> "id_id";
      case "identifiers.namespace" -> "id_namespace";
      case "expressionTypes",
              "manifestationType",
              "manufacturingType",
              "mediaTypes",
              "otherLanguages" ->
          "mf_%s".formatted(modelProperty);
      default -> super.getColumnName(modelProperty).replace('.', '_');
    };
  }

  @Override
  public ListResponse<Manifestation, ListRequest> getNewspapers(ListRequest listRequest)
      throws RepositoryException {
    StringBuilder sqlQuery = new StringBuilder("select * from digipress ");
    Map<String, Object> mappings = new HashMap<>();
    if (listRequest.hasFiltering()) addFiltering(listRequest.getFiltering(), sqlQuery, mappings);
    if (listRequest.hasSorting()) addOrderBy(listRequest.getSorting(), sqlQuery);

    try {
      List<Manifestation> results =
          dbi.withHandle(
              h ->
                  h.createQuery(sqlQuery.toString())
                      .bindMap(mappings)
                      .reduceRows(this::basicReduceRowsBiConsumer)
                      .collect(Collectors.toList()));
      return new ListResponse<>(results, listRequest);
    } catch (StatementException e) {
      String detailMessage = e.getCause() != null ? e.getCause().getMessage() : e.getMessage();
      throw new RepositoryException(
          String.format("The SQL statement is defective: %s", detailMessage), e);
    } catch (JdbiException e) {
      throw new RepositoryException(e);
    }
  }
}
