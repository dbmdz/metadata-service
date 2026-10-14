package de.digitalcollections.cudami.client.identifiable.entity.geo.location;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.digitalcollections.cudami.client.identifiable.entity.CudamiEntitiesClient;
import de.digitalcollections.model.identifiable.entity.geo.location.StillWaters;
import java.net.http.HttpClient;
import java.util.Map;

public class CudamiStillWatersClient extends CudamiEntitiesClient<StillWaters> {

  public CudamiStillWatersClient(
      HttpClient http,
      String serverUrl,
      ObjectMapper mapper,
      Map<String, String> additionalGETHeaders) {
    super(
        http,
        serverUrl,
        StillWaters.class,
        mapper,
        API_VERSION_PREFIX + "/stillwaters",
        additionalGETHeaders);
  }
}
