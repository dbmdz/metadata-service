package de.digitalcollections.cudami.client.relation;

import de.digitalcollections.cudami.client.BaseCudamiRestClientTest;
import de.digitalcollections.model.relation.Predicate;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("The PredicatesClient")
class CudamiPredicatesClientTest
    extends BaseCudamiRestClientTest<Predicate, CudamiPredicatesClient> {

  @Test
  @DisplayName("can update without uuid (old behaviour)")
  public void testUpdateWithoutUuid() throws Exception {
    Predicate predicate = Predicate.builder().value("foo").build();
    client.update(predicate);

    verifyHttpRequestByMethodRelativeUrlAndRequestBody("put", "/foo", predicate);
  }

  @Test
  @DisplayName("can update with uuid")
  public void testUpdateWithUuid() throws Exception {
    UUID uuid = UUID.randomUUID();
    Predicate predicate = Predicate.builder().value("foo").uuid(uuid).build();
    client.update(uuid, predicate);

    verifyHttpRequestByMethodRelativeUrlAndRequestBody("put", "/" + uuid, predicate);
  }

  @Test
  @DisplayName("can find by PageRequest")
  @Override
  public void testFindWithPageRequest2() throws Exception {
    client.find(buildExamplePageRequest());
    verifyHttpRequestByMethodAndRelativeURL(
        "get",
        "?pageNumber=1&pageSize=2&sortBy=sortable.desc.nullsfirst.ignorecase&filtering=foo:eq:bar;gnarf:eq:krchch");
  }
}
