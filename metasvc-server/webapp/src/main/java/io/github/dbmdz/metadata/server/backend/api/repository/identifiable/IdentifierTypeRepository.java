package io.github.dbmdz.metadata.server.backend.api.repository.identifiable;

import de.digitalcollections.model.identifiable.IdentifierType;
import io.github.dbmdz.metadata.server.backend.api.repository.UniqueObjectRepository;
import io.github.dbmdz.metadata.server.backend.api.repository.exceptions.RepositoryException;

public interface IdentifierTypeRepository extends UniqueObjectRepository<IdentifierType> {

  IdentifierType getByNamespace(String namespace) throws RepositoryException;
}
