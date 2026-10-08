package de.digitalcollections.cudami.client.identifiable.entity.geo.location;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.digitalcollections.cudami.client.identifiable.entity.CudamiEntitiesClient;
import de.digitalcollections.model.identifiable.entity.geo.location.Country;
import java.net.http.HttpClient;
import java.util.Map;

public class CudamiCountriesClient extends CudamiEntitiesClient<Country> {

  public CudamiCountriesClient(
      HttpClient http,
      String serverUrl,
      ObjectMapper mapper,
      Map<String, String> additionalGETHeaders) {
    super(
        http,
        serverUrl,
        Country.class,
        mapper,
        API_VERSION_PREFIX + "/countries",
        additionalGETHeaders);
  }
}
