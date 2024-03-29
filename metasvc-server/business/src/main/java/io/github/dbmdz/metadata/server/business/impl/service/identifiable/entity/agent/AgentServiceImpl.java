package io.github.dbmdz.metadata.server.business.impl.service.identifiable.entity.agent;

import de.digitalcollections.cudami.model.config.CudamiConfig;
import de.digitalcollections.model.identifiable.entity.agent.Agent;
import de.digitalcollections.model.identifiable.entity.digitalobject.DigitalObject;
import de.digitalcollections.model.identifiable.entity.work.Work;
import io.github.dbmdz.metadata.server.backend.api.repository.exceptions.RepositoryException;
import io.github.dbmdz.metadata.server.backend.api.repository.identifiable.entity.agent.AgentRepository;
import io.github.dbmdz.metadata.server.business.api.service.LocaleService;
import io.github.dbmdz.metadata.server.business.api.service.exceptions.ServiceException;
import io.github.dbmdz.metadata.server.business.api.service.identifiable.IdentifierService;
import io.github.dbmdz.metadata.server.business.api.service.identifiable.alias.UrlAliasService;
import io.github.dbmdz.metadata.server.business.api.service.identifiable.entity.agent.AgentService;
import io.github.dbmdz.metadata.server.business.impl.service.identifiable.entity.EntityServiceImpl;
import io.github.dbmdz.metadata.server.config.HookProperties;
import java.util.List;
import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

/** Service for Agent handling. */
// @Transactional should not be set in derived class to prevent overriding, check base class instead
@Service("agentService")
public class AgentServiceImpl<A extends Agent> extends EntityServiceImpl<A>
    implements AgentService<A> {

  private static final Logger LOGGER = LoggerFactory.getLogger(AgentServiceImpl.class);

  public AgentServiceImpl(
      @Qualifier("agentRepository") AgentRepository<A> repository,
      IdentifierService identifierService,
      UrlAliasService urlAliasService,
      HookProperties hookProperties,
      LocaleService localeService,
      CudamiConfig cudamiConfig) {
    super(
        repository,
        identifierService,
        urlAliasService,
        hookProperties,
        localeService,
        cudamiConfig);
  }

  @Override
  public List<A> getCreatorsForWork(Work work) {
    throw new UnsupportedOperationException(); // TODO: not yet implemented
  }

  @Override
  public Set<DigitalObject> getDigitalObjects(A agent) throws ServiceException {
    try {
      return ((AgentRepository<A>) repository).getDigitalObjects(agent);
    } catch (RepositoryException e) {
      throw new ServiceException("Backend failure", e);
    }
  }

  @Override
  public Set<Work> getWorks(A agent) throws ServiceException {
    try {
      return ((AgentRepository<A>) repository).getWorks(agent);
    } catch (RepositoryException e) {
      throw new ServiceException("Backend failure", e);
    }
  }
}
