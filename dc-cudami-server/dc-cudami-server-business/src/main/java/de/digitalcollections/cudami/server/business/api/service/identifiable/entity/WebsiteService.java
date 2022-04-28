package de.digitalcollections.cudami.server.business.api.service.identifiable.entity;

import de.digitalcollections.model.identifiable.entity.Website;
import de.digitalcollections.model.identifiable.web.Webpage;
import de.digitalcollections.model.paging.SearchPageRequest;
import de.digitalcollections.model.paging.SearchPageResponse;
import java.util.List;
import java.util.UUID;

/** Service for Website. */
public interface WebsiteService extends EntityService<Website> {

  SearchPageResponse<Webpage> findRootWebpages(UUID uuid, SearchPageRequest searchPageRequest);

  default List<Webpage> getRootWebpages(Website website) {
    if (website == null) {
      return null;
    }
    return getRootWebpages(website.getUuid());
  }

  List<Webpage> getRootWebpages(UUID uuid);

  default boolean updateRootWebpagesOrder(Website website, List<Webpage> rootPages) {
    if (website == null || rootPages == null) {
      return false;
    }
    return updateRootWebpagesOrder(website.getUuid(), rootPages);
  }

  boolean updateRootWebpagesOrder(UUID websiteUuid, List<Webpage> rootPages);
}
