package io.github.dbmdz.metadata.server.business.api.service.identifiable;

import de.digitalcollections.model.identifiable.Identifiable;
import de.digitalcollections.model.identifiable.Identifier;
import de.digitalcollections.model.validation.ValidationException;
import io.github.dbmdz.metadata.server.business.api.service.UniqueObjectService;
import io.github.dbmdz.metadata.server.business.api.service.exceptions.ServiceException;
import java.util.List;
import java.util.Set;

public interface IdentifierService extends UniqueObjectService<Identifier> {
  int deleteByIdentifiable(Identifiable identifiable) throws ServiceException;

  List<Identifier> findByIdentifiable(Identifiable identifiable) throws ServiceException;

  Set<Identifier> saveForIdentifiable(Identifiable identifiable, Set<Identifier> identifiers)
      throws ServiceException, ValidationException;

  void validate(Set<Identifier> identifiers) throws ServiceException, ValidationException;
}
