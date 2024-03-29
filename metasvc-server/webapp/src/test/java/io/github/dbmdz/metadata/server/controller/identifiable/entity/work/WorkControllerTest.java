package io.github.dbmdz.metadata.server.controller.identifiable.entity.work;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import de.digitalcollections.model.identifiable.Identifier;
import de.digitalcollections.model.identifiable.entity.work.Work;
import io.github.dbmdz.metadata.server.business.api.service.identifiable.entity.agent.AgentService;
import io.github.dbmdz.metadata.server.business.api.service.identifiable.entity.work.ItemService;
import io.github.dbmdz.metadata.server.business.api.service.identifiable.entity.work.ManifestationService;
import io.github.dbmdz.metadata.server.business.api.service.identifiable.entity.work.WorkService;
import io.github.dbmdz.metadata.server.controller.BaseControllerTest;
import java.nio.charset.StandardCharsets;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;

@WebMvcTest(WorkController.class)
@DisplayName("The WorkController")
class WorkControllerTest extends BaseControllerTest {

  @MockBean private WorkService workService;
  @MockBean private ItemService itemService;
  @MockBean private AgentService agentService;
  @MockBean private ManifestationService manifestationService;

  @DisplayName("can retrieve by identifier with plaintext id")
  @ParameterizedTest
  @ValueSource(
      strings = {
        "/v6/works/identifier/foo:bar",
        "/v5/works/identifier/foo:bar",
        "/v2/works/identifier/foo:bar",
        "/latest/works/identifier/foo:bar",
        "/v6/works/identifier/foo:bar.json",
        "/v5/works/identifier/foo:bar.json",
        "/v2/works/identifier/foo:bar.json",
        "/latest/works/identifier/foo:bar.json"
      })
  void testGetByIdentifierWithPlaintextId(String path) throws Exception {
    Work expected = new Work();

    when(workService.getByIdentifier(eq(Identifier.builder().namespace("foo").id("bar").build())))
        .thenReturn(expected);

    testHttpGet(path);

    verify(workService, times(1))
        .getByIdentifier(eq(Identifier.builder().namespace("foo").id("bar").build()));
  }

  @DisplayName("can retrieve by identifier with base 64 encoded data")
  @ParameterizedTest
  @ValueSource(
      strings = {
        "/v6/works/identifier/",
        "/v5/works/identifier/",
        "/v2/works/identifier/",
        "/latest/works/identifier/"
      })
  void testGetByIdentifierWithBase64EncodedData(String basePath) throws Exception {
    Work expected = new Work();

    when(workService.getByIdentifier(
            eq(Identifier.builder().namespace("foo").id("bar/bla").build())))
        .thenReturn(expected);

    testHttpGet(
        basePath
            + java.util.Base64.getEncoder()
                .encodeToString("foo:bar/bla".getBytes(StandardCharsets.UTF_8)));

    verify(workService, times(1))
        .getByIdentifier(eq(Identifier.builder().namespace("foo").id("bar/bla").build()));
  }
}
