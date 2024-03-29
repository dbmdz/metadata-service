package io.github.dbmdz.metadata.server.controller.identifiable;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import de.digitalcollections.model.identifiable.IdentifierType;
import de.digitalcollections.model.list.paging.PageRequest;
import de.digitalcollections.model.list.paging.PageResponse;
import io.github.dbmdz.metadata.server.business.api.service.identifiable.IdentifierTypeService;
import io.github.dbmdz.metadata.server.controller.BaseControllerTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;

@WebMvcTest(V2IdentifierTypeController.class)
@DisplayName("The V2IdentifierTypeController")
class V2IdentifierTypeControllerTest extends BaseControllerTest {

  @MockBean private IdentifierTypeService identifierTypeService;

  @DisplayName("returns a list of identifier types")
  @ParameterizedTest
  @ValueSource(strings = {"/v2/identifiertypes?pageNumber=0&pageSize=5"})
  public void identifierTypesList(String path) throws Exception {
    PageResponse<IdentifierType> expected =
        (PageResponse)
            PageResponse.builder()
                .forPageSize(5)
                .forRequestPage(0)
                .forAscendingOrderedField("namespace")
                .withContent(
                    IdentifierType.builder()
                        .label("MDZ-ID")
                        .namespace("mdz-obj")
                        .pattern("^bsb[0-9]{8}$")
                        .uuid("d0e7f4b8-7d0c-4233-b58d-20437477672b")
                        .build())
                .build();

    when(identifierTypeService.find(any(PageRequest.class))).thenReturn(expected);

    testJson(path, "/v2/identifiertypes/identifiertypes.json");
  }
}
