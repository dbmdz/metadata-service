package de.digitalcollections.cudami.client.identifiable.entity.work;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.digitalcollections.cudami.client.identifiable.entity.CudamiEntitiesClient;
import de.digitalcollections.model.exception.TechnicalException;
import de.digitalcollections.model.identifiable.entity.item.Item;
import de.digitalcollections.model.identifiable.entity.manifestation.Manifestation;
import de.digitalcollections.model.list.ListRequest;
import de.digitalcollections.model.list.ListResponse;
import de.digitalcollections.model.list.paging.PageRequest;
import de.digitalcollections.model.list.paging.PageResponse;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.net.http.HttpClient;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

public class CudamiManifestationsClient extends CudamiEntitiesClient<Manifestation> {

  @SuppressFBWarnings(value = "SS_SHOULD_BE_STATIC", justification = "non-static is fine, though")
  private final String digiPressBaseEndpoint = API_VERSION_PREFIX + "/digipress";

  public CudamiManifestationsClient(HttpClient http, String serverUrl, ObjectMapper mapper) {
    super(http, serverUrl, Manifestation.class, mapper, API_VERSION_PREFIX + "/manifestations");
  }

  public PageResponse<Manifestation> findChildren(UUID uuid, PageRequest pageRequest)
      throws TechnicalException {
    return doGetRequestForPagedObjectList(
        String.format(baseEndpoint + "/" + uuid + "/children"), pageRequest);
  }

  public PageResponse<Item> findItems(UUID uuid, PageRequest pageRequest)
      throws TechnicalException {
    return doGetRequestForPagedObjectList(
        String.format("%s/%s/items", baseEndpoint, uuid), pageRequest, Item.class);
  }

  public List<Locale> getLanguagesOfItems(UUID uuid) throws TechnicalException {
    return doGetRequestForObjectList(
        String.format("%s/%s/items/languages", baseEndpoint, uuid), Locale.class);
  }

  public ListResponse<Manifestation, ListRequest> findNewspapers(ListRequest listRequest)
      throws TechnicalException {
    return doGetRequestForObjectListResponse(digiPressBaseEndpoint, targetType, listRequest);
  }

  public void refreshNewspapers() throws TechnicalException {
    doPutRequestForObject(digiPressBaseEndpoint + "/refresh", null);
  }
}
