package de.digitalcollections.cudami.client.identifiable.entity;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.digitalcollections.model.exception.TechnicalException;
import de.digitalcollections.model.identifiable.entity.Article;
import de.digitalcollections.model.paging.SearchPageRequest;
import de.digitalcollections.model.paging.SearchPageResponse;
import java.net.http.HttpClient;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

public class CudamiArticlesClient extends CudamiEntitiesClient<Article> {

  public CudamiArticlesClient(HttpClient http, String serverUrl, ObjectMapper mapper) {
    super(http, serverUrl, Article.class, mapper, "/v5/articles");
  }

  @Override
  public SearchPageResponse<Article> find(SearchPageRequest pageRequest) throws TechnicalException {
    return this.doGetSearchRequestForPagedObjectList(baseEndpoint, pageRequest);
  }

  public Article getByUuid(UUID uuid, Locale locale) throws TechnicalException {
    if (locale != null) {
      return doGetRequestForObject(String.format(baseEndpoint + "/%s?pLocale=%s", uuid, locale));
    } else {
      return doGetRequestForObject(String.format(baseEndpoint + "/%s", uuid));
    }
  }

  public List<Locale> getLanguages() throws TechnicalException {
    return this.doGetRequestForObjectList(baseEndpoint + "/languages", Locale.class);
  }
}
