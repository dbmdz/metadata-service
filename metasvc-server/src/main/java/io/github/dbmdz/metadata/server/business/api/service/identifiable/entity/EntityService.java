package io.github.dbmdz.metadata.server.business.api.service.identifiable.entity;

import de.digitalcollections.model.identifiable.entity.Entity;
import io.github.dbmdz.metadata.server.business.api.service.exceptions.ServiceException;
import io.github.dbmdz.metadata.server.business.api.service.identifiable.IdentifiableService;

/**
 * @param <E> entity instance
 */
public interface EntityService<E extends Entity> extends IdentifiableService<E> {

  E getByRefId(long refId) throws ServiceException;
}
