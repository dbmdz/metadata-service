package io.github.dbmdz.metadata.server.business.api.service.semantic;

import de.digitalcollections.model.semantic.Tag;
import io.github.dbmdz.metadata.server.business.api.service.UniqueObjectService;
import io.github.dbmdz.metadata.server.business.api.service.exceptions.ServiceException;

/** Service for Tag */
public interface TagService extends UniqueObjectService<Tag> {

  Tag getByValue(String value) throws ServiceException;
}
