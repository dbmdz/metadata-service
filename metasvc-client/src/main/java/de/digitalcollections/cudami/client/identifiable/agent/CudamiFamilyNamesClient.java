package de.digitalcollections.cudami.client.identifiable.agent;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.digitalcollections.cudami.client.identifiable.CudamiIdentifiablesClient;
import de.digitalcollections.model.identifiable.agent.FamilyName;
import java.net.http.HttpClient;
import java.util.Map;

public class CudamiFamilyNamesClient extends CudamiIdentifiablesClient<FamilyName> {

  public CudamiFamilyNamesClient(
      HttpClient http,
      String serverUrl,
      ObjectMapper mapper,
      Map<String, String> additionalGETHeaders) {
    super(
        http,
        serverUrl,
        FamilyName.class,
        mapper,
        API_VERSION_PREFIX + "/familynames",
        additionalGETHeaders);
  }
}
