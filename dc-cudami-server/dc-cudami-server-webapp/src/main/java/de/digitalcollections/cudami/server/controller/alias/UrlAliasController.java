package de.digitalcollections.cudami.server.controller.alias;

import de.digitalcollections.cudami.server.business.api.service.alias.UrlAliasService;
import de.digitalcollections.cudami.server.business.api.service.exceptions.CudamiServiceException;
import de.digitalcollections.cudami.server.controller.ControllerException;
import de.digitalcollections.model.alias.LocalizedUrlAliases;
import de.digitalcollections.model.alias.UrlAlias;
import de.digitalcollections.model.paging.Order;
import de.digitalcollections.model.paging.SearchPageRequest;
import de.digitalcollections.model.paging.SearchPageResponse;
import de.digitalcollections.model.paging.Sorting;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import java.util.UUID;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Tag(name = "UrlAlias controller")
public class UrlAliasController {

  private final UrlAliasService urlAliasService;

  public UrlAliasController(UrlAliasService urlAliasService) {
    this.urlAliasService = urlAliasService;
  }

  @Operation(summary = "Get an UrlAlias by uuid")
  @GetMapping(
      value = {
        "/v5/urlaliases/{uuid:[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}}"
      },
      produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<UrlAlias> get(
      @Parameter(
              description =
                  "UUID of the urlalias, e.g. <tt>599a120c-2dd5-11e8-b467-0ed5f89f718b</tt>")
          @PathVariable("uuid")
          UUID uuid)
      throws ControllerException {

    UrlAlias result;
    try {
      result = urlAliasService.findOne(uuid);
    } catch (CudamiServiceException e) {
      throw new ControllerException(e);
    }

    if (result == null) {
      return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    return new ResponseEntity<>(result, HttpStatus.OK);
  }

  @Operation(summary = "Delete an UrlAlias by uuid")
  @DeleteMapping(
      value = {
        "/v5/urlaliases/{uuid:[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}}"
      })
  public ResponseEntity<Void> delete(
      @Parameter(
              description =
                  "UUID of the urlalias, e.g. <tt>599a120c-2dd5-11e8-b467-0ed5f89f718b</tt>")
          @PathVariable("uuid")
          UUID uuid)
      throws ControllerException {
    boolean isDeleted;
    try {
      isDeleted = urlAliasService.delete(uuid);
    } catch (CudamiServiceException e) {
      throw new ControllerException(e);
    }

    if (!isDeleted) {
      return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    return new ResponseEntity<>(HttpStatus.NO_CONTENT);
  }

  @Operation(summary = "Create and persist an UrlAlias")
  @PostMapping(
      value = {"/v5/urlaliases"},
      produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<UrlAlias> create(@RequestBody UrlAlias urlAlias)
      throws ControllerException {

    if (urlAlias == null || urlAlias.getUuid() != null) {
      return new ResponseEntity("UUID must not be set", HttpStatus.UNPROCESSABLE_ENTITY);
    }

    UrlAlias result;
    try {
      result = urlAliasService.create(urlAlias);
    } catch (CudamiServiceException e) {
      throw new ControllerException(e);
    }

    return new ResponseEntity<>(result, HttpStatus.OK);
  }

  @Operation(summary = "update an UrlAlias")
  @PutMapping(
      value = {
        "/v5/urlaliases/{uuid:[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}}"
      },
      produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<UrlAlias> update(
      @Parameter(
              description =
                  "UUID of the urlalias, e.g. <tt>599a120c-2dd5-11e8-b467-0ed5f89f718b</tt>")
          @PathVariable("uuid")
          UUID uuid,
      @RequestBody UrlAlias urlAlias)
      throws ControllerException {

    if (uuid == null || urlAlias == null || !uuid.equals(urlAlias.getUuid())) {
      return new ResponseEntity(
          "UUID=" + uuid + " not set or does not match UUID of provided resource",
          HttpStatus.UNPROCESSABLE_ENTITY);
    }

    UrlAlias result;
    try {
      result = urlAliasService.update(urlAlias);
    } catch (CudamiServiceException e) {
      throw new ControllerException(e);
    }

    return new ResponseEntity<>(result, HttpStatus.OK);
  }

  @Operation(
      summary =
          "Find limited amounts of LocalizedUrlAliases. If the searchTerm is used, the slugs to be returned have to match the searchTerm")
  @GetMapping(
      value = {"/v5/urlaliases"},
      produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<SearchPageResponse<LocalizedUrlAliases>> find(
      @RequestParam(name = "pageNumber", required = false, defaultValue = "0") int pageNumber,
      @RequestParam(name = "pageSize", required = false, defaultValue = "5") int pageSize,
      @RequestParam(name = "sortBy", required = false) List<Order> sortBy,
      @RequestParam(name = "searchTerm", required = false) String searchTerm)
      throws ControllerException {
    SearchPageRequest pageRequest = new SearchPageRequest(searchTerm, pageNumber, pageSize);
    if (sortBy != null) {
      Sorting sorting = new Sorting(sortBy);
      pageRequest.setSorting(sorting);
    }

    SearchPageResponse<LocalizedUrlAliases> result;
    try {
      result = urlAliasService.find(pageRequest);
    } catch (CudamiServiceException e) {
      throw new ControllerException(e);
    }

    return new ResponseEntity<>(result, HttpStatus.OK);
  }

  @Operation(summary = "Get the main UrlAlias for a given website uuid and slug")
  @GetMapping(
      value = {
        "/v5/urlaliases/{website_uuid:[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}}/{slug}"
      },
      produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<LocalizedUrlAliases> getMainUrlAlias(
      @Parameter(
              description =
                  "UUID of the website, e.g. <tt>599a120c-2dd5-11e8-b467-0ed5f89f718b</tt>")
          @PathVariable("website_uuid")
          UUID websiteUuid,
      @Parameter(description = "the slug of the URL, e.g. <tt>imprint</tt>") @PathVariable("slug")
          String slug)
      throws ControllerException {
    LocalizedUrlAliases result;
    try {
      result = urlAliasService.findMainLink(websiteUuid, slug);
    } catch (CudamiServiceException e) {
      throw new ControllerException(e);
    }

    if (result == null) {
      return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    return new ResponseEntity<>(result, HttpStatus.OK);
  }
}
