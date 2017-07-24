package de.digitalcollections.cudami.server.backend.impl.jdbi;

import de.digitalcollections.core.model.api.paging.Order;
import de.digitalcollections.core.model.api.paging.PageRequest;
import de.digitalcollections.core.model.api.paging.PageResponse;
import de.digitalcollections.core.model.api.paging.Sorting;
import de.digitalcollections.core.model.api.paging.enums.Direction;
import de.digitalcollections.core.model.impl.paging.PageResponseImpl;
import de.digitalcollections.cudami.model.api.security.enums.Role;
import de.digitalcollections.cudami.model.impl.security.UserImpl;
import de.digitalcollections.cudami.server.backend.api.repository.UserRepository;
import java.util.Iterator;
import java.util.List;
import org.jdbi.v3.core.Jdbi;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
public class UserRepositoryImpl implements UserRepository<UserImpl, Long> {

  @Autowired
  private Jdbi dbi;

  @Override
  public long count() {
    throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
  }

  @Override
  public UserImpl create() {
    return new UserImpl();
  }

  @Override
  public PageResponse<UserImpl> find(PageRequest pageRequest) {
    StringBuilder query = new StringBuilder("SELECT * FROM users ORDER BY :order_field");

    // Sorting
    Sorting sorting = pageRequest.getSorting();
    String sortField = null;
    if (sorting != null) {
      Iterator<Order> iterator = sorting.iterator();
      if (iterator.hasNext()) { // FIXME just supporting one field sorting until now
        Order order = iterator.next();
        sortField = order.getProperty();
        if (sortField != null) {
          Direction sortDirection = order.getDirection();
          if (sortDirection != null && sortDirection.isDescending()) {
            query.append(" DESC");
          }
        }
      }
    }
    if (sortField == null) {
      sortField = "id";
    }
    final String finalSortField = sortField;

    // TODO paging
    // TODO total
    List<UserImpl> content = dbi.withHandle(h -> h.createQuery(query.toString())
            .bind("order_field", finalSortField)
            .mapToBean(UserImpl.class)
            .list());

    // TODO PageRequest
    PageResponse pageResponse = new PageResponseImpl(content);
    return pageResponse;
  }

  @Override
  public List<UserImpl> findActiveAdminUsers() {
    return dbi.withHandle(h -> h.createQuery(
            "SELECT * FROM users WHERE '" + Role.ADMIN.name() + "' = any(roles)")
            .mapToBean(UserImpl.class)
            .list());
  }

  @Override
  public UserImpl findByEmail(String email) {
    List<UserImpl> users = dbi.withHandle(h -> h.createQuery(
            "SELECT * FROM users WHERE email = :email")
            .bind("email", email)
            .mapToBean(UserImpl.class)
            .list());
    if (users.isEmpty()) {
      return null;
    }
    return users.get(0);
  }

  @Override
  public UserImpl findOne(Long id) {
    List<UserImpl> users = dbi.withHandle(h -> h.createQuery(
            "SELECT * FROM users WHERE id = :id")
            .bind("id", id)
            .mapToBean(UserImpl.class)
            .list());
    if (users.isEmpty()) {
      return null;
    }
    return users.get(0);
  }

  @Override
  public UserImpl save(UserImpl user) {
//    UserImpl result = dbi.withHandle(h -> h.createQuery(
//            "INSERT INTO users(email, enabled, firstname, lastname, passwordHash, roles) VALUES (:email, :enabled, :firstname, :lastname, :passwordHash, :roles) RETURNING *")
//            .bindBean(user)
//            .bind("roles", user.getRoles().stream().map(Role::name).toArray(String[]::new))
//            .mapToBean(UserImpl.class)
//            .findOnly());
//    return (S) result;

    UserImpl result = dbi.withHandle(h -> h
            .registerArrayType(Role.class, "varchar")
            .createQuery("INSERT INTO users(email, enabled, firstname, lastname, passwordHash, roles) VALUES (:email, :enabled, :firstname, :lastname, :passwordHash, :roles) RETURNING *")
            .bindBean(user)
            .mapToBean(UserImpl.class)
            .findOnly());
    return result;
  }

  @Override
  public UserImpl update(UserImpl user) {
    UserImpl result = dbi.withHandle(h -> h
            .registerArrayType(Role.class, "varchar")
            .createQuery("UPDATE users SET email=:email, enabled=:enabled, firstname=:firstname, lastname=:lastname, passwordHash=:passwordHash, roles=:roles WHERE id=:id RETURNING *")
            .bindBean(user)
            .mapToBean(UserImpl.class)
            .findOnly());
    return result;
  }

}
