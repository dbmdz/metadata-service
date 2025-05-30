package io.github.dbmdz.metadata.server.controller.identifiable.entity.work;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import de.digitalcollections.model.identifiable.Identifier;
import de.digitalcollections.model.identifiable.entity.digitalobject.DigitalObject;
import de.digitalcollections.model.identifiable.entity.item.Item;
import de.digitalcollections.model.list.filtering.FilterCriterion;
import de.digitalcollections.model.list.filtering.Filtering;
import de.digitalcollections.model.list.paging.PageRequest;
import de.digitalcollections.model.list.paging.PageResponse;
import io.github.dbmdz.metadata.server.business.api.service.identifiable.entity.DigitalObjectService;
import io.github.dbmdz.metadata.server.business.api.service.identifiable.entity.work.ItemService;
import io.github.dbmdz.metadata.server.business.api.service.identifiable.entity.work.WorkService;
import io.github.dbmdz.metadata.server.controller.BaseControllerTest;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.ArgumentCaptor;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;

@WebMvcTest(ItemController.class)
@DisplayName("The ItemController")
class ItemControllerTest extends BaseControllerTest {

  @MockBean private DigitalObjectService digitalObjectService;
  @MockBean private ItemService itemService;
  @MockBean private WorkService workService;

  @DisplayName("can retrieve an item by identifier without any special characters")
  @ParameterizedTest
  @ValueSource(strings = {"/v6/items/identifier/foo:bar", "/v6/items/identifier/foo:bar.json"})
  public void getByIdentifierWithoutSpecialCharacters(String path) throws Exception {
    when(itemService.getByIdentifier(any(Identifier.class))).thenReturn(Item.builder().build());

    ArgumentCaptor<Identifier> identifierCaptor = ArgumentCaptor.forClass(Identifier.class);

    testHttpGet(path);

    verify(itemService, times(1)).getByIdentifier(identifierCaptor.capture());

    assertThat(identifierCaptor.getValue().getNamespace()).isEqualTo("foo");
    assertThat(identifierCaptor.getValue().getId()).isEqualTo("bar");
  }

  @DisplayName("can retrieve an item by identifier with unencoded slashes as identifier")
  @ParameterizedTest
  @ValueSource(
      strings = {"/v6/items/identifier/foo:bar/baz", "/v6/items/identifier/foo:bar/baz.json"})
  public void getByIdentifierWithUnencodedSlashesAsIdentifier(String path) throws Exception {
    when(itemService.getByIdentifier(any(Identifier.class))).thenReturn(Item.builder().build());

    ArgumentCaptor<Identifier> identifierCaptor = ArgumentCaptor.forClass(Identifier.class);

    testHttpGet(path);

    verify(itemService, times(1)).getByIdentifier(identifierCaptor.capture());

    assertThat(identifierCaptor.getValue().getNamespace()).isEqualTo("foo");
    assertThat(identifierCaptor.getValue().getId()).isEqualTo("bar/baz");
  }

  @DisplayName("can retrieve an item by identifier with Base64 encoded slashes as identifier")
  @ParameterizedTest
  @ValueSource(
      strings = {
        "/v6/items/identifier/",
      })
  public void getByIdentifierWithBase64EncodedSlashesAsIdentifier() throws Exception {
    String path =
        "/v6/items/identifier/"
            + Base64.getEncoder().encodeToString("foo:bar/baz".getBytes(StandardCharsets.UTF_8));

    when(itemService.getByIdentifier(any(Identifier.class))).thenReturn(Item.builder().build());

    ArgumentCaptor<Identifier> identifierCaptor = ArgumentCaptor.forClass(Identifier.class);

    testHttpGet(path);

    verify(itemService, times(1)).getByIdentifier(identifierCaptor.capture());

    assertThat(identifierCaptor.getValue().getNamespace()).isEqualTo("foo");
    assertThat(identifierCaptor.getValue().getId()).isEqualTo("bar/baz");
  }

  @DisplayName("can retrieve by identifier with plaintext id")
  @ParameterizedTest
  @ValueSource(
      strings = {
        "/v6/items/identifier/foo:bar",
        "/v6/items/identifier/foo:bar.json",
        "/v5/items/identifier/foo:bar",
        "/v5/items/identifier/foo:bar.json",
        "/v2/items/identifier/foo:bar",
        "/v2/items/identifier/foo:bar.json",
        "/latest/items/identifier/foo:bar",
        "/latest/items/identifier/foo:bar.json"
      })
  void testGetByIdentifierWithPlaintextId(String path) throws Exception {
    Item expected = Item.builder().build();

    when(itemService.getByIdentifier(eq(Identifier.builder().namespace("foo").id("bar").build())))
        .thenReturn(expected);

    testHttpGet(path);

    verify(itemService, times(1))
        .getByIdentifier(eq(Identifier.builder().namespace("foo").id("bar").build()));
  }

  @DisplayName("can retrieve by identifier with base 64 encoded data")
  @ParameterizedTest
  @ValueSource(
      strings = {
        "/v6/items/identifier/",
        "/v5/items/identifier/",
        "/v2/items/identifier/",
        "/latest/items/identifier/"
      })
  void testGetByIdentifierWithBase64EncodedData(String basePath) throws Exception {
    Item expected = Item.builder().build();

    when(itemService.getByIdentifier(
            eq(Identifier.builder().namespace("foo").id("bar/bla").build())))
        .thenReturn(expected);

    testHttpGet(
        basePath
            + java.util.Base64.getEncoder()
                .encodeToString("foo:bar/bla".getBytes(StandardCharsets.UTF_8)));

    verify(itemService, times(1))
        .getByIdentifier(eq(Identifier.builder().namespace("foo").id("bar/bla").build()));
  }

  @DisplayName("can filter items by the uuid of their \"parent\" item")
  @Test
  public void filterByPartOfItemUuid() throws Exception {
    UUID uuid = UUID.randomUUID();
    testHttpGet("/v6/items?pageNumber=0&pageSize=100&filter=[part_of_item]:eq:" + uuid);
    PageRequest expectedPageRequest =
        PageRequest.builder()
            .pageNumber(0)
            .pageSize(100)
            .filtering(
                Filtering.builder()
                    .add(
                        FilterCriterion.nativeBuilder()
                            .withExpression("part_of_item")
                            .isEquals(uuid.toString())
                            .build())
                    .build())
            .build();
    verify(itemService, times(1)).find(eq(expectedPageRequest));
  }

  @DisplayName("can return a paged list of DigitalObjects for an item, even it the item")
  @ParameterizedTest
  @ValueSource(
      strings = {
        "/v6/items/1c72ae9a-94e1-45b1-848f-da1303000924/digitalobjects?pageSize=25&pageNumber=0"
      })
  public void pageListOfDigitalObjectsForItem(String path) throws Exception {
    DigitalObject expectedDigitalObject =
        DigitalObject.builder()
            .uuid(UUID.fromString("604ed5f3-d245-4829-b8ad-297cc947af7e"))
            .label("Hello")
            .build();
    UUID itemUuid = UUID.fromString("1c72ae9a-94e1-45b1-848f-da1303000924");
    PageResponse<DigitalObject> expectedPageResponse =
        PageResponse.builder()
            .forPageSize(25)
            .forRequestPage(0)
            .withTotalElements(1)
            .withContent(List.of(expectedDigitalObject))
            .build();
    when(digitalObjectService.findDigitalObjectsByItem(
            eq(Item.builder().uuid(itemUuid).build()), any(PageRequest.class)))
        .thenReturn(expectedPageResponse);

    testJson(path, "/v6/items/1c72ae9a-94e1-45b1-848f-da1303000924_digitalobjects.json");
  }
}
