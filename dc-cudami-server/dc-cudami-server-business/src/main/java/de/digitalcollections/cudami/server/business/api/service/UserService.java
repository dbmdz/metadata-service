package de.digitalcollections.cudami.server.business.api.service;

import de.digitalcollections.core.model.api.paging.PageRequest;
import de.digitalcollections.core.model.api.paging.PageResponse;
import de.digitalcollections.cudami.model.api.security.User;
import java.util.List;
import java.util.UUID;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.validation.Errors;

/**
 * Service for User.
 *
 * @param <T> domain object
 */
public interface UserService<T extends User> {

  T activate(UUID uuid);

  T createAdminUser();

  T deactivate(UUID uuid);

  boolean doesActiveAdminUserExist();

  List<T> findActiveAdminUsers();

  T get(UUID uuid);

  PageResponse<T> find(PageRequest pageRequest);

  T loadUserByUsername(String string) throws UsernameNotFoundException;

  T save(T user, Errors results);

  T update(T user, Errors results);
}
