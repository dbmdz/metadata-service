package de.digitalcollections.cudami.server.backend.impl.jdbi.identifiable.entity;

import static org.assertj.core.api.Assertions.assertThat;

import de.digitalcollections.cudami.server.backend.api.repository.exceptions.RepositoryException;
import de.digitalcollections.cudami.server.backend.impl.jdbi.AbstractIdentifiableRepositoryImplTest;
import de.digitalcollections.cudami.server.backend.impl.jdbi.identifiable.web.WebpageRepositoryImpl;
import de.digitalcollections.model.identifiable.entity.Website;
import de.digitalcollections.model.identifiable.web.Webpage;
import de.digitalcollections.model.text.LocalizedStructuredContent;
import de.digitalcollections.model.text.StructuredContent;
import de.digitalcollections.model.text.contentblock.Text;
import java.util.List;
import java.util.Locale;
import java.util.function.Function;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;

@SpringBootTest(
    webEnvironment = WebEnvironment.MOCK,
    classes = {WebsiteRepositoryImpl.class})
@DisplayName("The Website Repository")
class WebsiteRepositoryImplTest
    extends AbstractIdentifiableRepositoryImplTest<WebsiteRepositoryImpl> {
  @Autowired WebpageRepositoryImpl webpageRepository;

  @BeforeEach
  public void beforeEach() {
    repo = new WebsiteRepositoryImpl(jdbi, webpageRepository, cudamiConfig);
  }

  @Test
  @DisplayName("can save a website")
  void saveWebsite() throws RepositoryException {
    Webpage webpage = Webpage.builder().label("webpage").build();
    webpageRepository.save(webpage);
    Website website =
        Website.builder()
            .label(Locale.GERMAN, "Digitale Sammlungen")
            .url("https://www.digitale-sammlungen.de")
            .registrationDate("2022-05-04")
            .rootPages(List.of(webpage))
            .build();

    saveAndAssertTimestampsAndEqualityToSaveable(website);
  }

  @Test
  @DisplayName("can update a website")
  void testUpdate() throws RepositoryException {
    Webpage webpage1 = Webpage.builder().label("webpage1").build();
    webpageRepository.save(webpage1);
    Webpage webpage2 = Webpage.builder().label("webpage2").build();
    webpageRepository.save(webpage2);

    Website website =
        Website.builder()
            .label(Locale.GERMAN, "Digitale Sammlungen")
            .url("https://www.digitale-sammlungen.de")
            .registrationDate("2022-05-04")
            .rootPages(List.of(webpage1))
            .build();
    repo.save(website);

    website.setRootPages(List.of(webpage1, webpage2));

    Website beforeUpdate = createDeepCopy(website);
    updateAndAssertUpdatedLastModifiedTimestamp(website);
    assertInDatabaseIsEqualToUpdateable(website, beforeUpdate, Function.identity());
  }

  @Test
  @DisplayName("save a website with notes")
  void saveWebsiteWithNotes() throws RepositoryException {
    var noteContent1 = new StructuredContent();
    noteContent1.addContentBlock(new Text("eine Bemerkung"));
    var note1 = new LocalizedStructuredContent();
    note1.put(Locale.GERMAN, noteContent1);

    var noteContent2 = new StructuredContent();
    noteContent2.addContentBlock(new Text("zweite Bemerkung"));
    var note2 = new LocalizedStructuredContent();
    note2.put(Locale.GERMAN, noteContent2);
    Website website =
        Website.builder()
            .label(Locale.GERMAN, "digiPress")
            .url("https://digipress.digitale-sammlungen.de")
            .registrationDate("2022-05-04")
            .rootPages(List.of(Webpage.builder().build()))
            .note(note1)
            .note(note2)
            .build();

    repo.save(website);

    assertThat(website.getNotes()).isEqualTo(website.getNotes());
    assertThat(website.getUuid()).isNotNull();
  }
}
