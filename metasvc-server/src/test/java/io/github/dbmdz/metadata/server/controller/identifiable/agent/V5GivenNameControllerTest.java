package io.github.dbmdz.metadata.server.controller.identifiable.agent;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import de.digitalcollections.model.identifiable.agent.GivenName;
import de.digitalcollections.model.list.paging.PageRequest;
import de.digitalcollections.model.list.paging.PageResponse;
import io.github.dbmdz.metadata.server.business.api.service.identifiable.agent.GivenNameService;
import io.github.dbmdz.metadata.server.controller.BaseControllerTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;

@WebMvcTest(V5GivenNameController.class)
@DisplayName("The V5GivenNameController")
class V5GivenNameControllerTest extends BaseControllerTest {

  @MockBean private GivenNameService givenNameService;

  @DisplayName("shall return a paged list of given names")
  @ParameterizedTest
  @ValueSource(strings = {"/v5/givennames?pageSize=1&pageNumber=0"})
  void testFind(String path) throws Exception {
    PageResponse<GivenName> expected =
        (PageResponse<GivenName>)
            PageResponse.builder()
                .forRequestPage(0)
                .forPageSize(1)
                .withTotalElements(0)
                .forAscendingOrderedField("label", "de")
                .forAscendingOrderedField("label")
                .forAscendingOrderedField("uuid")
                .build();

    when(givenNameService.find(any(PageRequest.class))).thenReturn(expected);

    testJson(path, "/v5/givennames/find_with_empty_result.json");
  }
}
