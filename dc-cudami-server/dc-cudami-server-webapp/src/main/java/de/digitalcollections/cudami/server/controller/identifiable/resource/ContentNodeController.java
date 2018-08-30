package de.digitalcollections.cudami.server.controller.identifiable.resource;

import de.digitalcollections.cudami.server.business.api.service.exceptions.IdentifiableServiceException;
import de.digitalcollections.cudami.server.business.api.service.identifiable.resource.ContentNodeService;
import de.digitalcollections.model.api.identifiable.Identifiable;
import de.digitalcollections.model.api.identifiable.resource.ContentNode;
import de.digitalcollections.model.api.paging.PageRequest;
import de.digitalcollections.model.api.paging.PageResponse;
import de.digitalcollections.model.api.paging.Sorting;
import de.digitalcollections.model.api.paging.enums.Direction;
import de.digitalcollections.model.api.paging.enums.NullHandling;
import de.digitalcollections.model.api.paging.impl.OrderImpl;
import de.digitalcollections.model.api.paging.impl.PageRequestImpl;
import de.digitalcollections.model.api.paging.impl.SortingImpl;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.UUID;
import org.jsondoc.core.annotation.Api;
import org.jsondoc.core.annotation.ApiMethod;
import org.jsondoc.core.annotation.ApiPathParam;
import org.jsondoc.core.annotation.ApiQueryParam;
import org.jsondoc.core.annotation.ApiResponseObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Api(description = "The content node controller", name = "ContentNode controller")
public class ContentNodeController {

  @Autowired
  private ContentNodeService<ContentNode> service;

  @ApiMethod(description = "get all content nodes")
  @RequestMapping(value = "/v1/contentnodes",
          produces = "application/json", method = RequestMethod.GET)
  @ApiResponseObject
  public PageResponse<ContentNode> findAll(
          @RequestParam(name = "pageNumber", required = false, defaultValue = "0") int pageNumber,
          @RequestParam(name = "pageSize", required = false, defaultValue = "5") int pageSize,
          @RequestParam(name = "sortField", required = false, defaultValue = "uuid") String sortField,
          @RequestParam(name = "sortDirection", required = false, defaultValue = "ASC") Direction sortDirection,
          @RequestParam(name = "nullHandling", required = false, defaultValue = "NATIVE") NullHandling nullHandling
  ) {
    // FIXME add support for multiple sorting orders
    OrderImpl order = new OrderImpl(sortDirection, sortField, nullHandling);
    Sorting sorting = new SortingImpl(order);
    PageRequest pageRequest = new PageRequestImpl(pageNumber, pageSize, sorting);
    return service.find(pageRequest);
  }

  // Test-URL: http://localhost:9000/v1/contentnodes/599a120c-2dd5-11e8-b467-0ed5f89f718b
  @ApiMethod(description = "get a content node as JSON or XML, depending on extension or <tt>format</tt> request parameter or accept header")
  @RequestMapping(value = {"/v1/contentnodes/{uuid}"}, produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE}, method = RequestMethod.GET)
  @ApiResponseObject
  public ResponseEntity<ContentNode> getContentNode(
          @ApiPathParam(description = "UUID of the content node, e.g. <tt>599a120c-2dd5-11e8-b467-0ed5f89f718b</tt>") @PathVariable("uuid") UUID uuid,
          @ApiQueryParam(name = "pLocale", description = "Desired locale, e.g. <tt>de_DE</tt>. If unset, contents in all languages will be returned")
          @RequestParam(name = "pLocale", required = false) Locale pLocale
  ) throws IdentifiableServiceException {

    ContentNode contentNode;
    if (pLocale == null) {
      contentNode = service.get(uuid);
    } else {
      contentNode = service.get(uuid, pLocale);
    }
    return new ResponseEntity<>(contentNode, HttpStatus.OK);
  }

  @ApiMethod(description = "save a newly created top-level content node")
  @RequestMapping(value = "/v1/contenttrees/{parentContentTreeUuid}/contentnode", produces = "application/json", method = RequestMethod.POST)
  @ApiResponseObject
  public ContentNode saveWithParentContentTree(@PathVariable UUID parentContentTreeUuid, @RequestBody ContentNode contentNode, BindingResult errors) throws IdentifiableServiceException {
    return service.saveWithParentContentTree(contentNode, parentContentTreeUuid);
  }

  @ApiMethod(description = "save a newly created content node")
  @RequestMapping(value = "/v1/contentnodes/{parentContentNodeUuid}/contentnode", produces = "application/json", method = RequestMethod.POST)
  @ApiResponseObject
  public ContentNode saveWithParentContentNode(@PathVariable UUID parentContentNodeUuid, @RequestBody ContentNode contentNode, BindingResult errors) throws IdentifiableServiceException {
    return service.saveWithParentContentNode(contentNode, parentContentNodeUuid);
  }

  @ApiMethod(description = "update a content node")
  @RequestMapping(value = "/v1/contentnodes/{uuid}", produces = "application/json", method = RequestMethod.PUT)
  @ApiResponseObject
  public ContentNode update(@PathVariable UUID uuid, @RequestBody ContentNode contentNode, BindingResult errors) throws IdentifiableServiceException {
    assert Objects.equals(uuid, contentNode.getUuid());
    return service.update(contentNode);
  }

  @ApiMethod(description = "get count of content nodes")
  @RequestMapping(value = "/v1/contentnodes/count", produces = "application/json", method = RequestMethod.GET)
  @ApiResponseObject
  public long count() {
    return service.count();
  }

  @ApiMethod(description = "get child content nodes of content node")
  @RequestMapping(value = "/v1/contentnodes/{uuid}/children", produces = "application/json", method = RequestMethod.GET)
  @ApiResponseObject
  List<ContentNode> getChildren(@PathVariable UUID uuid) {
    return service.getChildren(uuid);
  }
  
  @RequestMapping(value = "/v1/contentnodes/{uuid}/identifiables", produces = "application/json", method = RequestMethod.GET)
  public List<Identifiable> getIdentifiables(@PathVariable UUID uuid) {
    return service.getIdentifiables(uuid);
  }
}
