package de.digitalcollections.cudami.client.business.api.service;

import de.digitalcollections.core.model.api.paging.PageRequest;
import de.digitalcollections.core.model.api.paging.PageResponse;
import de.digitalcollections.cudami.model.api.security.User;
import java.io.Serializable;
import java.util.List;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.validation.Errors;

/**
 * Service for User.
 *
 * @param <T> domain object
 * @param <ID> unique id
 */
public interface UserService<T extends User, ID extends Serializable> extends UserDetailsService {

  T activate(ID id);

  T create();

  T create(T user, String password1, String password2, Errors results);

  T createAdminUser();

  T deactivate(ID id);

  PageResponse<T> find(PageRequest page);

  T get(ID id);

  List<T> getAll();

  T update(T user, String password1, String password2, Errors results);

  boolean doesActiveAdminUserExist();
}
