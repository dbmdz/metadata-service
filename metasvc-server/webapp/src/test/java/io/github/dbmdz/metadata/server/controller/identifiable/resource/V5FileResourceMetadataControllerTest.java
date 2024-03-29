package io.github.dbmdz.metadata.server.controller.identifiable.resource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import de.digitalcollections.model.file.MimeType;
import de.digitalcollections.model.identifiable.resource.FileResource;
import de.digitalcollections.model.identifiable.resource.LinkedDataFileResource;
import de.digitalcollections.model.list.filtering.FilterCriterion;
import de.digitalcollections.model.list.filtering.FilterOperation;
import de.digitalcollections.model.list.filtering.Filtering;
import de.digitalcollections.model.list.paging.PageRequest;
import de.digitalcollections.model.list.paging.PageResponse;
import io.github.dbmdz.metadata.server.business.api.service.identifiable.resource.FileResourceMetadataService;
import io.github.dbmdz.metadata.server.controller.BaseControllerTest;
import java.util.List;
import java.util.Locale;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.ArgumentCaptor;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;

@WebMvcTest(V5FileResourceMetadataController.class)
@DisplayName("The V5 FileResourceMetadata Controller")
class V5FileResourceMetadataControllerTest extends BaseControllerTest {

  @MockBean(name = "fileResourceMetadataService")
  private FileResourceMetadataService<FileResource> fileResourceMetadataService;

  @DisplayName("can return a filtered and paged list of LinkedDataFileResources")
  @ParameterizedTest
  @ValueSource(
      strings = {"/v5/fileresources?pageNumber=0&pageSize=1&uri=eq:http%3A%2F%2Ffoo.bar%2Fbla.xml"})
  public void find(String path) throws Exception {
    PageResponse<FileResource> expected =
        (PageResponse)
            PageResponse.builder()
                .forPageSize(1)
                .forAscendingOrderedField("label", "de")
                .forAscendingOrderedField("label")
                .withTotalElements(1)
                .withContent(
                    LinkedDataFileResource.builder()
                        .uuid("12345678-abcd-1234-abcd-123456789012")
                        .label(Locale.GERMAN, "Test-Label")
                        .mimeType(MimeType.MIME_APPLICATION_XML)
                        .uri("http://foo.bar/bla.xml")
                        .build())
                .build();

    when(fileResourceMetadataService.find(any(PageRequest.class))).thenReturn(expected);

    ArgumentCaptor<PageRequest> pageRequestArgumentCaptor =
        ArgumentCaptor.forClass(PageRequest.class);

    PageRequest expectedPageRequest = new PageRequest();
    expectedPageRequest.setPageSize(1);
    expectedPageRequest.setPageNumber(0);
    FilterCriterion filterCriterion =
        new FilterCriterion("uri", FilterOperation.EQUALS, "http://foo.bar/bla.xml");
    Filtering filtering = new Filtering(List.of(filterCriterion));
    expectedPageRequest.setFiltering(filtering);

    testJson(path, "/v5/fileresources/filteredlist.json");

    verify(fileResourceMetadataService, times(1)).find(pageRequestArgumentCaptor.capture());
    assertThat(pageRequestArgumentCaptor.getValue()).isEqualTo(expectedPageRequest);
  }

  @DisplayName("shall return a paged list of file resources")
  @ParameterizedTest
  @ValueSource(
      strings = {
        "/v5/fileresources?pageSize=1&pageNumber=0",
        "/v2/fileresources?pageSize=1&pageNumber=0",
        "/latest/fileresources?pageSize=1&pageNumber=0"
      })
  void testFind(String path) throws Exception {
    PageResponse<FileResource> expected =
        (PageResponse<FileResource>)
            PageResponse.builder()
                .forRequestPage(0)
                .forPageSize(1)
                .withTotalElements(1)
                .forAscendingOrderedField("label", "de")
                .forAscendingOrderedField("label")
                .withContent(
                    List.of(
                        LinkedDataFileResource.builder()
                            .label(Locale.GERMAN, "Test-Label")
                            .filename("bla.xml")
                            .mimeType(MimeType.MIME_APPLICATION_XML)
                            .uri("http://foo.bar/bla.xml")
                            .uuid("12345678-abcd-1234-abcd-123456789012")
                            .build()))
                .build();

    when(fileResourceMetadataService.find(any(PageRequest.class))).thenReturn(expected);

    testJson(path, "/v5/fileresources/filteredlist.json");
  }

  @DisplayName("shall return a paged list of file resources, filtered by type")
  @ParameterizedTest
  @ValueSource(
      strings = {
        "/v5/fileresources/type/application?pageSize=1&pageNumber=0",
        "/v2/fileresources/type/application?pageSize=1&pageNumber=0",
        "/latest/fileresources/type/application?pageSize=1&pageNumber=0"
      })
  void testFindImages(String path) throws Exception {
    PageResponse<FileResource> expected =
        (PageResponse<FileResource>)
            PageResponse.builder()
                .forRequestPage(0)
                .forPageSize(1)
                .withTotalElements(1)
                .forAscendingOrderedField("label", "de")
                .forAscendingOrderedField("label")
                .withContent(
                    List.of(
                        LinkedDataFileResource.builder()
                            .label(Locale.GERMAN, "Test-Label")
                            .filename("bla.xml")
                            .mimeType(MimeType.MIME_APPLICATION_XML)
                            .uri("http://foo.bar/bla.xml")
                            .uuid("12345678-abcd-1234-abcd-123456789012")
                            .build()))
                .build();

    when(fileResourceMetadataService.find(any(PageRequest.class))).thenReturn(expected);

    testJson(path, "/v5/fileresources/filteredlist.json");
  }
}
