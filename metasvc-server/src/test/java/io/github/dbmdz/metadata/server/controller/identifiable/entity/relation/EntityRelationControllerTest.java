package io.github.dbmdz.metadata.server.controller.identifiable.entity.relation;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

import de.digitalcollections.model.identifiable.entity.Entity;
import de.digitalcollections.model.identifiable.entity.relation.EntityRelation;
import io.github.dbmdz.metadata.server.business.api.service.identifiable.entity.relation.EntityToEntityRelationService;
import io.github.dbmdz.metadata.server.controller.BaseControllerTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;

@WebMvcTest(EntityRelationController.class)
@DisplayName("The EntityRelation controller")
class EntityRelationControllerTest extends BaseControllerTest {

  @MockBean private EntityToEntityRelationService entityRelationService;

  @DisplayName("can delete an EntityRelation")
  @ParameterizedTest
  @ValueSource(
      strings = {
        "/v6/entities/relations/8fe24c2a-ee3d-4f3f-bc4b-c78b6c132afd/is_realized_with/d2206b4c-910f-4dbe-b496-c0a5c0449ee7"
      })
  public void listOfRelatedEntities(String path) throws Exception {
    EntityRelation expectedEntityRelationToDelete =
        EntityRelation.builder()
            .subject(Entity.builder().uuid("8fe24c2a-ee3d-4f3f-bc4b-c78b6c132afd").build())
            .predicate("is_realized_with")
            .object(Entity.builder().uuid("d2206b4c-910f-4dbe-b496-c0a5c0449ee7").build())
            .build();
    when(entityRelationService.delete(eq(expectedEntityRelationToDelete))).thenReturn(1);

    testDeleteSuccessful(path);
  }
}
