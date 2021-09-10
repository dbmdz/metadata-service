package de.digitalcollections.cudami.server.controller.identifiable.alias;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

import de.digitalcollections.cudami.server.business.api.service.exceptions.CudamiServiceException;
import de.digitalcollections.cudami.server.business.api.service.identifiable.alias.UrlAliasService;
import de.digitalcollections.cudami.server.controller.BaseControllerTest;
import de.digitalcollections.cudami.server.model.LocalizedUrlAliasBuilder;
import de.digitalcollections.cudami.server.model.SearchPageResponseBuilder;
import de.digitalcollections.cudami.server.model.UrlAliasBuilder;
import de.digitalcollections.model.identifiable.IdentifiableType;
import de.digitalcollections.model.identifiable.alias.LocalizedUrlAliases;
import de.digitalcollections.model.identifiable.alias.UrlAlias;
import de.digitalcollections.model.identifiable.entity.EntityType;
import de.digitalcollections.model.paging.SearchPageRequest;
import de.digitalcollections.model.paging.SearchPageResponse;
import java.util.List;
import java.util.Locale;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;

@WebMvcTest(UrlAliasController.class)
@DisplayName("The UrlAliasController")
class UrlAliasControllerTest extends BaseControllerTest {

  @MockBean private UrlAliasService urlAliasService;

  @DisplayName("returns a 404, when an UrlAlias could not be found")
  @ParameterizedTest
  @ValueSource(strings = {"/v5/urlaliases/12345678-1234-1234-1234-123456789012"})
  public void nonexistingUrlAlias(String path) throws Exception {
    when(urlAliasService.findOne(any(UUID.class))).thenReturn(null);

    testNotFound(path);
  }

  @DisplayName("returns an existingUrlAlias")
  @ParameterizedTest
  @ValueSource(strings = {"/v5/urlaliases/12345678-1234-1234-1234-123456789012"})
  public void existingUrlAlias(String path) throws Exception {
    UrlAlias expectedUrlAlias =
        new UrlAliasBuilder()
            .createdAt("2021-08-17T15:18:01.000001")
            .lastPublishedAt("2021-08-17T15:18:01.000001")
            .isMainAlias()
            .withSlug("hurz")
            .withTargetLanguage("de")
            .withTargetType(IdentifiableType.ENTITY, EntityType.COLLECTION)
            .withTargetUuid("23456789-2345-2345-2345-234567890123")
            .withUuid("12345678-1234-1234-1234-123456789012")
            .withWebsiteUuid("87654321-4321-4321-4321-876543210987")
            .build();
    when(urlAliasService.findOne(any(UUID.class))).thenReturn(expectedUrlAlias);

    testJson(path);
  }

  @DisplayName("returns a 404 on an attempt to delete an nonexisting UrlAlias")
  @ParameterizedTest
  @ValueSource(strings = {"/v5/urlaliases/12345678-1234-1234-1234-123456789012"})
  public void deleteNonexistingUrlAlias(String path) throws Exception {
    when(urlAliasService.delete(any(UUID.class))).thenReturn(false);

    testDeleteNotFound(path);
  }

  @DisplayName("returns a 204 after deleting an existing UrlAlias")
  @ParameterizedTest
  @ValueSource(strings = {"/v5/urlaliases/12345678-1234-1234-1234-123456789012"})
  public void deleteExistingUrlAlias(String path) throws Exception {
    when(urlAliasService.delete(any(UUID.class))).thenReturn(true);

    testDeleteSuccessful(path);
  }

  @DisplayName("returns an error state when creating an UrlAlias with already set uuid")
  @Test
  public void createWithSetUuidLeadsToError() throws Exception {
    String body =
        "{\n"
            + "  \"created\": \"2021-08-17T15:18:01.000001\",\n"
            + "  \"lastPublished\": \"2021-08-17T15:18:01.000001\",\n"
            + "  \"mainAlias\": true,\n"
            + "  \"slug\": \"hurz\",\n"
            + "  \"targetLanguage\": \"de\",\n"
            + "  \"targetIdentifiableType\": \"ENTITY\",\n"
            + "  \"targetEntityType\": \"COLLECTION\",\n"
            + "  \"uuid\": \"12345678-1234-1234-1234-123456789012\",\n"
            + "  \"targetUuid\": \"23456789-2345-2345-2345-234567890123\",\n"
            + "  \"websiteUuid\": \"87654321-4321-4321-4321-876543210987\"\n"
            + "}";

    testPostJsonWithState("/v5/urlaliases", body, 422);
  }

  @DisplayName("successfully creates an UrlAlias")
  @ParameterizedTest
  @ValueSource(strings = {"/v5/urlaliases"})
  public void save(String path) throws Exception {
    UrlAlias expectedUrlAlias =
        new UrlAliasBuilder()
            .createdAt("2021-08-17T15:18:01.000001")
            .lastPublishedAt("2021-08-17T15:18:01.000001")
            .isMainAlias()
            .withSlug("hurz")
            .withTargetLanguage("de")
            .withTargetType(IdentifiableType.ENTITY, EntityType.COLLECTION)
            .withTargetUuid("23456789-2345-2345-2345-234567890123")
            .withUuid("12345678-1234-1234-1234-123456789012")
            .withWebsiteUuid("87654321-4321-4321-4321-876543210987")
            .build();
    when(urlAliasService.create(any(UrlAlias.class))).thenReturn(expectedUrlAlias);

    String body =
        "{\n"
            + "  \"created\": \"2021-08-17T15:18:01.000001\",\n"
            + "  \"lastPublished\": \"2021-08-17T15:18:01.000001\",\n"
            + "  \"mainAlias\": true,\n"
            + "  \"slug\": \"hurz\",\n"
            + "  \"targetLanguage\": \"de\",\n"
            + "  \"targetIdentifiableType\": \"ENTITY\",\n"
            + "  \"targetEntityType\": \"COLLECTION\",\n"
            + "  \"targetUuid\": \"23456789-2345-2345-2345-234567890123\",\n"
            + "  \"websiteUuid\": \"87654321-4321-4321-4321-876543210987\"\n"
            + "}";

    testPostJson(path, body, "/v5/urlaliases/12345678-1234-1234-1234-123456789012.json");
  }

  @DisplayName("successfully updates an UrlAlias")
  @ParameterizedTest
  @ValueSource(strings = {"/v5/urlaliases/12345678-1234-1234-1234-123456789012"})
  public void update(String path) throws Exception {
    UrlAlias expectedUrlAlias =
        new UrlAliasBuilder()
            .createdAt("2021-08-17T15:18:01.000001")
            .lastPublishedAt("2021-08-17T15:18:01.000001")
            .isMainAlias()
            .withSlug("hurz")
            .withTargetLanguage("de")
            .withTargetType(IdentifiableType.ENTITY, EntityType.COLLECTION)
            .withTargetUuid("23456789-2345-2345-2345-234567890123")
            .withUuid("12345678-1234-1234-1234-123456789012")
            .withWebsiteUuid("87654321-4321-4321-4321-876543210987")
            .build();
    when(urlAliasService.update(any(UrlAlias.class))).thenReturn(expectedUrlAlias);

    String body =
        "{\n"
            + "  \"created\": \"2021-08-17T15:18:01.000001\",\n"
            + "  \"lastPublished\": \"2021-08-17T15:18:01.000001\",\n"
            + "  \"mainAlias\": true,\n"
            + "  \"slug\": \"hurz\",\n"
            + "  \"targetLanguage\": \"de\",\n"
            + "  \"targetIdentifiableType\": \"ENTITY\",\n"
            + "  \"targetEntityType\": \"COLLECTION\",\n"
            + "  \"uuid\": \"12345678-1234-1234-1234-123456789012\",\n"
            + "  \"targetUuid\": \"23456789-2345-2345-2345-234567890123\",\n"
            + "  \"websiteUuid\": \"87654321-4321-4321-4321-876543210987\"\n"
            + "}";

    testPutJson(path, body, "/v5/urlaliases/12345678-1234-1234-1234-123456789012.json");
  }

  @DisplayName("returns an error state when updating an UrlAlias with missing uuid")
  @Test
  public void updateWithMissingUuidLeadsToError() throws Exception {
    String body =
        "{\n"
            + "  \"created\": \"2021-08-17T15:18:01.000001\",\n"
            + "  \"lastPublished\": \"2021-08-17T15:18:01.000001\",\n"
            + "  \"mainAlias\": true,\n"
            + "  \"slug\": \"hurz\",\n"
            + "  \"targetLanguage\": \"de\",\n"
            + "  \"targetIdentifiableType\": \"ENTITY\",\n"
            + "  \"targetEntityType\": \"COLLECTION\",\n"
            + "  \"targetUuid\": \"23456789-2345-2345-2345-234567890123\",\n"
            + "  \"websiteUuid\": \"87654321-4321-4321-4321-876543210987\"\n"
            + "}";

    testPutJsonWithState("/v5/urlaliases/12345678-1234-1234-1234-123456789012", body, 422);
  }

  @DisplayName("can return a find result with empty content")
  @ParameterizedTest
  @ValueSource(strings = {"/v5/urlaliases"})
  public void findWithEmptyResult(String path) throws Exception {
    SearchPageResponse<LocalizedUrlAliases> expected =
        (SearchPageResponse<LocalizedUrlAliases>)
            new SearchPageResponseBuilder().forPageSize(1).withTotalElements(0).build();

    when(urlAliasService.find(any(SearchPageRequest.class))).thenReturn(expected);

    testJson(path, "/v5/urlaliases/empty.json");
  }

  @DisplayName("can return a find result with existing content")
  @ParameterizedTest
  @ValueSource(strings = {"/v5/urlaliases?pageNumber=0&pageSize=1"})
  public void findWithFilledResult(String path) throws Exception {
    SearchPageResponse<LocalizedUrlAliases> expected =
        (SearchPageResponse<LocalizedUrlAliases>)
            new SearchPageResponseBuilder()
                .forPageSize(1)
                .withTotalElements(319)
                .withContent(
                    List.of(
                        new LocalizedUrlAliasBuilder()
                            .addUrlAlias(
                                new UrlAliasBuilder()
                                    .createdAt("2021-08-17T15:18:01.000001")
                                    .lastPublishedAt("2021-08-17T15:18:01.000001")
                                    .isMainAlias()
                                    .withSlug("hurz")
                                    .withTargetLanguage("de")
                                    .withTargetType(IdentifiableType.ENTITY, EntityType.COLLECTION)
                                    .withTargetUuid("23456789-2345-2345-2345-234567890123")
                                    .withUuid("12345678-1234-1234-1234-123456789012")
                                    .withWebsiteUuid("87654321-4321-4321-4321-876543210987")
                                    .build())
                            .build()))
                .build();

    when(urlAliasService.find(any(SearchPageRequest.class))).thenReturn(expected);

    testJson(path, "/v5/urlaliases/find_with_result.json");
  }

  @DisplayName(
      "returns a 404, when the parameters for retrieving a main link, are invalid and so don't match the endpoint URLs")
  @ParameterizedTest
  @ValueSource(
      strings = {
        "/v5/urlaliases/12345678-1234-1234-1234-123456789012/",
        "/v5/urlaliases//slug",
        "/v5/urlaliases/foo/slug"
      })
  public void invalidParamtersForMainLink(String path) throws Exception {
    testNotFound(path);
  }

  @DisplayName(
      "returns a 404 for a main link, when no main link for a given webpage/slug pair exists")
  @Test
  public void nonexistingMainLink() throws Exception {
    when(urlAliasService.findMainLink(any(UUID.class), eq("notexisting"))).thenReturn(null);

    testNotFound("/v5/urlalises/12345678-1234-1234-1234-123456789012/notexisting");
  }

  @DisplayName("can return a mainLink for a given webpage/slug pair")
  @ParameterizedTest
  @ValueSource(strings = {"/v5/urlaliases/87654321-4321-4321-4321-876543210987/imprint"})
  public void existingMainLink(String path) throws Exception {
    LocalizedUrlAliases expected = new LocalizedUrlAliases();
    expected.add(
        new UrlAliasBuilder()
            .createdAt("2021-08-17T15:18:01.000001")
            .lastPublishedAt("2021-08-17T15:18:01.000001")
            .isMainAlias()
            .withSlug("imprint")
            .withTargetLanguage("en")
            .withTargetType(IdentifiableType.RESOURCE, null)
            .withTargetUuid("23456789-2345-2345-2345-234567890123")
            .withUuid("12345678-1234-1234-1234-123456789012")
            .withWebsiteUuid("87654321-4321-4321-4321-876543210987")
            .build());

    when(urlAliasService.findMainLink(any(UUID.class), eq("imprint"))).thenReturn(expected);

    testJson(path);
  }

  @DisplayName("throws an exception, when the service fails on generating a slug")
  @Test
  public void exceptionOnSlugGeneration() throws Exception {
    when(urlAliasService.generateSlug(any(Locale.class), any(String.class), any(UUID.class)))
        .thenThrow(new CudamiServiceException("foo"));

    testInternalError("/v5/urlaliases/slug/de_DE/label/12345678-1234-1234-1234-123456789012");
  }

  @DisplayName("returns 404 when trying to generate a slug for a nonexisting website uuid")
  @Test
  public void slugForNonexistingWebsiteUuid() throws Exception {
    when(urlAliasService.generateSlug(any(Locale.class), any(String.class), any(UUID.class)))
        .thenReturn(null);

    testNotFound("/v5/urlaliases/slug/de_DE/label/12345678-1234-1234-1234-123456789012");
  }

  @DisplayName("returns a generated slug")
  @Test
  public void generateSlug() throws Exception {
    when(urlAliasService.generateSlug(any(Locale.class), any(String.class), any(UUID.class)))
        .thenReturn("hurz");

    testGetJsonString(
        "/v5/urlaliases/slug/de_DE/label/12345678-1234-1234-1234-123456789012", "hurz");
  }

  @DisplayName("returns a generated slug, when no website id is provided")
  @Test
  public void generateSlugWithoutWebsiteId() throws Exception {
    when(urlAliasService.generateSlug(any(Locale.class), any(String.class), eq(null)))
        .thenReturn("hurz");

    testGetJsonString("/v5/urlaliases/slug/de_DE/label", "hurz");
  }
}
