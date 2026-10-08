package de.digitalcollections.cudami.client.identifiable.entity.geo.location;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.digitalcollections.cudami.client.identifiable.entity.CudamiEntitiesClient;
import de.digitalcollections.model.identifiable.entity.geo.location.Cave;
import java.net.http.HttpClient;
import java.util.Map;

public class CudamiCavesClient extends CudamiEntitiesClient<Cave> {

  public CudamiCavesClient(
      HttpClient http,
      String serverUrl,
      ObjectMapper mapper,
      Map<String, String> additionalGETHeaders) {
    super(http, serverUrl, Cave.class, mapper, API_VERSION_PREFIX + "/caves", additionalGETHeaders);
  }
}
