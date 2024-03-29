package io.github.dbmdz.metadata.server.controller.identifiable.entity.relation;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import de.digitalcollections.model.identifiable.entity.relation.EntityRelation;
import de.digitalcollections.model.list.filtering.FilterCriterion;
import de.digitalcollections.model.list.filtering.Filtering;
import de.digitalcollections.model.list.paging.PageRequest;
import de.digitalcollections.model.list.paging.PageResponse;
import io.github.dbmdz.metadata.server.business.api.service.exceptions.ServiceException;
import io.github.dbmdz.metadata.server.business.api.service.identifiable.entity.relation.EntityToEntityRelationService;
import io.github.dbmdz.metadata.server.controller.CudamiControllerException;
import io.github.dbmdz.metadata.server.controller.legacy.V5MigrationHelper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Tag(name = "V5 Entity relation controller")
public class V5EntityRelationController {

  private final EntityToEntityRelationService entityRelationService;
  private final ObjectMapper objectMapper;

  public V5EntityRelationController(
      EntityToEntityRelationService entityRelationService, ObjectMapper objectMapper) {
    this.entityRelationService = entityRelationService;
    this.objectMapper = objectMapper;
  }

  @Operation(summary = "Get paged, sorted, filtered relations")
  @GetMapping(
      value = {"/v5/entities/relations"},
      produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<String> findByPredicate(
      @RequestParam(name = "pageNumber", required = false, defaultValue = "0") int pageNumber,
      @RequestParam(name = "pageSize", required = false, defaultValue = "25") int pageSize,
      @RequestParam(name = "predicate", required = false) String predicate)
      throws CudamiControllerException, ServiceException {
    PageRequest pageRequest = new PageRequest(pageNumber, pageSize);

    if (StringUtils.hasText(predicate)) {
      Filtering filtering =
          Filtering.builder()
              .add(
                  FilterCriterion.builder().withExpression("predicate").isEquals(predicate).build())
              .build();

      pageRequest.add(filtering);
    }
    PageResponse<EntityRelation> pageResponse = entityRelationService.find(pageRequest);

    try {
      String result = V5MigrationHelper.migrate(pageResponse, objectMapper);
      return new ResponseEntity<>(result, HttpStatus.OK);
    } catch (JsonProcessingException e) {
      throw new CudamiControllerException(e);
    }
  }
}
