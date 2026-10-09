package de.digitalcollections.cudami.client.identifiable.entity.geo.location;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.digitalcollections.cudami.client.identifiable.entity.CudamiEntitiesClient;
import de.digitalcollections.model.identifiable.entity.geo.location.Continent;
import java.net.http.HttpClient;
import java.util.Map;

public class CudamiContinentsClient extends CudamiEntitiesClient<Continent> {

  public CudamiContinentsClient(
      HttpClient http,
      String serverUrl,
      ObjectMapper mapper,
      Map<String, String> additionalGETHeaders) {
    super(
        http,
        serverUrl,
        Continent.class,
        mapper,
        API_VERSION_PREFIX + "/continents",
        additionalGETHeaders);
  }
}
