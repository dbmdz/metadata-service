package io.github.dbmdz.metadata.server.backend.impl.jdbi.identifiable.web;

import static org.assertj.core.api.Assertions.assertThat;

import de.digitalcollections.model.identifiable.web.Webpage;
import de.digitalcollections.model.validation.ValidationException;
import io.github.dbmdz.metadata.server.backend.api.repository.exceptions.RepositoryException;
import io.github.dbmdz.metadata.server.backend.impl.jdbi.AbstractIdentifiableRepositoryImplTest;
import java.net.MalformedURLException;
import java.net.URL;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;

@SpringBootTest(webEnvironment = WebEnvironment.MOCK, classes = WebpageRepositoryImpl.class)
@DisplayName("Webpage repository")
class WebpageRepositoryImplTest
    extends AbstractIdentifiableRepositoryImplTest<WebpageRepositoryImpl> {
  @Autowired WebpageRepositoryImpl repo;

  @Test
  void testSaveAndRetrieve()
      throws MalformedURLException, RepositoryException, ValidationException {
    Webpage expected =
        Webpage.builder()
            .externalUrl(new URL("https://example.test"))
            .showExternalAsInternalUrl(true)
            .label("External URL test")
            .build();
    repo.save(expected);
    assertThat(expected.getUuid()).isNotNull();
    Webpage actual = repo.getByUuid(expected.getUuid());
    assertThat(actual).isEqualTo(expected);
  }
}
