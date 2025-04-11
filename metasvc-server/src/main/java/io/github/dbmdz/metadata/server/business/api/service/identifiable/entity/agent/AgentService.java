package io.github.dbmdz.metadata.server.business.api.service.identifiable.entity.agent;

import de.digitalcollections.model.identifiable.entity.agent.Agent;
import de.digitalcollections.model.identifiable.entity.digitalobject.DigitalObject;
import de.digitalcollections.model.identifiable.entity.work.Work;
import io.github.dbmdz.metadata.server.business.api.service.exceptions.ServiceException;
import io.github.dbmdz.metadata.server.business.api.service.identifiable.entity.EntityService;
import java.util.List;
import java.util.Set;

public interface AgentService<A extends Agent> extends EntityService<A> {

  List<A> getCreatorsForWork(Work work) throws ServiceException;

  Set<DigitalObject> getDigitalObjects(A agent) throws ServiceException;

  Set<Work> getWorks(A agent) throws ServiceException;
}
