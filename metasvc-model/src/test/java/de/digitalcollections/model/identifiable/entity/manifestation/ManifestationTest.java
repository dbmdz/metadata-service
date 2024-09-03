package de.digitalcollections.model.identifiable.entity.manifestation;

import static org.assertj.core.api.Assertions.assertThat;

import de.digitalcollections.model.identifiable.entity.agent.Person;
import de.digitalcollections.model.identifiable.entity.relation.EntityRelation;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("The Manifestation")
class ManifestationTest {

  @DisplayName("only dumps uuids of relations in toString to avoid recursion")
  @Test
  public void testToString() {
    Person author = Person.builder().uuid(UUID.randomUUID()).build();
    Manifestation manifestation = Manifestation.builder().uuid(UUID.randomUUID()).build();
    EntityRelation entityRelation =
        EntityRelation.builder().subject(author).predicate("foo").object(manifestation).build();
    manifestation.setRelations(List.of(entityRelation));

    String actual = manifestation.toString();

    String expected =
        "Manifestation{composition='null', dimensions='null', expressionTypes=[], relations=[EntityRelation{subject="
            + author.getUuid()
            + ", predicate='foo', object="
            + manifestation.getUuid()
            + "}], language=null, manifestationType=null, manufacturingType=null, mediaTypes=[], otherLanguages=[], parents=[], publicationInfo=null, distributionInfo=null, productionInfo=null, scale='null', subjects=[], "
            + "titles=[], version='null', work=null, customAttributes=null, navDate=null, refId=0, notes=[], description=null, identifiableObjectType=MANIFESTATION, identifiers=[], label=null, localizedUrlAliases=null, previewImage=null, previewImageRenderingHints=null, tags=[], type=ENTITY, created=null, lastModified=null, uuid="
            + manifestation.getUuid()
            + "}";
    assertThat(actual).isEqualTo(expected);
  }

  @DisplayName("can dump shortened Strings for weird manifestations")
  @Test
  public void shortenedStringsForWeirdManifestations() {
    List<EntityRelation> relations = new ArrayList<>();
    relations.add(null); // yes, that's an actual real-world example!
    String actual = Manifestation.dumpShortenedRelations(relations);
    assertThat(actual).isEqualTo("[]");
  }
}
