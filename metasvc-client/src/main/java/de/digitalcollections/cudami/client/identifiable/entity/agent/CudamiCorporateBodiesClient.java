package de.digitalcollections.cudami.client.identifiable.entity.agent;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.digitalcollections.cudami.client.identifiable.entity.CudamiEntitiesClient;
import de.digitalcollections.model.exception.TechnicalException;
import de.digitalcollections.model.exception.http.client.ResourceNotFoundException;
import de.digitalcollections.model.identifiable.entity.agent.CorporateBody;
import java.net.http.HttpClient;
import java.util.Map;

public class CudamiCorporateBodiesClient extends CudamiEntitiesClient<CorporateBody> {

  public CudamiCorporateBodiesClient(
      HttpClient http,
      String serverUrl,
      ObjectMapper mapper,
      Map<String, String> additionalGETHeaders) {
    super(
        http,
        serverUrl,
        CorporateBody.class,
        mapper,
        API_VERSION_PREFIX + "/corporatebodies",
        additionalGETHeaders);
  }

  public CorporateBody fetchAndSaveByGndId(String gndId) throws TechnicalException {
    try {
      return doPostRequestForObject(String.format("%s/gnd/%s", baseEndpoint, gndId));
    } catch (ResourceNotFoundException e) {
      return null;
    }
  }
}
