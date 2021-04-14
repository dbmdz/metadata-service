package de.digitalcollections.cudami.server.controller.identifiable.entity;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import de.digitalcollections.cudami.server.business.api.service.identifiable.entity.WebsiteService;
import de.digitalcollections.cudami.server.controller.BaseControllerTest;
import de.digitalcollections.cudami.server.model.DescriptionBuilder;
import de.digitalcollections.cudami.server.model.WebpageBuilder;
import de.digitalcollections.cudami.server.model.WebsiteBuilder;
import de.digitalcollections.model.identifiable.entity.Website;
import de.digitalcollections.model.paging.Direction;
import de.digitalcollections.model.paging.NullHandling;
import de.digitalcollections.model.paging.OrderBuilder;
import de.digitalcollections.model.paging.PageRequestBuilder;
import de.digitalcollections.model.paging.SearchPageRequest;
import de.digitalcollections.model.paging.SearchPageResponse;
import de.digitalcollections.model.paging.SortingBuilder;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;

@WebMvcTest(V2WebsiteController.class)
class V2WebsiteControllerTest extends BaseControllerTest {

  @MockBean private WebsiteService websiteService;

  @DisplayName(
      "returns a website in v2 json format for UUID, with or without json suffix in the url")
  @ParameterizedTest
  @ValueSource(
      strings = {
        "/v2/websites/7ebaf4b1-cf5a-491b-991c-4fd082677ff9.json",
        "/v2/websites/7ebaf4b1-cf5a-491b-991c-4fd082677ff9"
      })
  public void returnWebsiteV2Json(String path) throws Exception {
    Website expected =
        new WebsiteBuilder()
            .createdAt("2018-05-04T09:05:47.493")
            .lastModifiedAt("2018-05-04T09:05:47.493")
            .withLabel(Locale.GERMAN, "Altsinica")
            .withDescription(
                new DescriptionBuilder().setLanguage("de").setType("paragraph").build())
            .withUuidFromPath(path)
            .withRefId(84)
            .withUrl("https://ostasien.digitale-sammlungen.de/")
            .withRootPages(
                List.of(
                    new WebpageBuilder()
                        .setUUID("6d9adace-187a-4f14-9a5a-e768558028a3")
                        .setCreated("2018-05-04T09:06:05.333")
                        .setLastModified("2020-09-30T16:23:44.393791")
                        .setLabel(Map.of("de", "Impressum"))
                        .setPublicationStart("2020-09-30")
                        .setIdentifiers(Set.of())
                        .build(),
                    new WebpageBuilder()
                        .setUUID("b0739393-2fdc-4703-8af1-c3b440292872")
                        .setCreated("2020-03-12T12:28:57.082438")
                        .setLastModified("2020-09-30T16:24:43.844093")
                        .setLabel(Map.of("de", "Barrierefreiheit"))
                        .setPublicationStart("2020-09-30")
                        .setIdentifiers(Set.of())
                        .build(),
                    new WebpageBuilder()
                        .setUUID("cbb85056-5e30-49cf-bd87-fd09486b9aa9")
                        .setCreated("2018-05-04T09:06:19.201")
                        .setLastModified("2020-09-30T16:24:23.379512")
                        .setLabel(Map.of("de", "Datenschutzerklärung"))
                        .setPublicationStart("2020-09-30")
                        .setIdentifiers(Set.of())
                        .build()))
            .build();

    /*
    Website website = new Website();
    website.setCreated(LocalDateTime.parse());
    website.setLastModified(LocalDateTime.parse("2018-05-04T09:05:47.493"));
    website.setIdentifiers(Set.of());
    website.setLabel(new LocalizedText(Locale.GERMAN, "Altsinica"));
    website.setDescription(new DescriptionBuilder().setLanguage("de").setType("paragraph").build());
    website.setType(IdentifiableType.ENTITY);
    website.setUuid(extractFirstUuidFromPath(path));
    website.setRefId(84);
    website.setUrl(new URL("https://ostasien.digitale-sammlungen.de/"));
    website.setRootPages(
        List.of(
            new WebpageBuilder()
                .setUUID("6d9adace-187a-4f14-9a5a-e768558028a3")
                .setCreated("2018-05-04T09:06:05.333")
                .setLastModified("2020-09-30T16:23:44.393791")
                .setLabel(Map.of("de", "Impressum"))
                .setPublicationStart("2020-09-30")
                .setIdentifiers(Set.of())
                .build(),
            new WebpageBuilder()
                .setUUID("b0739393-2fdc-4703-8af1-c3b440292872")
                .setCreated("2020-03-12T12:28:57.082438")
                .setLastModified("2020-09-30T16:24:43.844093")
                .setLabel(Map.of("de", "Barrierefreiheit"))
                .setPublicationStart("2020-09-30")
                .setIdentifiers(Set.of())
                .build(),
            new WebpageBuilder()
                .setUUID("cbb85056-5e30-49cf-bd87-fd09486b9aa9")
                .setCreated("2018-05-04T09:06:19.201")
                .setLastModified("2020-09-30T16:24:23.379512")
                .setLabel(Map.of("de", "Datenschutzerklärung"))
                .setPublicationStart("2020-09-30")
                .setIdentifiers(Set.of())
                .build()));
    */
    when(websiteService.get(any(UUID.class))).thenReturn(expected);

    testJson(path);
  }

  @DisplayName("returns a paged list of websites")
  @ParameterizedTest
  @ValueSource(strings = {"/v2/websites?pageNumber=0&pageSize=1"})
  public void pagedWebsites(String path) throws Exception {
    SearchPageResponse<Website> expected = new SearchPageResponse();
    expected.setTotalElements(82);
    expected.setPageRequest(
        new PageRequestBuilder()
            .pageSize(1)
            .pageNumber(0)
            .sorting(
                new SortingBuilder()
                    .order(
                        new OrderBuilder()
                            .direction(Direction.ASC)
                            .property("label")
                            .subProperty("de")
                            .nullHandling(NullHandling.NATIVE)
                            .build())
                    .order(
                        new OrderBuilder()
                            .direction(Direction.ASC)
                            .property("label")
                            .subProperty("")
                            .nullHandling(NullHandling.NATIVE)
                            .build())
                    .build())
            .build());
    List<Website> websites =
        List.of(
            new WebsiteBuilder()
                .createdAt("2018-05-02T13:32:52.582")
                .withSimpleDescription(Locale.GERMAN, "")
                .withLabel(Locale.GERMAN, "Testseite")
                .lastModifiedAt("2018-09-11T09:47:40.311")
                .withUuid("e91464a1-588b-434b-a88e-b6a1c3824c85")
                .withRefId(71)
                .withUrl("https://www.digitale-sammlungen.de/")
                .build());
    expected.setContent(websites);

    when(websiteService.find(any(SearchPageRequest.class))).thenReturn(expected);

    testJson(path);
  }
}
