package io.github.dbmdz.metadata.server.backend.api.repository.identifiable.entity.work;

import de.digitalcollections.model.identifiable.entity.item.Item;
import de.digitalcollections.model.identifiable.entity.manifestation.Manifestation;
import de.digitalcollections.model.list.paging.PageRequest;
import de.digitalcollections.model.list.paging.PageResponse;
import io.github.dbmdz.metadata.server.backend.api.repository.exceptions.RepositoryException;
import io.github.dbmdz.metadata.server.backend.api.repository.identifiable.entity.EntityRepository;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

/** Repository for Item persistence handling. */
public interface ItemRepository extends EntityRepository<Item> {

  default PageResponse<Item> findItemsByManifestation(
      Manifestation manifestation, PageRequest pageRequest) throws RepositoryException {
    if (manifestation == null) {
      throw new IllegalArgumentException("find failed: given object must not be null");
    }
    return findItemsByManifestation(manifestation.getUuid(), pageRequest);
  }

  PageResponse<Item> findItemsByManifestation(UUID manifestationUuid, PageRequest pageRequest)
      throws RepositoryException;

  default List<Locale> getLanguagesOfDigitalObjects(Item item) throws RepositoryException {
    if (item == null) {
      throw new IllegalArgumentException("get failed: given object must not be null");
    }
    return getLanguagesOfDigitalObjects(item.getUuid());
  }

  List<Locale> getLanguagesOfDigitalObjects(UUID itemUuid);

  default List<Locale> getLanguagesOfItemsForManifestation(Manifestation manifestation)
      throws RepositoryException {
    if (manifestation == null) {
      throw new IllegalArgumentException("get failed: given object must not be null");
    }
    return getLanguagesOfItemsForManifestation(manifestation.getUuid());
  }

  List<Locale> getLanguagesOfItemsForManifestation(UUID manifestationUuid);
}
