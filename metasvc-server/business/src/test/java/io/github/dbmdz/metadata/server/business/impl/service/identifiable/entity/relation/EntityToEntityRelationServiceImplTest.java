package io.github.dbmdz.metadata.server.business.impl.service.identifiable.entity.relation;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

import de.digitalcollections.model.identifiable.entity.agent.Person;
import de.digitalcollections.model.identifiable.entity.manifestation.Manifestation;
import de.digitalcollections.model.identifiable.entity.relation.EntityRelation;
import io.github.dbmdz.metadata.server.backend.api.repository.identifiable.entity.relation.EntityToEntityRelationRepository;
import io.github.dbmdz.metadata.server.business.api.service.exceptions.ServiceException;
import io.github.dbmdz.metadata.server.business.api.service.identifiable.entity.relation.EntityToEntityRelationService;
import io.github.dbmdz.metadata.server.business.impl.service.AbstractServiceImplTest;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("The EntityRelation service")
class EntityToEntityRelationServiceImplTest extends AbstractServiceImplTest {

  private EntityToEntityRelationService entityRelationService;
  private EntityToEntityRelationRepository entityRelationRepository;

  @BeforeEach
  public void beforeEach() throws Exception {
    super.beforeEach();
    entityRelationRepository = mock(EntityToEntityRelationRepository.class);
    entityRelationService = new EntityToEntityRelationServiceImpl(entityRelationRepository);
  }

  @DisplayName(
      "persisting entity relations will fill the objects of the relations with the UUID of the manifestations only")
  @Test
  public void relationObjectsOnlyWithUuid() throws ServiceException {
    UUID uuid = UUID.randomUUID();
    Manifestation manifestation = Manifestation.builder().uuid(uuid).build();
    EntityRelation relation =
        EntityRelation.builder()
            .subject(Person.builder().label("Karl Ranseier").randomUuid().build())
            .predicate("is_least_successful_author_of")
            .object(manifestation)
            .build();
    manifestation.setRelations(List.of(relation));

    List<EntityRelation> relations = manifestation.getRelations();
    entityRelationService.setEntityRelations(manifestation, relations, true);
    manifestation.setRelations(relations);

    Manifestation manifestionWithUUIDOnly = Manifestation.builder().uuid(uuid).build();
    assertThat(
            manifestation.getRelations().stream()
                .map(EntityRelation::getObject)
                .collect(Collectors.toList()))
        .containsExactly(manifestionWithUUIDOnly);
  }
}
