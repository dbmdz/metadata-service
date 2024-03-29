package io.github.dbmdz.metadata.server.backend.impl.jdbi.identifiable.entity.agent;

import de.digitalcollections.cudami.model.config.CudamiConfig;
import de.digitalcollections.model.identifiable.entity.Entity;
import de.digitalcollections.model.identifiable.entity.agent.Agent;
import de.digitalcollections.model.identifiable.entity.digitalobject.DigitalObject;
import de.digitalcollections.model.identifiable.entity.work.Work;
import de.digitalcollections.model.list.paging.PageRequest;
import de.digitalcollections.model.list.paging.PageResponse;
import io.github.dbmdz.metadata.server.backend.api.repository.exceptions.RepositoryException;
import io.github.dbmdz.metadata.server.backend.api.repository.identifiable.IdentifierRepository;
import io.github.dbmdz.metadata.server.backend.api.repository.identifiable.alias.UrlAliasRepository;
import io.github.dbmdz.metadata.server.backend.api.repository.identifiable.entity.agent.AgentRepository;
import io.github.dbmdz.metadata.server.backend.impl.jdbi.identifiable.entity.EntityRepositoryImpl;
import java.util.Set;
import java.util.UUID;
import org.jdbi.v3.core.Jdbi;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

/** Repository for Agent persistence handling. No own table, using entities table. */
@Repository("agentRepository")
public class AgentRepositoryImpl<A extends Agent> extends EntityRepositoryImpl<A>
    implements AgentRepository<A> {

  private static final Logger LOGGER = LoggerFactory.getLogger(AgentRepositoryImpl.class);

  public static final String MAPPING_PREFIX = "ag";
  public static final String TABLE_ALIAS = "ag";
  public static final String TABLE_NAME = "agents";

  @Autowired
  public AgentRepositoryImpl(
      Jdbi dbi,
      CudamiConfig cudamiConfig,
      IdentifierRepository identifierRepository,
      UrlAliasRepository urlAliasRepository) {
    this(
        dbi,
        TABLE_NAME,
        TABLE_ALIAS,
        MAPPING_PREFIX,
        Agent.class,
        cudamiConfig.getOffsetForAlternativePaging(),
        identifierRepository,
        urlAliasRepository);
  }

  public AgentRepositoryImpl(
      Jdbi dbi,
      String tableName,
      String tableAlias,
      String mappingPrefix,
      Class<? extends Entity> entityImplClass,
      int offsetForAlternativePaging,
      IdentifierRepository identifierRepository,
      UrlAliasRepository urlAliasRepository) {
    super(
        dbi,
        tableName,
        tableAlias,
        mappingPrefix,
        entityImplClass,
        offsetForAlternativePaging,
        identifierRepository,
        urlAliasRepository);
  }

  @Override
  public PageResponse<DigitalObject> findDigitalObjects(UUID agentUuid, PageRequest pageRequest) {
    throw new UnsupportedOperationException(); // TODO: not yet implemented
  }

  @Override
  public PageResponse<Work> findWorks(UUID agentUuid, PageRequest pageRequest) {
    throw new UnsupportedOperationException(); // TODO: not yet implemented
  }

  @Override
  public Set<DigitalObject> getDigitalObjects(UUID uuidAgent) throws RepositoryException {
    throw new UnsupportedOperationException(); // TODO: not yet implemented
  }

  @Override
  public Set<Work> getWorks(UUID uuidAgent) {
    throw new UnsupportedOperationException(); // TODO: not yet implemented
  }
}
