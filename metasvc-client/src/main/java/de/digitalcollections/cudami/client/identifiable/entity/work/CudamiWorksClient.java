package de.digitalcollections.cudami.client.identifiable.entity.work;

import static de.digitalcollections.cudami.client.CudamiRestClient.API_VERSION_PREFIX;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.digitalcollections.cudami.client.identifiable.entity.CudamiEntitiesClient;
import de.digitalcollections.model.exception.TechnicalException;
import de.digitalcollections.model.identifiable.entity.agent.Agent;
import de.digitalcollections.model.identifiable.entity.manifestation.Manifestation;
import de.digitalcollections.model.identifiable.entity.work.Work;
import de.digitalcollections.model.list.paging.PageRequest;
import de.digitalcollections.model.list.paging.PageResponse;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.net.http.HttpClient;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.UUID;

public class CudamiWorksClient extends CudamiEntitiesClient<Work> {

  @SuppressFBWarnings(value = "SS_SHOULD_BE_STATIC", justification = "non-static is fine, though")
  private final String digiPressBaseEndpoint = API_VERSION_PREFIX + "/digipress";

  public CudamiWorksClient(HttpClient http, String serverUrl, ObjectMapper mapper) {
    super(http, serverUrl, Work.class, mapper, API_VERSION_PREFIX + "/works");
  }

  public Set<Agent> getCreators(UUID uuid) throws TechnicalException {
    return (Set<Agent>)
        doGetRequestForObjectList(String.format("%s/%s/creators", baseEndpoint, uuid), Agent.class);
  }

  public PageResponse<Work> findChildren(UUID uuid, PageRequest pageRequest)
      throws TechnicalException {
    return doGetRequestForPagedObjectList(
        String.format(baseEndpoint + "/" + uuid + "/children"), pageRequest);
  }

  public PageResponse<Manifestation> findManifestations(UUID uuid, PageRequest pageRequest)
      throws TechnicalException {
    return doGetRequestForPagedObjectList(
        String.format("%s/%s/manifestations", baseEndpoint, uuid),
        pageRequest,
        Manifestation.class);
  }

  public List<Locale> getLanguagesOfManifestations(UUID uuid) throws TechnicalException {
    return doGetRequestForObjectList(
        String.format("%s/%s/manifestations/languages", baseEndpoint, uuid), Locale.class);
  }

  public void refreshNewspapers() throws TechnicalException {
    doPutRequestForObject(digiPressBaseEndpoint + "/refresh", null);
  }
}
