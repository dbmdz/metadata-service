package io.github.dbmdz.metadata.server.business.api.service.identifiable.entity.work;

import de.digitalcollections.model.identifiable.entity.agent.Person;
import de.digitalcollections.model.identifiable.entity.item.Item;
import de.digitalcollections.model.identifiable.entity.work.Work;
import de.digitalcollections.model.list.paging.PageRequest;
import de.digitalcollections.model.list.paging.PageResponse;
import io.github.dbmdz.metadata.server.business.api.service.exceptions.ServiceException;
import io.github.dbmdz.metadata.server.business.api.service.identifiable.entity.EntityService;
import java.util.Set;

public interface WorkService extends EntityService<Work> {

  PageResponse<Work> findEmbeddedWorks(Work work, PageRequest pageRequest) throws ServiceException;

  Work getByItem(Item item) throws ServiceException;

  Set<Work> getByPerson(Person person) throws ServiceException;
}
