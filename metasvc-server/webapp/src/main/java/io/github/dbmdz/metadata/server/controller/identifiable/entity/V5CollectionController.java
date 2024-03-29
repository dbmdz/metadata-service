package io.github.dbmdz.metadata.server.controller.identifiable.entity;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import de.digitalcollections.model.identifiable.entity.Collection;
import de.digitalcollections.model.identifiable.entity.digitalobject.DigitalObject;
import de.digitalcollections.model.list.paging.PageRequest;
import de.digitalcollections.model.list.paging.PageResponse;
import de.digitalcollections.model.list.sorting.Order;
import de.digitalcollections.model.list.sorting.Sorting;
import io.github.dbmdz.metadata.server.business.api.service.LocaleService;
import io.github.dbmdz.metadata.server.business.api.service.exceptions.ServiceException;
import io.github.dbmdz.metadata.server.business.api.service.identifiable.entity.CollectionService;
import io.github.dbmdz.metadata.server.controller.CudamiControllerException;
import io.github.dbmdz.metadata.server.controller.legacy.V5MigrationHelper;
import io.github.dbmdz.metadata.server.controller.legacy.model.LegacyPageRequest;
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
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Tag(name = "Collection controller")
public class V5CollectionController {

  private static final Logger LOGGER = LoggerFactory.getLogger(V5CollectionController.class);

  private final CollectionService collectionService;
  private final LocaleService localeService;

  private final ObjectMapper objectMapper;

  public V5CollectionController(
      CollectionService collectionService, LocaleService localeService, ObjectMapper objectMapper) {
    this.collectionService = collectionService;
    this.localeService = localeService;
    this.objectMapper = objectMapper;
  }

  @Operation(
      summary =
          "Find limited amount of (active or all) collections containing searchTerm in label or description")
  @GetMapping(
      value = {"/v5/collections/search", "/v5/collections"},
      produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<String> find(
      @RequestParam(name = "pageNumber", required = false, defaultValue = "0") int pageNumber,
      @RequestParam(name = "pageSize", required = false, defaultValue = "5") int pageSize,
      @RequestParam(name = "sortBy", required = false) List<Order> sortBy,
      @RequestParam(name = "searchTerm", required = false) String searchTerm,
      @RequestParam(name = "active", required = false) String active)
      throws CudamiControllerException, ServiceException {
    PageRequest pageRequest = new LegacyPageRequest(searchTerm, pageNumber, pageSize);
    if (sortBy != null) {
      Sorting sorting = new Sorting(V5MigrationHelper.migrate(sortBy));
      pageRequest.setSorting(sorting);
    }
    PageResponse<Collection> pageResponse;
    if (active != null) {
      pageResponse = collectionService.findActive(pageRequest);
    } else {
      pageResponse = collectionService.find(pageRequest);
    }

    try {
      String result = V5MigrationHelper.migrate(pageResponse, objectMapper);
      return new ResponseEntity<>(result, HttpStatus.OK);
    } catch (JsonProcessingException e) {
      throw new CudamiControllerException(e);
    }
  }

  @Operation(summary = "Get paged digital objects of a collection")
  @GetMapping(
      value = {"/v5/collections/{uuid}/digitalobjects"},
      produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<String> findDigitalObjects(
      @Parameter(example = "", description = "UUID of the collection") @PathVariable("uuid")
          UUID collectionUuid,
      @RequestParam(name = "pageNumber", required = false, defaultValue = "0") int pageNumber,
      @RequestParam(name = "pageSize", required = false, defaultValue = "25") int pageSize,
      @RequestParam(name = "searchTerm", required = false) String searchTerm)
      throws CudamiControllerException, ServiceException {
    PageRequest searchPageRequest = new LegacyPageRequest(searchTerm, pageNumber, pageSize);

    Collection collection = new Collection();
    collection.setUuid(collectionUuid);
    PageResponse<DigitalObject> pageResponse =
        collectionService.findDigitalObjects(collection, searchPageRequest);

    try {
      String result = V5MigrationHelper.migrate(pageResponse, objectMapper);
      return new ResponseEntity<>(result, HttpStatus.OK);
    } catch (JsonProcessingException e) {
      throw new CudamiControllerException(e);
    }
  }

  @Operation(summary = "Get (active or all) paged subcollections of a collection")
  @GetMapping(
      value = {"/v5/collections/{uuid}/subcollections"},
      produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<String> findSubcollections(
      @Parameter(example = "", description = "UUID of the collection") @PathVariable("uuid")
          UUID collectionUuid,
      @RequestParam(name = "pageNumber", required = false, defaultValue = "0") int pageNumber,
      @RequestParam(name = "pageSize", required = false, defaultValue = "25") int pageSize,
      @RequestParam(name = "active", required = false) String active,
      @RequestParam(name = "sortBy", required = false) List<Order> sortBy,
      @RequestParam(name = "searchTerm", required = false) String searchTerm)
      throws CudamiControllerException, ServiceException {
    PageRequest searchPageRequest = new LegacyPageRequest(searchTerm, pageNumber, pageSize);
    if (sortBy != null) {
      Sorting sorting = new Sorting(V5MigrationHelper.migrate(sortBy));
      searchPageRequest.setSorting(sorting);
    }
    PageResponse<Collection> pageResponse;
    if (active != null) {
      pageResponse =
          collectionService.findActiveChildren(
              Collection.builder().uuid(collectionUuid).build(), searchPageRequest);
    } else {
      pageResponse =
          collectionService.findChildren(
              Collection.builder().uuid(collectionUuid).build(), searchPageRequest);
    }

    try {
      String result = V5MigrationHelper.migrate(pageResponse, objectMapper);
      return new ResponseEntity<>(result, HttpStatus.OK);
    } catch (JsonProcessingException e) {
      throw new CudamiControllerException(e);
    }
  }

  @Operation(summary = "Get all top collections")
  @GetMapping(
      value = {"/v5/collections/top", "/v2/collections/top", "/latest/collections/top"},
      produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<String> findTopCollections(
      @RequestParam(name = "pageNumber", required = false, defaultValue = "0") int pageNumber,
      @RequestParam(name = "pageSize", required = false, defaultValue = "5") int pageSize,
      @RequestParam(name = "sortBy", required = false) List<Order> sortBy,
      @RequestParam(name = "searchTerm", required = false) String searchTerm)
      throws CudamiControllerException, ServiceException {
    PageRequest pageRequest = new LegacyPageRequest(searchTerm, pageNumber, pageSize);
    if (sortBy != null) {
      Sorting sorting = new Sorting(V5MigrationHelper.migrate(sortBy));
      pageRequest.setSorting(sorting);
    }
    PageResponse<Collection> pageResponse = collectionService.findRootNodes(pageRequest);
    try {
      String result = V5MigrationHelper.migrate(pageResponse, objectMapper);
      return new ResponseEntity<>(result, HttpStatus.OK);
    } catch (JsonProcessingException e) {
      throw new CudamiControllerException(e);
    }
  }
}
