package io.github.dbmdz.metadata.server.business.api.service.security;

import de.digitalcollections.model.security.User;
import de.digitalcollections.model.validation.ValidationException;
import io.github.dbmdz.metadata.server.business.api.service.UniqueObjectService;
import io.github.dbmdz.metadata.server.business.api.service.exceptions.ServiceException;
import java.util.List;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.validation.Errors;

/** Service for User. */
public interface UserService extends UniqueObjectService<User> {

  User activate(User user) throws ServiceException, ValidationException;

  User createAdminUser() throws ServiceException;

  User deactivate(User user) throws ServiceException, ValidationException;

  boolean doesActiveAdminUserExist() throws ServiceException;

  List<User> getActiveAdminUsers() throws ServiceException;

  User getByUsername(String username) throws ServiceException, UsernameNotFoundException;

  User save(User user, Errors results) throws ServiceException, ValidationException;

  User update(User user, Errors results) throws ServiceException, ValidationException;
}
