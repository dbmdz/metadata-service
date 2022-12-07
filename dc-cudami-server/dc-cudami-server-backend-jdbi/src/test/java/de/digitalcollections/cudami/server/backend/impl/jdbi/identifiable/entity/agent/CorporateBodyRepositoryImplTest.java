package de.digitalcollections.cudami.server.backend.impl.jdbi.identifiable.entity.agent;

import static org.assertj.core.api.Assertions.assertThat;

import de.digitalcollections.cudami.server.backend.impl.jdbi.AbstractIdentifiableRepositoryImplTest;
import de.digitalcollections.model.identifiable.entity.agent.CorporateBody;
import de.digitalcollections.model.list.filtering.FilterCriterion;
import de.digitalcollections.model.list.filtering.FilterOperation;
import de.digitalcollections.model.list.filtering.Filtering;
import de.digitalcollections.model.list.paging.PageRequest;
import de.digitalcollections.model.list.paging.PageResponse;
import de.digitalcollections.model.text.LocalizedText;
import java.util.Locale;
import java.util.Set;
import java.util.function.Function;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;

@SpringBootTest(
    webEnvironment = WebEnvironment.MOCK,
    classes = {AgentRepositoryImpl.class})
@DisplayName("The CorporateBody Repository")
class CorporateBodyRepositoryImplTest
    extends AbstractIdentifiableRepositoryImplTest<CorporateBodyRepositoryImpl> {

  @BeforeEach
  public void beforeEach() {
    repo = new CorporateBodyRepositoryImpl(jdbi, cudamiConfig);
  }

  @Test
  @DisplayName("can save a CorporateBody")
  public void saveCorporateBody() {
    CorporateBody creator =
        CorporateBody.builder()
            .label(Locale.GERMAN, "Körperschaft")
            .label(Locale.ENGLISH, "Corporate Body")
            .build();

    saveAndAssertTimestampsAndEqualityToSaveable(creator);
  }

  @Test
  @DisplayName("can update a CorporateBody")
  void testUpdate() {
    CorporateBody corporateBody =
        CorporateBody.builder().label("Test").addName("some name").build();
    repo.save(corporateBody);

    corporateBody.setLabel("changed test");
    LocalizedText name =
        LocalizedText.builder()
            .text(Locale.ENGLISH, "some english name")
            .text(LOCALE_ZH_HANI, "難經辨眞")
            .build();
    corporateBody.setName(name);
    corporateBody.setNameLocalesOfOriginalScripts(Set.of(Locale.ENGLISH, LOCALE_ZH_HANI));

    CorporateBody beforeUpdate = createDeepCopy(corporateBody);
    updateAndAssertUpdatedLastModifiedTimestamp(corporateBody);
    assertInDatabaseIsEqualToUpdateable(corporateBody, beforeUpdate, Function.identity());
  }

  @Test
  @DisplayName("can retrieve a CorporateBody")
  public void saveAndRetrieveCorporateBody() {
    CorporateBody creator =
        CorporateBody.builder()
            .label(Locale.GERMAN, "Körperschaft")
            .label(Locale.ENGLISH, "Corporate Body")
            .build();

    CorporateBodyRepositoryImpl corporateBodyRepository =
        new CorporateBodyRepositoryImpl(jdbi, cudamiConfig);
    CorporateBody persisted = corporateBodyRepository.save(creator);

    CorporateBody actual = corporateBodyRepository.getByUuid(persisted.getUuid());
    assertThat(actual).isEqualTo(persisted);
  }

  @Test
  @DisplayName("can retrieve a CorporateBody by name")
  public void findCorporateBodyByName() {
    CorporateBody creator =
        CorporateBody.builder()
            .label(Locale.GERMAN, "Körperschaft")
            .label(Locale.ENGLISH, "Corporate Body")
            .addName(Locale.GERMAN, "Ein Unternehmen (Körperschaft)")
            .addName(Locale.ENGLISH, "A corporation doing funny stuff")
            .build();

    CorporateBodyRepositoryImpl corporateBodyRepository =
        new CorporateBodyRepositoryImpl(jdbi, cudamiConfig);
    CorporateBody persisted = corporateBodyRepository.save(creator);
    PageResponse<CorporateBody> byName =
        corporateBodyRepository.find(
            PageRequest.builder()
                .pageNumber(0)
                .pageSize(10)
                .filtering(
                    Filtering.builder()
                        .add(
                            new FilterCriterion<>(
                                "name", FilterOperation.CONTAINS, "Unternehmen Körperschaft"))
                        .build())
                .build());
    PageResponse<CorporateBody> byGermanName =
        corporateBodyRepository.find(
            PageRequest.builder()
                .pageNumber(0)
                .pageSize(10)
                .filtering(
                    Filtering.builder()
                        .add(
                            new FilterCriterion<>(
                                "name.de", FilterOperation.CONTAINS, "Unternehmen Körperschaft"))
                        .build())
                .build());
    PageResponse<CorporateBody> byEnglishName =
        corporateBodyRepository.find(
            PageRequest.builder()
                .pageNumber(0)
                .pageSize(10)
                .filtering(
                    Filtering.builder()
                        .add(
                            new FilterCriterion<>(
                                "name.en", FilterOperation.CONTAINS, "corporation funny stuff"))
                        .build())
                .build());
    PageResponse<CorporateBody> byEnglishNameEquals =
        corporateBodyRepository.find(
            PageRequest.builder()
                .pageNumber(0)
                .pageSize(10)
                .filtering(
                    Filtering.builder()
                        .add(
                            new FilterCriterion<>(
                                "name.en",
                                FilterOperation.EQUALS,
                                "a corporation doing funny stuff"))
                        .build())
                .build());
    PageResponse<CorporateBody> noMatch =
        corporateBodyRepository.find(
            PageRequest.builder()
                .pageNumber(0)
                .pageSize(10)
                .filtering(
                    Filtering.builder()
                        .add(
                            new FilterCriterion<>(
                                "name", FilterOperation.CONTAINS, "should not match"))
                        .build())
                .build());

    assertThat(byName.getNumberOfElements()).isEqualTo(1);
    assertThat(byName.getContent().get(0)).isEqualTo(persisted);
    assertThat(byGermanName.getNumberOfElements()).isEqualTo(1);
    assertThat(byGermanName.getContent().get(0)).isEqualTo(persisted);
    assertThat(byEnglishName.getNumberOfElements()).isEqualTo(1);
    assertThat(byEnglishName.getContent().get(0)).isEqualTo(persisted);
    assertThat(byEnglishNameEquals.getNumberOfElements()).isEqualTo(1);
    assertThat(byEnglishNameEquals.getContent().get(0)).isEqualTo(persisted);
    assertThat(noMatch.getNumberOfElements()).isEqualTo(0);
    assertThat(noMatch.getContent()).isEmpty();
  }
}
