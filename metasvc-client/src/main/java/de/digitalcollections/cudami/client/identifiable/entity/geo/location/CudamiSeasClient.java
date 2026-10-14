package de.digitalcollections.cudami.client.identifiable.entity.geo.location;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.digitalcollections.cudami.client.identifiable.entity.CudamiEntitiesClient;
import de.digitalcollections.model.identifiable.entity.geo.location.Sea;
import java.net.http.HttpClient;
import java.util.Map;

public class CudamiSeasClient extends CudamiEntitiesClient<Sea> {

  public CudamiSeasClient(
      HttpClient http,
      String serverUrl,
      ObjectMapper mapper,
      Map<String, String> additionalGETHeaders) {
    super(http, serverUrl, Sea.class, mapper, API_VERSION_PREFIX + "/seas", additionalGETHeaders);
  }
}
