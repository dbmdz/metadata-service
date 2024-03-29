package io.github.dbmdz.metadata.server.business.api.service.identifiable.entity.work;

import de.digitalcollections.model.identifiable.entity.item.Item;
import de.digitalcollections.model.identifiable.entity.manifestation.Manifestation;
import de.digitalcollections.model.list.paging.PageRequest;
import de.digitalcollections.model.list.paging.PageResponse;
import io.github.dbmdz.metadata.server.business.api.service.exceptions.ServiceException;
import io.github.dbmdz.metadata.server.business.api.service.identifiable.entity.EntityService;
import java.util.List;
import java.util.Locale;

public interface ItemService extends EntityService<Item> {

  PageResponse<Item> findItemsByManifestation(Manifestation manifestation, PageRequest pageRequest)
      throws ServiceException;

  List<Locale> getLanguagesOfDigitalObjects(Item item) throws ServiceException;

  List<Locale> getLanguagesOfItemsForManifestation(Manifestation manifestation)
      throws ServiceException;

  /**
   * Clears and saves the partOfItem attribute of an item
   *
   * @param item
   * @param parentItem
   * @return boolean value for success
   * @throws ServiceException
   */
  boolean clearPartOfItem(Item item, Item parentItem) throws ServiceException;

  boolean removeParentItemChildren(Item parentItem) throws ServiceException;
}
