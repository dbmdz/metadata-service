package de.digitalcollections.cudami.client;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.digitalcollections.cudami.client.config.CudamiConfigClient;
import de.digitalcollections.cudami.client.identifiable.CudamiIdentifiablesClient;
import de.digitalcollections.cudami.client.identifiable.CudamiIdentifierTypesClient;
import de.digitalcollections.cudami.client.identifiable.agent.CudamiFamilyNamesClient;
import de.digitalcollections.cudami.client.identifiable.agent.CudamiGivenNamesClient;
import de.digitalcollections.cudami.client.identifiable.alias.CudamiUrlAliasClient;
import de.digitalcollections.cudami.client.identifiable.entity.CudamiArticlesClient;
import de.digitalcollections.cudami.client.identifiable.entity.CudamiCollectionsClient;
import de.digitalcollections.cudami.client.identifiable.entity.CudamiDigitalObjectsClient;
import de.digitalcollections.cudami.client.identifiable.entity.CudamiEntitiesClient;
import de.digitalcollections.cudami.client.identifiable.entity.CudamiEventsClient;
import de.digitalcollections.cudami.client.identifiable.entity.CudamiHeadwordEntriesClient;
import de.digitalcollections.cudami.client.identifiable.entity.CudamiProjectsClient;
import de.digitalcollections.cudami.client.identifiable.entity.CudamiTopicsClient;
import de.digitalcollections.cudami.client.identifiable.entity.CudamiWebsitesClient;
import de.digitalcollections.cudami.client.identifiable.entity.agent.CudamiAgentsClient;
import de.digitalcollections.cudami.client.identifiable.entity.agent.CudamiCorporateBodiesClient;
import de.digitalcollections.cudami.client.identifiable.entity.agent.CudamiPersonsClient;
import de.digitalcollections.cudami.client.identifiable.entity.geo.location.CudamiGeoLocationsClient;
import de.digitalcollections.cudami.client.identifiable.entity.geo.location.CudamiHumanSettlementsClient;
import de.digitalcollections.cudami.client.identifiable.entity.relation.CudamiEntityRelationsClient;
import de.digitalcollections.cudami.client.identifiable.entity.semantic.CudamiSubjectsClient;
import de.digitalcollections.cudami.client.identifiable.entity.work.CudamiItemsClient;
import de.digitalcollections.cudami.client.identifiable.entity.work.CudamiManifestationsClient;
import de.digitalcollections.cudami.client.identifiable.entity.work.CudamiWorksClient;
import de.digitalcollections.cudami.client.identifiable.resource.CudamiFileResourcesBinaryClient;
import de.digitalcollections.cudami.client.identifiable.resource.CudamiFileResourcesMetadataClient;
import de.digitalcollections.cudami.client.identifiable.resource.CudamiImageFileResourcesClient;
import de.digitalcollections.cudami.client.identifiable.resource.CudamiLinkedDataFileResourcesClient;
import de.digitalcollections.cudami.client.identifiable.web.CudamiWebpagesClient;
import de.digitalcollections.cudami.client.legal.CudamiLicensesClient;
import de.digitalcollections.cudami.client.relation.CudamiPredicatesClient;
import de.digitalcollections.cudami.client.security.CudamiUsersClient;
import de.digitalcollections.cudami.client.semantic.CudamiHeadwordsClient;
import de.digitalcollections.cudami.client.semantic.CudamiTagsClient;
import de.digitalcollections.cudami.client.view.CudamiRenderingTemplatesClient;
import de.digitalcollections.model.identifiable.Identifiable;
import de.digitalcollections.model.identifiable.entity.Entity;
import java.net.http.HttpClient;
import java.net.http.HttpClient.Redirect;
import java.net.http.HttpClient.Version;
import java.time.Duration;
import java.util.Map;

public class CudamiClient {

  private final CudamiAgentsClient cudamiAgentsClient;
  private final CudamiArticlesClient cudamiArticlesClient;
  private final CudamiCollectionsClient cudamiCollectionsClient;
  private final CudamiConfigClient cudamiConfigClient;
  private final CudamiCorporateBodiesClient cudamiCorporateBodiesClient;
  private final CudamiDigitalObjectsClient cudamiDigitalObjectsClient;
  private final CudamiEntitiesClient<Entity> cudamiEntitiesClient;
  private final CudamiEntityRelationsClient cudamiEntityRelationsClient;
  private final CudamiEventsClient cudamiEventsClient;
  private final CudamiFamilyNamesClient cudamiFamilyNamesClient;
  private final CudamiFileResourcesBinaryClient cudamiFileResourcesBinaryClient;
  private final CudamiFileResourcesMetadataClient cudamiFileResourcesMetadataClient;
  private final CudamiGeoLocationsClient cudamiGeoLocationsClient;
  private final CudamiGivenNamesClient cudamiGivenNamesClient;
  private final CudamiHeadwordEntriesClient cudamiHeadwordEntriesClient;
  private final CudamiHeadwordsClient cudamiHeadwordsClient;
  private final CudamiHumanSettlementsClient cudamiHumanSettlementsClient;
  private final CudamiIdentifiablesClient<Identifiable> cudamiIdentifiablesClient;
  private final CudamiIdentifierTypesClient cudamiIdentifierTypesClient;
  private final CudamiImageFileResourcesClient cudamiImageFileResourcesClient;
  private final CudamiItemsClient cudamiItemsClient;
  private final CudamiLicensesClient cudamiLicensesClient;
  private final CudamiLinkedDataFileResourcesClient cudamiLinkedDataFileResourcesClient;
  private final CudamiLocalesClient cudamiLocalesClient;
  private final CudamiManifestationsClient cudamiManifestationsClient;
  private final CudamiPersonsClient cudamiPersonsClient;
  private final CudamiPredicatesClient cudamiPredicatesClient;
  private final CudamiProjectsClient cudamiProjectsClient;
  private final CudamiRenderingTemplatesClient cudamiRenderingTemplatesClient;
  private final CudamiSubjectsClient cudamiSubjectsClient;
  private final CudamiTagsClient cudamiTagsClient;
  private final CudamiTopicsClient cudamiTopicsClient;
  private final CudamiUrlAliasClient cudamiUrlAliasClient;
  private final CudamiUsersClient cudamiUsersClient;
  private final CudamiWebpagesClient cudamiWebpagesClient;
  private final CudamiWebsitesClient cudamiWebsitesClient;
  private final CudamiWorksClient cudamiWorksClient;
  protected final HttpClient http;

  public CudamiClient(String cudamiServerUrl, ObjectMapper mapper) {
    this(cudamiServerUrl, mapper, null);
  }

  public CudamiClient(
      String cudamiServerUrl, ObjectMapper mapper, Map<String, String> additionalGETHeaders) {
    this(
        HttpClient.newBuilder()
            .followRedirects(Redirect.ALWAYS)
            .connectTimeout(Duration.ofSeconds(10))
            .version(Version.HTTP_1_1)
            .build(),
        cudamiServerUrl,
        mapper,
        additionalGETHeaders);
  }

  public CudamiClient(HttpClient http, String cudamiServerUrl, ObjectMapper mapper) {
    this(http, cudamiServerUrl, mapper, null);
  }

  public CudamiClient(
      HttpClient http,
      String cudamiServerUrl,
      ObjectMapper mapper,
      Map<String, String> additionalGETHeaders) {
    this.http = http;
    this.cudamiAgentsClient =
        new CudamiAgentsClient(http, cudamiServerUrl, mapper, additionalGETHeaders);
    this.cudamiArticlesClient =
        new CudamiArticlesClient(http, cudamiServerUrl, mapper, additionalGETHeaders);
    this.cudamiCollectionsClient =
        new CudamiCollectionsClient(http, cudamiServerUrl, mapper, additionalGETHeaders);
    this.cudamiConfigClient =
        new CudamiConfigClient(http, cudamiServerUrl, mapper, additionalGETHeaders);
    this.cudamiCorporateBodiesClient =
        new CudamiCorporateBodiesClient(http, cudamiServerUrl, mapper, additionalGETHeaders);
    this.cudamiDigitalObjectsClient =
        new CudamiDigitalObjectsClient(http, cudamiServerUrl, mapper, additionalGETHeaders);
    this.cudamiEntitiesClient =
        new CudamiEntitiesClient<>(http, cudamiServerUrl, mapper, additionalGETHeaders);
    this.cudamiEntityRelationsClient =
        new CudamiEntityRelationsClient(http, cudamiServerUrl, mapper, additionalGETHeaders);
    this.cudamiEventsClient =
        new CudamiEventsClient(http, cudamiServerUrl, mapper, additionalGETHeaders);
    this.cudamiFamilyNamesClient =
        new CudamiFamilyNamesClient(http, cudamiServerUrl, mapper, additionalGETHeaders);
    this.cudamiFileResourcesBinaryClient =
        new CudamiFileResourcesBinaryClient(cudamiServerUrl, mapper);
    this.cudamiFileResourcesMetadataClient =
        new CudamiFileResourcesMetadataClient(http, cudamiServerUrl, mapper, additionalGETHeaders);
    this.cudamiGeoLocationsClient =
        new CudamiGeoLocationsClient(http, cudamiServerUrl, mapper, additionalGETHeaders);
    this.cudamiGivenNamesClient =
        new CudamiGivenNamesClient(http, cudamiServerUrl, mapper, additionalGETHeaders);
    this.cudamiHeadwordsClient =
        new CudamiHeadwordsClient(http, cudamiServerUrl, mapper, additionalGETHeaders);
    this.cudamiHeadwordEntriesClient =
        new CudamiHeadwordEntriesClient(http, cudamiServerUrl, mapper, additionalGETHeaders);
    this.cudamiHumanSettlementsClient =
        new CudamiHumanSettlementsClient(http, cudamiServerUrl, mapper, additionalGETHeaders);
    this.cudamiIdentifiablesClient =
        new CudamiIdentifiablesClient<>(http, cudamiServerUrl, mapper, additionalGETHeaders);
    this.cudamiIdentifierTypesClient =
        new CudamiIdentifierTypesClient(http, cudamiServerUrl, mapper, additionalGETHeaders);
    this.cudamiImageFileResourcesClient =
        new CudamiImageFileResourcesClient(http, cudamiServerUrl, mapper, additionalGETHeaders);
    this.cudamiItemsClient =
        new CudamiItemsClient(http, cudamiServerUrl, mapper, additionalGETHeaders);
    this.cudamiLicensesClient =
        new CudamiLicensesClient(http, cudamiServerUrl, mapper, additionalGETHeaders);
    this.cudamiLinkedDataFileResourcesClient =
        new CudamiLinkedDataFileResourcesClient(
            http, cudamiServerUrl, mapper, additionalGETHeaders);
    this.cudamiLocalesClient =
        new CudamiLocalesClient(http, cudamiServerUrl, mapper, additionalGETHeaders);
    this.cudamiManifestationsClient =
        new CudamiManifestationsClient(http, cudamiServerUrl, mapper, additionalGETHeaders);
    this.cudamiPersonsClient =
        new CudamiPersonsClient(http, cudamiServerUrl, mapper, additionalGETHeaders);
    this.cudamiPredicatesClient =
        new CudamiPredicatesClient(http, cudamiServerUrl, mapper, additionalGETHeaders);
    this.cudamiProjectsClient =
        new CudamiProjectsClient(http, cudamiServerUrl, mapper, additionalGETHeaders);
    this.cudamiRenderingTemplatesClient =
        new CudamiRenderingTemplatesClient(http, cudamiServerUrl, mapper, additionalGETHeaders);
    this.cudamiSubjectsClient =
        new CudamiSubjectsClient(http, cudamiServerUrl, mapper, additionalGETHeaders);
    this.cudamiTagsClient =
        new CudamiTagsClient(http, cudamiServerUrl, mapper, additionalGETHeaders);
    this.cudamiTopicsClient =
        new CudamiTopicsClient(http, cudamiServerUrl, mapper, additionalGETHeaders);
    this.cudamiUrlAliasClient =
        new CudamiUrlAliasClient(http, cudamiServerUrl, mapper, additionalGETHeaders);
    this.cudamiUsersClient =
        new CudamiUsersClient(http, cudamiServerUrl, mapper, additionalGETHeaders);
    this.cudamiWebpagesClient =
        new CudamiWebpagesClient(http, cudamiServerUrl, mapper, additionalGETHeaders);
    this.cudamiWebsitesClient =
        new CudamiWebsitesClient(http, cudamiServerUrl, mapper, additionalGETHeaders);
    this.cudamiWorksClient =
        new CudamiWorksClient(http, cudamiServerUrl, mapper, additionalGETHeaders);
  }

  public CudamiAgentsClient forAgents() {
    return cudamiAgentsClient;
  }

  public CudamiArticlesClient forArticles() {
    return cudamiArticlesClient;
  }

  public CudamiCollectionsClient forCollections() {
    return cudamiCollectionsClient;
  }

  public CudamiConfigClient forConfig() {
    return cudamiConfigClient;
  }

  public CudamiCorporateBodiesClient forCorporateBodies() {
    return cudamiCorporateBodiesClient;
  }

  public CudamiDigitalObjectsClient forDigitalObjects() {
    return cudamiDigitalObjectsClient;
  }

  public CudamiEntitiesClient forEntities() {
    return cudamiEntitiesClient;
  }

  public CudamiEventsClient forEvents() {
    return cudamiEventsClient;
  }

  public CudamiEntityRelationsClient forEntityRelations() {
    return cudamiEntityRelationsClient;
  }

  public CudamiFamilyNamesClient forFamilyNames() {
    return cudamiFamilyNamesClient;
  }

  public CudamiFileResourcesBinaryClient forFileResourcesBinary() {
    return cudamiFileResourcesBinaryClient;
  }

  public CudamiFileResourcesMetadataClient forFileResourcesMetadata() {
    return cudamiFileResourcesMetadataClient;
  }

  public CudamiGeoLocationsClient forGeoLocations() {
    return cudamiGeoLocationsClient;
  }

  public CudamiGivenNamesClient forGivenNames() {
    return cudamiGivenNamesClient;
  }

  public CudamiHeadwordEntriesClient forHeadwordEntries() {
    return cudamiHeadwordEntriesClient;
  }

  public CudamiHeadwordsClient forHeadwords() {
    return cudamiHeadwordsClient;
  }

  public CudamiHumanSettlementsClient forHumanSettlements() {
    return cudamiHumanSettlementsClient;
  }

  public CudamiIdentifiablesClient forIdentifiables() {
    return cudamiIdentifiablesClient;
  }

  public CudamiIdentifierTypesClient forIdentifierTypes() {
    return cudamiIdentifierTypesClient;
  }

  public CudamiImageFileResourcesClient forImageFileResources() {
    return cudamiImageFileResourcesClient;
  }

  public CudamiItemsClient forItems() {
    return cudamiItemsClient;
  }

  public CudamiLicensesClient forLicenses() {
    return cudamiLicensesClient;
  }

  public CudamiLinkedDataFileResourcesClient forLinkedDataFileResources() {
    return cudamiLinkedDataFileResourcesClient;
  }

  public CudamiLocalesClient forLocales() {
    return cudamiLocalesClient;
  }

  public CudamiManifestationsClient forManifestations() {
    return cudamiManifestationsClient;
  }

  public CudamiPersonsClient forPersons() {
    return cudamiPersonsClient;
  }

  public CudamiPredicatesClient forPredicates() {
    return cudamiPredicatesClient;
  }

  public CudamiProjectsClient forProjects() {
    return cudamiProjectsClient;
  }

  public CudamiRenderingTemplatesClient forRenderingTemplates() {
    return cudamiRenderingTemplatesClient;
  }

  public CudamiSubjectsClient forSubjects() {
    return cudamiSubjectsClient;
  }

  public CudamiTagsClient forTags() {
    return cudamiTagsClient;
  }

  public CudamiTopicsClient forTopics() {
    return cudamiTopicsClient;
  }

  public CudamiUrlAliasClient forUrlAliases() {
    return cudamiUrlAliasClient;
  }

  public CudamiUsersClient forUsers() {
    return cudamiUsersClient;
  }

  public CudamiWebpagesClient forWebpages() {
    return cudamiWebpagesClient;
  }

  public CudamiWebsitesClient forWebsites() {
    return cudamiWebsitesClient;
  }

  public CudamiWorksClient forWorks() {
    return cudamiWorksClient;
  }
}
