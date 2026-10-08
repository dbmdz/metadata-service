package de.digitalcollections.cudami.lobid.client;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.digitalcollections.cudami.lobid.client.mapper.Lobid2DCModelMapper;
import de.digitalcollections.model.exception.TechnicalException;
import de.digitalcollections.model.identifiable.semantic.Subject;
import dev.mdz.lobid.model.LobidSubject;
import java.net.http.HttpClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LobidSubjectsClient extends LobidBaseClient<LobidSubject> {

  private static final Logger LOGGER = LoggerFactory.getLogger(LobidSubjectsClient.class);

  LobidSubjectsClient(HttpClient http, String serverUrl, ObjectMapper mapper) {
    super(http, serverUrl, LobidSubject.class, mapper);
  }

  public Subject getByGndId(String gndId) throws TechnicalException {
    LobidSubject lobidSubject = doGetRequestForObject(String.format("/gnd/%s.json", gndId));
    Subject subject = Lobid2DCModelMapper.mapToSubject(lobidSubject, null);
    return subject;
  }
}
