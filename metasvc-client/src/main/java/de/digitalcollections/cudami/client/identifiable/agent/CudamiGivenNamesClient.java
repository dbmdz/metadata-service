package de.digitalcollections.cudami.client.identifiable.agent;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.digitalcollections.cudami.client.identifiable.CudamiIdentifiablesClient;
import de.digitalcollections.model.identifiable.agent.GivenName;
import java.net.http.HttpClient;
import java.util.Map;

public class CudamiGivenNamesClient extends CudamiIdentifiablesClient<GivenName> {

  public CudamiGivenNamesClient(
      HttpClient http,
      String serverUrl,
      ObjectMapper mapper,
      Map<String, String> additionalGETHeaders) {
    super(
        http,
        serverUrl,
        GivenName.class,
        mapper,
        API_VERSION_PREFIX + "/givennames",
        additionalGETHeaders);
  }
}
