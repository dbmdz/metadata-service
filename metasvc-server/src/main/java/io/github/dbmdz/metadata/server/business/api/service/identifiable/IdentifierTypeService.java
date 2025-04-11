package io.github.dbmdz.metadata.server.business.api.service.identifiable;

import de.digitalcollections.model.identifiable.IdentifierType;
import io.github.dbmdz.metadata.server.business.api.service.UniqueObjectService;
import io.github.dbmdz.metadata.server.business.api.service.exceptions.ServiceException;
import java.util.Map;

public interface IdentifierTypeService extends UniqueObjectService<IdentifierType> {

  IdentifierType getByNamespace(String namespace) throws ServiceException;

  // FIXME: move as internal implementation to IdentifierTypeRepositoryImpl or IdentifierServiceImpl
  // (for validation only)
  // get all identifierTypes using count and paging (maybe introduce a getAll() in repo?)
  Map<String, String> getIdentifierTypeCache() throws ServiceException;

  Map<String, String> updateIdentifierTypeCache() throws ServiceException;
}
