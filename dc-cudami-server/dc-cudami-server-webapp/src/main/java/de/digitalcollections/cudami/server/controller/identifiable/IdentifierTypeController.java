package de.digitalcollections.cudami.server.controller.identifiable;

import de.digitalcollections.cudami.server.business.api.service.exceptions.ServiceException;
import de.digitalcollections.cudami.server.business.api.service.identifiable.IdentifierTypeService;
import de.digitalcollections.cudami.server.controller.ParameterHelper;
import de.digitalcollections.model.identifiable.IdentifierType;
import de.digitalcollections.model.list.paging.PageRequest;
import de.digitalcollections.model.list.paging.PageResponse;
import de.digitalcollections.model.list.sorting.Order;
import de.digitalcollections.model.list.sorting.Sorting;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Tag(name = "Identifier type controller")
public class IdentifierTypeController {

  private final IdentifierTypeService identifierTypeService;

  public IdentifierTypeController(IdentifierTypeService identifierTypeService) {
    this.identifierTypeService = identifierTypeService;
  }

  @Operation(summary = "Get all identifier types")
  @GetMapping(
      value = {"/v6/identifiertypes"},
      produces = MediaType.APPLICATION_JSON_VALUE)
  public PageResponse<IdentifierType> find(
      @RequestParam(name = "pageNumber", required = false, defaultValue = "0") int pageNumber,
      @RequestParam(name = "pageSize", required = false, defaultValue = "25") int pageSize,
      @RequestParam(name = "sortBy", required = false) List<Order> sortBy,
      @RequestParam(name = "searchTerm", required = false) String searchTerm) {
    PageRequest pageRequest = new PageRequest(searchTerm, pageNumber, pageSize);
    if (sortBy != null) {
      Sorting sorting = new Sorting(sortBy);
      pageRequest.setSorting(sorting);
    }
    return identifierTypeService.find(pageRequest);
  }

  @Operation(summary = "get identifier type by namespace (which is unique)")
  @GetMapping(
      value = {"/v6/identifiertypes/namespace/{namespace}"},
      produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<IdentifierType> getByNamespace(@PathVariable String namespace) {
    IdentifierType identifierType = identifierTypeService.getByNamespace(namespace);
    return new ResponseEntity<>(
        identifierType, identifierType != null ? HttpStatus.OK : HttpStatus.NOT_FOUND);
  }

  @Operation(summary = "get identifier type by uuid")
  @GetMapping(
      value = {
        "/v6/identifiertypes/{uuid:" + ParameterHelper.UUID_PATTERN + "}",
        "/v5/identifiertypes/{uuid:" + ParameterHelper.UUID_PATTERN + "}",
        "/v2/identifiertypes/{uuid:" + ParameterHelper.UUID_PATTERN + "}",
        "/latest/identifiertypes/{uuid:" + ParameterHelper.UUID_PATTERN + "}"
      },
      produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<IdentifierType> getByUuid(@PathVariable UUID uuid) {
    IdentifierType identifierType = identifierTypeService.getByUuid(uuid);
    return new ResponseEntity<>(
        identifierType, identifierType != null ? HttpStatus.OK : HttpStatus.NOT_FOUND);
  }

  @Operation(summary = "save a newly created identifier type")
  @PostMapping(
      value = {
        "/v6/identifiertypes",
        "/v5/identifiertypes",
        "/v2/identifiertypes",
        "/latest/identifiertypes"
      },
      produces = MediaType.APPLICATION_JSON_VALUE)
  public IdentifierType save(@RequestBody IdentifierType identifierType, BindingResult errors)
      throws ServiceException {
    identifierTypeService.save(identifierType);
    return identifierType;
  }

  @Operation(summary = "update an identifier type")
  @PutMapping(
      value = {
        "/v6/identifiertypes/{uuid:" + ParameterHelper.UUID_PATTERN + "}",
        "/v5/identifiertypes/{uuid:" + ParameterHelper.UUID_PATTERN + "}",
        "/v2/identifiertypes/{uuid:" + ParameterHelper.UUID_PATTERN + "}",
        "/latest/identifiertypes/{uuid:" + ParameterHelper.UUID_PATTERN + "}"
      },
      produces = MediaType.APPLICATION_JSON_VALUE)
  public IdentifierType update(
      @PathVariable UUID uuid, @RequestBody IdentifierType identifierType, BindingResult errors)
      throws ServiceException {
    assert Objects.equals(uuid, identifierType.getUuid());
    identifierTypeService.update(identifierType);
    return identifierType;
  }
}
