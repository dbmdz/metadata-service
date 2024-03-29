package io.github.dbmdz.metadata.server.controller.identifiable.alias;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.when;

import de.digitalcollections.model.identifiable.alias.LocalizedUrlAliases;
import de.digitalcollections.model.identifiable.alias.UrlAlias;
import de.digitalcollections.model.identifiable.entity.Collection;
import de.digitalcollections.model.identifiable.entity.Website;
import de.digitalcollections.model.identifiable.web.Webpage;
import de.digitalcollections.model.list.paging.PageRequest;
import de.digitalcollections.model.list.paging.PageResponse;
import io.github.dbmdz.metadata.server.business.api.service.exceptions.ServiceException;
import io.github.dbmdz.metadata.server.business.api.service.identifiable.alias.UrlAliasService;
import io.github.dbmdz.metadata.server.controller.BaseControllerTest;
import java.util.List;
import java.util.Locale;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.invocation.InvocationOnMock;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;

@WebMvcTest(UrlAliasController.class)
@DisplayName("The UrlAliasController")
class UrlAliasControllerTest extends BaseControllerTest {

  @MockBean private UrlAliasService urlAliasService;

  @DisplayName("returns a 404, when an UrlAlias could not be found")
  @ParameterizedTest
  @ValueSource(strings = {"/v6/urlaliases/12345678-1234-1234-1234-123456789012"})
  public void nonexistingUrlAlias(String path) throws Exception {
    when(urlAliasService.getByExample(any(UrlAlias.class))).thenReturn(null);

    testNotFound(path);
  }

  @DisplayName("returns an existingUrlAlias")
  @ParameterizedTest
  @ValueSource(strings = {"/v6/urlaliases/12345678-1234-1234-1234-123456789012"})
  public void existingUrlAlias(String path) throws Exception {
    UrlAlias expectedUrlAlias =
        UrlAlias.builder()
            .created("2021-08-17T15:18:01.000001")
            .lastPublished("2021-08-17T15:18:01.000001")
            .isPrimary()
            .slug("hurz")
            .targetLanguage("de")
            .target(Collection.builder().uuid("23456789-2345-2345-2345-234567890123").build())
            .uuid("12345678-1234-1234-1234-123456789012")
            .website(Website.builder().uuid("87654321-4321-4321-4321-876543210987").build())
            .build();
    when(urlAliasService.getByExample(any(UrlAlias.class))).thenReturn(expectedUrlAlias);

    testJson(path);
  }

  @DisplayName("returns a 404 on an attempt to delete an nonexisting UrlAlias")
  @ParameterizedTest
  @ValueSource(strings = {"/v6/urlaliases/12345678-1234-1234-1234-123456789012"})
  public void deleteNonexistingUrlAlias(String path) throws Exception {
    when(urlAliasService.delete(any(UrlAlias.class))).thenReturn(false);

    testDeleteNotFound(path);
  }

  @DisplayName("returns a 204 after deleting an existing UrlAlias")
  @ParameterizedTest
  @ValueSource(strings = {"/v6/urlaliases/12345678-1234-1234-1234-123456789012"})
  public void deleteExistingUrlAlias(String path) throws Exception {
    when(urlAliasService.delete(any(UrlAlias.class))).thenReturn(true);

    testDeleteSuccessful(path);
  }

  @DisplayName("returns an error state when creating an UrlAlias with already set uuid")
  @Test
  public void createWithSetUuidLeadsToError() throws Exception {
    String body =
        """
        {\n
        \"objectType\": \"URL_ALIAS\",\n
        \"created\": \"2021-08-17T15:18:01.000001\",\n
        \"lastPublished\": \"2021-08-17T15:18:01.000001\",\n
        \"primary\": true,\n
        \"slug\": \"hurz\",\n
        \"targetLanguage\": \"de\",\n
        \"uuid\": \"12345678-1234-1234-1234-123456789012\",\n
        \"website\": {\n
          \"identifiers\":[],\n
          \"uuid\":\"87654321-4321-4321-4321-876543210987\",\n
          \"identifiableObjectType\":\"WEBSITE\",\n
          \"refId\":0\n
        }\n
      }
          """;
    testPostJsonWithState("/v6/urlaliases", body, 422);
  }

  @DisplayName("successfully creates an UrlAlias")
  @ParameterizedTest
  @ValueSource(strings = {"/v6/urlaliases"})
  public void save(String path) throws Exception {
    UrlAlias expectedUrlAlias =
        UrlAlias.builder()
            .created("2021-08-17T15:18:01.000001")
            .lastPublished("2021-08-17T15:18:01.000001")
            .isPrimary()
            .slug("hurz")
            .targetLanguage("de")
            .target(Collection.builder().uuid("23456789-2345-2345-2345-234567890123").build())
            .uuid("12345678-1234-1234-1234-123456789012")
            .website(Website.builder().uuid("87654321-4321-4321-4321-876543210987").build())
            .build();

    doAnswer(invocation -> replaceFirstArgumentData(expectedUrlAlias, invocation))
        .when(urlAliasService)
        .save(any(UrlAlias.class));

    String body =
        """
        {\n
        \"objectType\": \"URL_ALIAS\",\n
        \"created\": \"2021-08-17T15:18:01.000001\",\n
        \"lastPublished\": \"2021-08-17T15:18:01.000001\",\n
        \"primary\": true,\n
        \"slug\": \"hurz\",\n
        \"targetLanguage\": \"de\",\n
        \"target\": {\n
          \"uuid\": \"23456789-2345-2345-2345-234567890123\",\n
          \"identifiableObjectType\": \"COLLECTION\",\n
          \"type\": \"ENTITY\"\n
        },\n
        \"website\": {\n
          \"identifiers\":[],\n
          \"uuid\": \"87654321-4321-4321-4321-876543210987\",\n
          \"identifiableObjectType\": \"WEBSITE\",\n
          \"refId\":0\n
        }\n
      }
          """;
    testPostJson(path, body, "/v6/urlaliases/12345678-1234-1234-1234-123456789012.json");
  }

  @DisplayName("successfully updates an UrlAlias")
  @ParameterizedTest
  @ValueSource(strings = {"/v6/urlaliases/12345678-1234-1234-1234-123456789012"})
  public void update(String path) throws Exception {
    UrlAlias expectedUrlAlias =
        UrlAlias.builder()
            .created("2021-08-17T15:18:01.000001")
            .lastPublished("2021-08-17T15:18:01.000001")
            .isPrimary()
            .slug("hurz")
            .targetLanguage("de")
            .target(Collection.builder().uuid("23456789-2345-2345-2345-234567890123").build())
            .uuid("12345678-1234-1234-1234-123456789012")
            .website(Website.builder().uuid("87654321-4321-4321-4321-876543210987").build())
            .build();

    doAnswer(invocation -> replaceFirstArgumentData(expectedUrlAlias, invocation))
        .when(urlAliasService)
        .update(any(UrlAlias.class));

    String body =
        """
        {\n
          \"objectType\": \"URL_ALIAS\",\n
          \"created\": \"2021-08-17T15:18:01.000001\",\n
          \"lastPublished\": \"2021-08-17T15:18:01.000001\",\n
          \"primary\": true,\n
          \"slug\": \"hurz\",\n
          \"targetLanguage\": \"de\",\n
          \"target\": {\n
            \"uuid\": \"23456789-2345-2345-2345-234567890123\",\n
            \"identifiableObjectType\": \"COLLECTION\",\n
            \"type\": \"ENTITY\"\n
          },\n
          \"uuid\": \"12345678-1234-1234-1234-123456789012\",\n
          \"website\": {\n
            \"identifiers\":[],\n
            \"uuid\":\"87654321-4321-4321-4321-876543210987\",\n
            \"identifiableObjectType\":\"WEBSITE\",\n
            \"refId\":0\n
          }\n
        }
            """;

    testPutJson(path, body, "/v6/urlaliases/12345678-1234-1234-1234-123456789012.json");
  }

  @DisplayName("returns an error state when updating an UrlAlias with missing uuid")
  @Test
  public void updateWithMissingUuidLeadsToError() throws Exception {
    String body =
        """
        {\n
          \"objectType\": \"URL_ALIAS\",\n
          \"created\": \"2021-08-17T15:18:01.000001\",\n
          \"lastPublished\": \"2021-08-17T15:18:01.000001\",\n
          \"primary\": true,\n
          \"slug\": \"hurz\",\n
          \"targetLanguage\": \"de\",\n
          \"website\": {\n
            \"identifiers\":[],\n
            \"uuid\":\"87654321-4321-4321-4321-876543210987\",\n
            \"identifiableObjectType\":\"WEBSITE\",\n
            \"refId\":0\n
          }\n
        }
            """;

    testPutJsonWithState("/v6/urlaliases/12345678-1234-1234-1234-123456789012", body, 422);
  }

  @DisplayName("can return a find result with empty content")
  @ParameterizedTest
  @ValueSource(strings = {"/v6/urlaliases"})
  public void findWithEmptyResult(String path) throws Exception {
    PageResponse<LocalizedUrlAliases> expected =
        PageResponse.builder().forPageSize(1).withTotalElements(0).build();

    when(urlAliasService.findLocalizedUrlAliases(any(PageRequest.class))).thenReturn(expected);

    testJson(path, "/v6/urlaliases/empty.json");
  }

  @DisplayName("can return a find result with existing content")
  @ParameterizedTest
  @ValueSource(strings = {"/v6/urlaliases?pageNumber=0&pageSize=1"})
  public void findWithFilledResult(String path) throws Exception {
    PageResponse<LocalizedUrlAliases> expected =
        (PageResponse<LocalizedUrlAliases>)
            PageResponse.builder()
                .forPageSize(1)
                .withTotalElements(319)
                .withContent(
                    List.of(
                        new LocalizedUrlAliases(
                            UrlAlias.builder()
                                .created("2021-08-17T15:18:01.000001")
                                .lastPublished("2021-08-17T15:18:01.000001")
                                .isPrimary()
                                .slug("hurz")
                                .targetLanguage("de")
                                .target(
                                    Collection.builder()
                                        .uuid("23456789-2345-2345-2345-234567890123")
                                        .build())
                                .uuid("12345678-1234-1234-1234-123456789012")
                                .website(
                                    Website.builder()
                                        .uuid("87654321-4321-4321-4321-876543210987")
                                        .build())
                                .build())))
                .build();

    when(urlAliasService.findLocalizedUrlAliases(any(PageRequest.class))).thenReturn(expected);

    testJson(path, "/v6/urlaliases/find_with_result.json");
  }

  @DisplayName(
      "returns a 404, when the parameters for retrieving the primary links, are invalid and so don't match the endpoint URLs")
  @ParameterizedTest
  @ValueSource(
      strings = {
        "/v6/urlaliases/primary//12345678-1234-1234-1234-123456789012",
        "/v6/urlaliases/primary/slug/invalid-uuid"
      })
  public void invalidParametersForPrimaryLinks(String path) throws Exception {
    testNotFound(path);
  }

  @DisplayName(
      "returns a 404 for primary links, when no primary links for a given slug/webpage tuple exist")
  @Test
  public void nonexistingPrimaryLinks() throws Exception {
    when(urlAliasService.getPrimaryUrlAliases(
            any(Website.class), eq("notexisting"), any(Locale.class)))
        .thenReturn(null);

    testNotFound("/v6/urlaliases/primary/notexisting/12345678-1234-1234-1234-123456789012");
  }

  @DisplayName("can return the primary links for a given slug/webpage tuple")
  @ParameterizedTest
  @ValueSource(strings = {"/v6/urlaliases/primary/imprint/87654321-4321-4321-4321-876543210987"})
  public void existingPrimaryLinks(String path) throws Exception {
    LocalizedUrlAliases expected = new LocalizedUrlAliases();
    expected.add(
        UrlAlias.builder()
            .created("2021-08-17T15:18:01.000001")
            .lastPublished("2021-08-17T15:18:01.000001")
            .isPrimary()
            .slug("imprint")
            .targetLanguage("en")
            .target(Webpage.builder().uuid("23456789-2345-2345-2345-234567890123").build())
            .uuid("12345678-1234-1234-1234-123456789012")
            .website(Website.builder().uuid("87654321-4321-4321-4321-876543210987").build())
            .build());

    when(urlAliasService.getPrimaryUrlAliases(any(Website.class), eq("imprint"), eq(null)))
        .thenReturn(expected);

    testJson(path, "/v6/urlaliases/primary_imprint_87654321-4321-4321-4321-876543210987.json");
  }

  @DisplayName("can return the primary links for a given slug/webpage tuple and locale filter")
  @ParameterizedTest
  @ValueSource(
      strings = {"/v6/urlaliases/primary/imprint/87654321-4321-4321-4321-876543210987?pLocale=de"})
  public void existingPrimaryLinksWithLocale(String path) throws Exception {
    LocalizedUrlAliases expected = new LocalizedUrlAliases();
    expected.add(
        UrlAlias.builder()
            .created("2021-08-17T15:18:01.000001")
            .lastPublished("2021-08-17T15:18:01.000001")
            .isPrimary()
            .slug("imprint")
            .targetLanguage("de")
            .target(Webpage.builder().uuid("23456789-2345-2345-2345-234567890123").build())
            .uuid("12345678-1234-1234-1234-123456789012")
            .website(Website.builder().uuid("87654321-4321-4321-4321-876543210987").build())
            .build());

    Locale actualLocale = Locale.forLanguageTag("de");
    when(urlAliasService.getPrimaryUrlAliases(any(Website.class), eq("imprint"), eq(actualLocale)))
        .thenReturn(expected);

    testJson(path, "/v6/urlaliases/primary_imprint_87654321-4321-4321-4321-876543210987_de.json");
  }

  @DisplayName("can return the primary links for a given slug but empty website_uuid")
  @ParameterizedTest
  @ValueSource(strings = {"/v6/urlaliases/primary/imprint"})
  public void existingPrimaryLinksForEmptyWebsiteUuid(String path) throws Exception {
    LocalizedUrlAliases expected = new LocalizedUrlAliases();
    expected.add(
        UrlAlias.builder()
            .created("2021-08-17T15:18:01.000001")
            .lastPublished("2021-08-17T15:18:01.000001")
            .isPrimary()
            .slug("imprint")
            .targetLanguage("en")
            .target(Webpage.builder().uuid("23456789-2345-2345-2345-234567890123").build())
            .uuid("12345678-1234-1234-1234-123456789012")
            .build());

    when(urlAliasService.getPrimaryUrlAliases(eq((Website) null), eq("imprint"), eq((Locale) null)))
        .thenReturn(expected);

    testJson(path, "/v6/urlaliases/primary_imprint.json");
  }

  @DisplayName("throws an exception, when the service fails on generating a slug")
  @Test
  public void exceptionOnSlugGeneration() throws Exception {
    when(urlAliasService.generateSlug(any(Locale.class), any(String.class), any(Website.class)))
        .thenThrow(new ServiceException("foo"));

    testInternalError("/v6/urlaliases/slug/de_DE/label/12345678-1234-1234-1234-123456789012");
  }

  @DisplayName("returns 404 when trying to generate a slug for a nonexisting website uuid")
  @Test
  public void slugForNonexistingWebsiteUuid() throws Exception {
    when(urlAliasService.generateSlug(any(Locale.class), any(String.class), any(Website.class)))
        .thenReturn(null);

    testNotFound("/v6/urlaliases/slug/de_DE/label/12345678-1234-1234-1234-123456789012");
  }

  @DisplayName("returns a generated slug")
  @Test
  public void generateSlug() throws Exception {
    when(urlAliasService.generateSlug(any(Locale.class), any(String.class), any(Website.class)))
        .thenReturn("hurz");

    testGetJsonString(
        "/v6/urlaliases/slug/de_DE/label/12345678-1234-1234-1234-123456789012", "\"hurz\"");
  }

  @DisplayName("returns a generated slug, when no website id is provided")
  @Test
  public void generateSlugWithoutWebsiteId() throws Exception {
    when(urlAliasService.generateSlug(any(Locale.class), any(String.class), eq(null)))
        .thenReturn("hurz");

    testGetJsonString("/v6/urlaliases/slug/de_DE/label", "\"hurz\"");
  }

  // ------------------------------------------------------------
  private static Object replaceFirstArgumentData(UrlAlias expected, InvocationOnMock invocation) {
    Object[] args = invocation.getArguments();
    ((UrlAlias) args[0]).setUuid(expected.getUuid());
    ((UrlAlias) args[0]).setPrimary(expected.isPrimary());
    ((UrlAlias) args[0]).setWebsite(expected.getWebsite());
    return null;
  }
}
