package de.digitalcollections.cudami.server.controller.identifiable.web;

import de.digitalcollections.cudami.server.business.api.service.LocaleService;
import de.digitalcollections.cudami.server.business.api.service.exceptions.ServiceException;
import de.digitalcollections.cudami.server.business.api.service.exceptions.ValidationException;
import de.digitalcollections.cudami.server.business.api.service.identifiable.IdentifiableService;
import de.digitalcollections.cudami.server.business.api.service.identifiable.web.WebpageService;
import de.digitalcollections.cudami.server.controller.ParameterHelper;
import de.digitalcollections.cudami.server.controller.identifiable.AbstractIdentifiableController;
import de.digitalcollections.model.identifiable.entity.Website;
import de.digitalcollections.model.identifiable.resource.FileResource;
import de.digitalcollections.model.identifiable.web.Webpage;
import de.digitalcollections.model.list.filtering.FilterCriterion;
import de.digitalcollections.model.list.paging.PageRequest;
import de.digitalcollections.model.list.paging.PageResponse;
import de.digitalcollections.model.list.sorting.Order;
import de.digitalcollections.model.view.BreadcrumbNavigation;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import java.util.Locale;
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
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Tag(name = "Webpage controller")
public class WebpageController extends AbstractIdentifiableController<Webpage> {

  private final LocaleService localeService;
  private final WebpageService service;

  public WebpageController(LocaleService localeService, WebpageService webpageService) {
    this.localeService = localeService;
    this.service = webpageService;
  }

  @Operation(summary = "Add file resource related to webpage")
  @PostMapping(
      value = {
        "/v6/webpages/{uuid:"
            + ParameterHelper.UUID_PATTERN
            + "}/related/fileresources/{fileResourceUuid}",
        "/v5/webpages/{uuid:"
            + ParameterHelper.UUID_PATTERN
            + "}/related/fileresources/{fileResourceUuid}",
        "/v2/webpages/{uuid:"
            + ParameterHelper.UUID_PATTERN
            + "}/related/fileresources/{fileResourceUuid}",
        "/latest/webpages/{uuid:"
            + ParameterHelper.UUID_PATTERN
            + "}/related/fileresources/{fileResourceUuid}"
      })
  @ResponseStatus(value = HttpStatus.OK)
  public void addRelatedFileResource(@PathVariable UUID uuid, @PathVariable UUID fileResourceUuid)
      throws ServiceException {
    service.addRelatedFileresource(
        Webpage.builder().uuid(uuid).build(),
        FileResource.builder().uuid(fileResourceUuid).build());
  }

  @Operation(summary = "Get all webpages as (paged, sorted, filtered) list")
  @GetMapping(
      value = {"/v6/webpages"},
      produces = MediaType.APPLICATION_JSON_VALUE)
  public PageResponse<Webpage> find(
      @RequestParam(name = "pageNumber", required = false, defaultValue = "0") int pageNumber,
      @RequestParam(name = "pageSize", required = false, defaultValue = "25") int pageSize,
      @RequestParam(name = "sortBy", required = false) List<Order> sortBy,
      @RequestParam(name = "filter", required = false) List<FilterCriterion> filterCriteria)
      throws ServiceException {
    PageRequest pageRequest =
        createPageRequest(Webpage.class, pageNumber, pageSize, sortBy, filterCriteria);
    return service.find(pageRequest);
  }

  @Operation(summary = "Get all (active) children of a webpage as (paged, sorted, filtered) list")
  @GetMapping(
      value = {"/v6/webpages/{uuid:" + ParameterHelper.UUID_PATTERN + "}/children"},
      produces = MediaType.APPLICATION_JSON_VALUE)
  public PageResponse<Webpage> findSubpages(
      @Parameter(
              example = "",
              description =
                  "UUID of the parent webpage, e.g. <tt>599a120c-2dd5-11e8-b467-0ed5f89f718b</tt>")
          @PathVariable("uuid")
          UUID uuid,
      @RequestParam(name = "pageNumber", required = false, defaultValue = "0") int pageNumber,
      @RequestParam(name = "pageSize", required = false, defaultValue = "25") int pageSize,
      @RequestParam(name = "sortBy", required = false) List<Order> sortBy,
      @RequestParam(name = "filter", required = false) List<FilterCriterion> filterCriteria,
      @RequestParam(name = "active", required = false) String active)
      throws ServiceException {
    PageRequest pageRequest =
        createPageRequest(Webpage.class, pageNumber, pageSize, sortBy, filterCriteria);
    if (active != null) {
      return service.findActiveChildren(Webpage.builder().uuid(uuid).build(), pageRequest);
    }
    return service.findChildren(Webpage.builder().uuid(uuid).build(), pageRequest);
  }

  @Operation(summary = "Get the breadcrumb for a webpage")
  @GetMapping(
      value = {
        "/v6/webpages/{uuid:" + ParameterHelper.UUID_PATTERN + "}/breadcrumb",
        "/v5/webpages/{uuid:" + ParameterHelper.UUID_PATTERN + "}/breadcrumb",
        "/v3/webpages/{uuid:" + ParameterHelper.UUID_PATTERN + "}/breadcrumb",
        "/latest/webpages/{uuid:" + ParameterHelper.UUID_PATTERN + "}/breadcrumb"
      },
      produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<BreadcrumbNavigation> getBreadcrumbNavigation(
      @Parameter(
              example = "",
              description =
                  "UUID of the webpage, e.g. <tt>6119d8e9-9c92-4091-8dcb-bc4053385406</tt>")
          @PathVariable("uuid")
          UUID uuid,
      @Parameter(
              name = "pLocale",
              description =
                  "Desired locale, e.g. <tt>de_DE</tt>. If unset, contents in all languages will be returned")
          @RequestParam(name = "pLocale", required = false)
          Locale pLocale)
      throws ServiceException {

    BreadcrumbNavigation breadcrumbNavigation;

    if (pLocale == null) {
      breadcrumbNavigation = service.getBreadcrumbNavigation(Webpage.builder().uuid(uuid).build());
    } else {
      breadcrumbNavigation =
          service.getBreadcrumbNavigation(
              Webpage.builder().uuid(uuid).build(), pLocale, localeService.getDefaultLocale());
    }

    if (breadcrumbNavigation == null || breadcrumbNavigation.getNavigationItems().isEmpty()) {
      return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    return new ResponseEntity<>(breadcrumbNavigation, HttpStatus.OK);
  }

  @Operation(summary = "Get a webpage by uuid")
  @GetMapping(
      value = {
        "/v6/webpages/{uuid:" + ParameterHelper.UUID_PATTERN + "}",
        "/v5/webpages/{uuid:" + ParameterHelper.UUID_PATTERN + "}",
        "/latest/webpages/{uuid:" + ParameterHelper.UUID_PATTERN + "}"
      },
      produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<Webpage> getByUuid(
      @Parameter(
              example = "",
              description =
                  "UUID of the webpage, e.g. <tt>599a120c-2dd5-11e8-b467-0ed5f89f718b</tt>")
          @PathVariable("uuid")
          UUID uuid,
      @Parameter(
              name = "pLocale",
              description =
                  "Desired locale, e.g. <tt>de_DE</tt>. If unset, contents in all languages will be returned")
          @RequestParam(name = "pLocale", required = false)
          Locale pLocale,
      @Parameter(name = "active", description = "If set, object will only be returned if active")
          @RequestParam(name = "active", required = false)
          String active)
      throws ServiceException {
    Webpage webpage;
    if (active != null) {
      if (pLocale == null) {
        webpage = service.getByExampleAndActive(Webpage.builder().uuid(uuid).build());
      } else {
        webpage =
            service.getByExampleAndActiveAndLocale(Webpage.builder().uuid(uuid).build(), pLocale);
      }
    } else {
      if (pLocale == null) {
        webpage = service.getByExample(Webpage.builder().uuid(uuid).build());
      } else {
        webpage = service.getByExampleAndLocale(Webpage.builder().uuid(uuid).build(), pLocale);
      }
    }
    return new ResponseEntity<>(webpage, webpage != null ? HttpStatus.OK : HttpStatus.NOT_FOUND);
  }

  @Operation(summary = "Get (active or all) children of a webpage recursivly as JSON")
  @GetMapping(
      value = {
        "/v6/webpages/{uuid:" + ParameterHelper.UUID_PATTERN + "}/childrentree",
        "/v5/webpages/{uuid:" + ParameterHelper.UUID_PATTERN + "}/childrentree",
        "/latest/webpages/{uuid:" + ParameterHelper.UUID_PATTERN + "}/childrentree"
      },
      produces = MediaType.APPLICATION_JSON_VALUE)
  public List<Webpage> getChildrenTree(
      @Parameter(
              example = "",
              description =
                  "UUID of the root webpage, e.g. <tt>599a120c-2dd5-11e8-b467-0ed5f89f718b</tt>")
          @PathVariable("uuid")
          UUID uuid,
      @Parameter(name = "active", description = "If set, only active children will be returned")
          @RequestParam(name = "active", required = false)
          String active)
      throws ServiceException {

    return (active != null)
        ? service.getActiveChildrenTree(Webpage.builder().uuid(uuid).build())
        : service.getChildrenTree(Webpage.builder().uuid(uuid).build());
  }

  @Operation(summary = "Get parent of a webpage as JSON")
  @GetMapping(
      value = {
        "/v6/webpages/{uuid:" + ParameterHelper.UUID_PATTERN + "}/parent",
        "/v5/webpages/{uuid:" + ParameterHelper.UUID_PATTERN + "}/parent",
        "/v3/webpages/{uuid:" + ParameterHelper.UUID_PATTERN + "}/parent",
        "/latest/webpages/{uuid:" + ParameterHelper.UUID_PATTERN + "}/parent"
      },
      produces = MediaType.APPLICATION_JSON_VALUE)
  public Webpage getParent(
      @Parameter(
              example = "",
              description =
                  "UUID of the webpage, e.g. <tt>599a120c-2dd5-11e8-b467-0ed5f89f718b</tt>")
          @PathVariable("uuid")
          UUID uuid)
      throws ServiceException {
    return service.getParent(Webpage.builder().uuid(uuid).build());
  }

  @Operation(summary = "Get file resources related to webpage")
  @GetMapping(
      value = {
        "/v6/webpages/{uuid:" + ParameterHelper.UUID_PATTERN + "}/related/fileresources",
        "/v5/webpages/{uuid:" + ParameterHelper.UUID_PATTERN + "}/related/fileresources",
        "/v2/webpages/{uuid:" + ParameterHelper.UUID_PATTERN + "}/related/fileresources",
        "/latest/webpages/{uuid:" + ParameterHelper.UUID_PATTERN + "}/related/fileresources"
      },
      produces = MediaType.APPLICATION_JSON_VALUE)
  public PageResponse<FileResource> findRelatedFileResources(@PathVariable UUID uuid)
      throws ServiceException {
    PageRequest pageRequest = PageRequest.builder().pageNumber(0).pageSize(25).build();
    return service.findRelatedFileResources(Webpage.builder().uuid(uuid).build(), pageRequest);
  }

  @Override
  protected IdentifiableService<Webpage> getService() {
    return service;
  }

  @Operation(summary = "Get website of a webpage as JSON")
  @GetMapping(
      value = {
        "/v6/webpages/{uuid:" + ParameterHelper.UUID_PATTERN + "}/website",
        "/v5/webpages/{uuid:" + ParameterHelper.UUID_PATTERN + "}/website",
        "/v3/webpages/{uuid:" + ParameterHelper.UUID_PATTERN + "}/website",
        "/latest/webpages/{uuid:" + ParameterHelper.UUID_PATTERN + "}/website"
      },
      produces = MediaType.APPLICATION_JSON_VALUE)
  public Website getWebsite(
      @Parameter(
              example = "",
              description =
                  "UUID of the webpage, e.g. <tt>599a120c-2dd5-11e8-b467-0ed5f89f718b</tt>")
          @PathVariable("uuid")
          UUID uuid)
      throws ServiceException {
    return service.getWebsite(Webpage.builder().uuid(uuid).build());
  }

  @Operation(summary = "Save a newly created webpage")
  @PostMapping(
      value = {
        "/v6/webpages/{parentWebpageUuid}/webpage",
        "/v5/webpages/{parentWebpageUuid}/webpage",
        "/v2/webpages/{parentWebpageUuid}/webpage",
        "/latest/webpages/{parentWebpageUuid}/webpage"
      },
      produces = MediaType.APPLICATION_JSON_VALUE)
  public Webpage saveWithParentWebpage(
      @PathVariable UUID parentWebpageUuid, @RequestBody Webpage webpage, BindingResult errors)
      throws ServiceException, ValidationException {
    return service.saveWithParent(webpage, Webpage.builder().uuid(parentWebpageUuid).build());
  }

  @Operation(summary = "Save a newly created top-level webpage")
  @PostMapping(
      value = {
        "/v6/websites/{parentWebsiteUuid}/webpage",
        "/v5/websites/{parentWebsiteUuid}/webpage",
        "/v2/websites/{parentWebsiteUuid}/webpage",
        "/latest/websites/{parentWebsiteUuid}/webpage",
      },
      produces = MediaType.APPLICATION_JSON_VALUE)
  public Webpage saveWithParentWebsite(
      @PathVariable UUID parentWebsiteUuid, @RequestBody Webpage webpage, BindingResult errors)
      throws ServiceException {
    return service.saveWithParentWebsite(
        webpage, Website.builder().uuid(parentWebsiteUuid).build());
  }

  @Operation(summary = "Update a webpage")
  @PutMapping(
      value = {
        "/v6/webpages/{uuid:" + ParameterHelper.UUID_PATTERN + "}",
        "/v5/webpages/{uuid:" + ParameterHelper.UUID_PATTERN + "}",
        "/v2/webpages/{uuid:" + ParameterHelper.UUID_PATTERN + "}",
        "/latest/webpages/{uuid:" + ParameterHelper.UUID_PATTERN + "}"
      },
      produces = MediaType.APPLICATION_JSON_VALUE)
  public Webpage update(@PathVariable UUID uuid, @RequestBody Webpage webpage, BindingResult errors)
      throws ServiceException, ValidationException {
    assert Objects.equals(uuid, webpage.getUuid());
    service.update(webpage);
    return webpage;
  }

  @Operation(summary = "Update the order of a webpage's children")
  @PutMapping(
      value = {
        "/v6/webpages/{uuid:" + ParameterHelper.UUID_PATTERN + "}/children",
        "/v5/webpages/{uuid:" + ParameterHelper.UUID_PATTERN + "}/children",
        "/v3/webpages/{uuid:" + ParameterHelper.UUID_PATTERN + "}/children",
        "/latest/webpages/{uuid:" + ParameterHelper.UUID_PATTERN + "}/children"
      },
      produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity updateChildrenOrder(
      @Parameter(example = "", description = "UUID of the webpage") @PathVariable("uuid") UUID uuid,
      @Parameter(example = "", description = "List of the children") @RequestBody
          List<Webpage> rootPages)
      throws ServiceException {
    boolean successful =
        service.updateChildrenOrder(Webpage.builder().uuid(uuid).build(), rootPages);
    return successful
        ? new ResponseEntity<>(HttpStatus.NO_CONTENT)
        : new ResponseEntity<>(HttpStatus.NOT_FOUND);
  }
}
