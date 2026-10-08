package de.digitalcollections.cudami.client.identifiable.entity.geo.location;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.digitalcollections.cudami.client.identifiable.entity.CudamiEntitiesClient;
import de.digitalcollections.model.identifiable.entity.geo.location.River;
import java.net.http.HttpClient;
import java.util.Map;

public class CudamiRiversClient extends CudamiEntitiesClient<River> {

  public CudamiRiversClient(
      HttpClient http,
      String serverUrl,
      ObjectMapper mapper,
      Map<String, String> additionalGETHeaders) {
    super(
        http, serverUrl, River.class, mapper, API_VERSION_PREFIX + "/rivers", additionalGETHeaders);
  }
}
