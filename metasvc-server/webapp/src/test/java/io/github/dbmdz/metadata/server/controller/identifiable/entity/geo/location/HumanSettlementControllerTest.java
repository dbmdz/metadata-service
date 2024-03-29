package io.github.dbmdz.metadata.server.controller.identifiable.entity.geo.location;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import de.digitalcollections.model.identifiable.Identifier;
import de.digitalcollections.model.identifiable.entity.geo.location.HumanSettlement;
import io.github.dbmdz.metadata.server.business.api.service.identifiable.entity.geo.location.HumanSettlementService;
import io.github.dbmdz.metadata.server.controller.BaseControllerTest;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;

@WebMvcTest(HumanSettlementController.class)
@DisplayName("The HumanSettlementController")
class HumanSettlementControllerTest extends BaseControllerTest {

  @MockBean private HumanSettlementService humanSettlementService;

  @DisplayName("can retrieve by identifier with plaintext id")
  @ParameterizedTest
  @ValueSource(
      strings = {
        "/v6/humansettlements/identifier/foo:bar",
        "/v5/humansettlements/identifier/foo:bar",
        "/v2/humansettlements/identifier/foo:bar",
        "/latest/humansettlements/identifier/foo:bar",
        "/v6/humansettlements/identifier/foo:bar.json",
        "/v5/humansettlements/identifier/foo:bar.json",
        "/v2/humansettlements/identifier/foo:bar.json",
        "/latest/humansettlements/identifier/foo:bar.json"
      })
  void testGetByIdentifierWithPlaintextId(String path) throws Exception {
    HumanSettlement expected = HumanSettlement.builder().build();

    when(humanSettlementService.getByIdentifier(
            eq(Identifier.builder().namespace("foo").id("bar").build())))
        .thenReturn(expected);

    testHttpGet(path);

    verify(humanSettlementService, times(1))
        .getByIdentifier(eq(Identifier.builder().namespace("foo").id("bar").build()));
  }

  @DisplayName("can retrieve by identifier with base 64 encoded data")
  @ParameterizedTest
  @ValueSource(
      strings = {
        "/v6/humansettlements/identifier/",
        "/v5/humansettlements/identifier/",
        "/v2/humansettlements/identifier/",
        "/latest/humansettlements/identifier/"
      })
  void testGetByIdentifierWithBase64EncodedData(String basePath) throws Exception {
    HumanSettlement expected = HumanSettlement.builder().build();

    when(humanSettlementService.getByIdentifier(
            eq(Identifier.builder().namespace("foo").id("bar/bla").build())))
        .thenReturn(expected);

    testHttpGet(
        basePath
            + Base64.getEncoder().encodeToString("foo:bar/bla".getBytes(StandardCharsets.UTF_8)));

    verify(humanSettlementService, times(1))
        .getByIdentifier(eq(Identifier.builder().namespace("foo").id("bar/bla").build()));
  }
}
