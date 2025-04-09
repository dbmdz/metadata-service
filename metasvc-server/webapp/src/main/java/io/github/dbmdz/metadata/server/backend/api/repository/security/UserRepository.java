package io.github.dbmdz.metadata.server.backend.api.repository.security;

import de.digitalcollections.model.security.User;
import io.github.dbmdz.metadata.server.backend.api.repository.UniqueObjectRepository;
import io.github.dbmdz.metadata.server.backend.api.repository.exceptions.RepositoryException;
import java.util.List;

/** Repository for User persistence handling. */
public interface UserRepository extends UniqueObjectRepository<User> {

  List<User> getActiveAdminUsers() throws RepositoryException;

  User getByEmail(String email) throws RepositoryException;
}
