package de.digitalcollections.cudami.client.config;

import static de.digitalcollections.cudami.client.CudamiRestClient.API_VERSION_PREFIX;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.digitalcollections.client.BaseRestClient;
import de.digitalcollections.cudami.model.config.CudamiConfig;
import de.digitalcollections.model.exception.TechnicalException;
import java.net.http.HttpClient;
import java.util.Map;

public class CudamiConfigClient extends BaseRestClient<CudamiConfig> {

  public CudamiConfigClient(
      HttpClient http,
      String serverUrl,
      ObjectMapper mapper,
      Map<String, String> additionalGETHeaders) {
    super(
        http,
        serverUrl,
        CudamiConfig.class,
        mapper,
        API_VERSION_PREFIX + "/config",
        additionalGETHeaders);
  }

  public CudamiConfig getConfig() throws TechnicalException {
    return (CudamiConfig) doGetRequestForObject(baseEndpoint, CudamiConfig.class);
  }
}
