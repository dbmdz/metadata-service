package de.digitalcollections.cudami.client.identifiable.entity.agent;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.digitalcollections.cudami.client.CudamiBaseClient;
import de.digitalcollections.cudami.client.exceptions.HttpException;
import de.digitalcollections.model.identifiable.entity.agent.CorporateBody;
import de.digitalcollections.model.paging.PageRequest;
import de.digitalcollections.model.paging.PageResponse;
import de.digitalcollections.model.paging.SearchPageRequest;
import de.digitalcollections.model.paging.SearchPageResponse;
import java.net.http.HttpClient;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

public class CudamiCorporateBodiesClient extends CudamiBaseClient<CorporateBody> {

  public CudamiCorporateBodiesClient(HttpClient http, String serverUrl, ObjectMapper mapper) {
    super(http, serverUrl, CorporateBody.class, mapper);
  }

  public long count() throws HttpException {
    return Long.parseLong(doGetRequestForString("/v5/corporatebodies/count"));
  }

  public CorporateBody create() {
    return new CorporateBody();
  }

  public CorporateBody fetchAndSaveByGndId(String gndId) throws HttpException {
    return doPostRequestForObject(String.format("/v5/corporatebodies/gnd/%s", gndId));
  }

  // TODO: Implement with test
  @Deprecated(since = "5.0", forRemoval = true)
  /** @deprecated Please use {@link #find(SearchPageRequest)} instead */
  public PageResponse<CorporateBody> find(PageRequest pageRequest) throws HttpException {
    return doGetRequestForPagedObjectList("/v5/corporatebodies", pageRequest);
  }

  public SearchPageResponse<CorporateBody> find(SearchPageRequest searchPageRequest)
      throws HttpException {
    return doGetSearchRequestForPagedObjectList("/v5/corporatebodies", searchPageRequest);
  }

  public CorporateBody findOne(UUID uuid) throws HttpException {
    return doGetRequestForObject(String.format("/v5/corporatebodies/%s", uuid));
  }

  public CorporateBody findOne(UUID uuid, String locale) throws HttpException {
    return doGetRequestForObject(String.format("/v5/corporatebodies/%s?pLocale=%s", uuid, locale));
  }

  public CorporateBody findOneByIdentifier(String namespace, String id) throws HttpException {
    return doGetRequestForObject(
        String.format("/v5/corporatebodies/identifier/%s:%s.json", namespace, id));
  }

  public List<Locale> getLanguages() throws HttpException {
    return doGetRequestForObjectList("/v5/corporatebodies/languages", Locale.class);
  }

  public CorporateBody save(CorporateBody corporateBody) throws HttpException {
    return doPostRequestForObject("/v5/corporatebodies", corporateBody);
  }

  public CorporateBody update(UUID uuid, CorporateBody corporateBody) throws HttpException {
    return doPutRequestForObject(String.format("/v5/corporatebodies/%s", uuid), corporateBody);
  }
}
