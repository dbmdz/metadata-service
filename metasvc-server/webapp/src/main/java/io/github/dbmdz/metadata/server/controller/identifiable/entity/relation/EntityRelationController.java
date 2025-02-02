package io.github.dbmdz.metadata.server.controller.identifiable.entity.relation;

import de.digitalcollections.model.identifiable.entity.Entity;
import de.digitalcollections.model.identifiable.entity.relation.EntityRelation;
import de.digitalcollections.model.list.filtering.FilterCriterion;
import de.digitalcollections.model.list.filtering.Filtering;
import de.digitalcollections.model.list.paging.PageRequest;
import de.digitalcollections.model.list.paging.PageResponse;
import io.github.dbmdz.metadata.server.business.api.service.exceptions.ServiceException;
import io.github.dbmdz.metadata.server.business.api.service.identifiable.entity.relation.EntityToEntityRelationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Tag(name = "Entity relation controller")
public class EntityRelationController {

  private static final Logger LOGGER = LoggerFactory.getLogger(EntityRelationController.class);
  private final EntityToEntityRelationService service;

  public EntityRelationController(EntityToEntityRelationService entityRelationservice) {
    this.service = entityRelationservice;
  }

  @Operation(summary = "Get paged, sorted, filtered relations")
  @GetMapping(
      value = {"/v6/entities/relations"},
      produces = MediaType.APPLICATION_JSON_VALUE)
  public PageResponse<EntityRelation> findByPredicate(
      @RequestParam(name = "pageNumber", required = false, defaultValue = "0") int pageNumber,
      @RequestParam(name = "pageSize", required = false, defaultValue = "25") int pageSize,
      @RequestParam(name = "predicate", required = false) String predicate)
      throws ServiceException {
    PageRequest pageRequest = new PageRequest(pageNumber, pageSize);

    if (StringUtils.hasText(predicate)) {
      Filtering filtering =
          Filtering.builder()
              .add(
                  FilterCriterion.builder().withExpression("predicate").isEquals(predicate).build())
              .build();

      pageRequest.add(filtering);
    }
    return service.find(pageRequest);
  }

  @Operation(summary = "Connect a list of entity pairs with a predicate each")
  @PutMapping(
      value = {
        "/v5/entities/relations",
        "/v6/entities/relations",
        "/v3/entities/relations",
        "/latest/entities/relations"
      },
      produces = MediaType.APPLICATION_JSON_VALUE)
  List<EntityRelation> save(@RequestBody List<EntityRelation> entityRelations)
      throws ServiceException {
    service.save(entityRelations);
    return entityRelations;
  }

  @Operation(
      summary =
          "Connect a list of two entities, which share the same subject; obsolete; please use generic method without subjectuuid in path instead.")
  @PutMapping(
      value = {
        "/v6/entities/{subjectuuid}/relations",
        "/v5/entities/{subjectuuid}/relations",
        "/v3/entities/{subjectuuid}/relations",
        "/latest/entities/{subjectuuid}/relations"
      },
      produces = MediaType.APPLICATION_JSON_VALUE)
  @Deprecated
  /**
   * @deprecated use {@link #saveEntityRelations(List)} instead}
   */
  List<EntityRelation> saveEntityRelationsForSubject(
      @PathVariable("subjectuuid") UUID subjectUuid,
      @RequestBody List<EntityRelation> entityRelations)
      throws ServiceException {
    if (!subjectUuid.equals(entityRelations.get(0).getSubject().getUuid())) {
      throw new IllegalArgumentException(
          "Mismatching arguments. SubjectUuid must match the Uuid of the subject of the first item");
    }
    service.save(entityRelations);
    return entityRelations;
  }

  @Operation(summary = "Delete an entity relation")
  @DeleteMapping(
      value = "/v6/entities/relations/{subjectuuid}/{predicate}/{objectuuid}",
      produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<Void> deleteByUuidsAndPredicate(
      @Parameter(description = "UUID of the subject") @PathVariable("subjectuuid") UUID subjectUuid,
      @Parameter(description = "predicate") @PathVariable("predicate") String predicate,
      @Parameter(description = "UUID of the object") @PathVariable("objectuuid") UUID objectUuid) {
    EntityRelation example =
        EntityRelation.builder()
            .subject(Entity.builder().uuid(subjectUuid).build())
            .predicate(predicate)
            .object(Entity.builder().uuid(objectUuid).build())
            .build();

    boolean successful;
    try {
      successful = service.delete(example) == 1;
    } catch (ServiceException e) {
      LOGGER.error("Cannot delete EntityRelation " + example + ": " + e, e);
      return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
    }
    return successful
        ? new ResponseEntity<>(HttpStatus.NO_CONTENT)
        : new ResponseEntity<>(HttpStatus.NOT_FOUND);
  }
}
