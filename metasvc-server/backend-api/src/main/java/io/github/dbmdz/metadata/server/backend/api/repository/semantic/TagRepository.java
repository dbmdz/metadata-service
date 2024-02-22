package io.github.dbmdz.metadata.server.backend.api.repository.semantic;

import de.digitalcollections.model.semantic.Tag;
import io.github.dbmdz.metadata.server.backend.api.repository.UniqueObjectRepository;
import io.github.dbmdz.metadata.server.backend.api.repository.exceptions.RepositoryException;

public interface TagRepository extends UniqueObjectRepository<Tag> {

  Tag getByValue(String value) throws RepositoryException;
}
