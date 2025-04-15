package io.github.dbmdz.metadata.server.backend.impl.jdbi.semantic;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

import de.digitalcollections.model.list.buckets.Bucket;
import de.digitalcollections.model.list.buckets.BucketsRequest;
import de.digitalcollections.model.list.buckets.BucketsResponse;
import de.digitalcollections.model.semantic.Headword;
import de.digitalcollections.model.validation.ValidationException;
import io.github.dbmdz.metadata.server.backend.api.repository.exceptions.RepositoryException;
import io.github.dbmdz.metadata.server.backend.impl.database.config.SpringConfigBackendTestDatabase;
import java.util.Locale;
import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
@SpringBootTest(
    webEnvironment = WebEnvironment.MOCK,
    classes = {HeadwordRepositoryImpl.class})
@ContextConfiguration(classes = SpringConfigBackendTestDatabase.class)
@Sql(scripts = "classpath:cleanup_database.sql")
@DisplayName("The Headword Repository")
class HeadwordRepositoryImplTest {

  @Autowired HeadwordRepositoryImpl repo;

  @Test
  @DisplayName("can save and retrieve")
  void saveHeadword() throws RepositoryException, ValidationException {
    Headword headword = new Headword("Eiffelturm", Locale.GERMAN);
    repo.save(headword);

    assertThat(headword.getUuid()).isNotNull();
    assertThat(headword.getCreated()).isNotNull();
    assertThat(headword.getLastModified()).isNotNull();
  }

  @Test
  @DisplayName("get buckets")
  void findBuckets() throws RepositoryException, ValidationException {
    // save 100 headwords
    for (int i = 1; i <= 100; i++) {
      Headword headword =
          new Headword("headword" + StringUtils.leftPad("" + i, 3, '0'), Locale.GERMAN);
      repo.save(headword);
    }

    // get 5 buckets
    BucketsRequest<Headword> bucketsRequest = new BucketsRequest<>(5);
    BucketsResponse<Headword> response = repo.find(bucketsRequest);

    assertThat(response.getContent().size() == 5);

    Bucket<Headword> firstBucket = response.getContent().get(0);
    assertEquals("headword001", firstBucket.getStartObject().getLabel());

    Bucket<Headword> lastBucket = response.getContent().get(4);
    assertEquals("headword100", lastBucket.getEndObject().getLabel());
  }
}
