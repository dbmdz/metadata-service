package de.digitalcollections.cudami.server.controller.identifiable.entity.agent;

import de.digitalcollections.cudami.server.business.api.service.exceptions.ConflictException;
import de.digitalcollections.cudami.server.business.api.service.exceptions.ServiceException;
import de.digitalcollections.cudami.server.business.api.service.exceptions.ValidationException;
import de.digitalcollections.cudami.server.business.api.service.identifiable.IdentifiableService;
import de.digitalcollections.cudami.server.business.api.service.identifiable.entity.agent.CorporateBodyService;
import de.digitalcollections.cudami.server.controller.ParameterHelper;
import de.digitalcollections.cudami.server.controller.identifiable.AbstractIdentifiableController;
import de.digitalcollections.model.identifiable.entity.agent.CorporateBody;
import de.digitalcollections.model.list.paging.PageResponse;
import de.digitalcollections.model.list.sorting.Order;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.UUID;
import java.util.regex.Pattern;
import javax.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Tag(name = "Corporate body controller")
public class CorporateBodyController extends AbstractIdentifiableController<CorporateBody> {

  private static final Pattern GNDID_PATTERN = Pattern.compile("(\\d+(-.)?)|(\\d+X)");

  private final CorporateBodyService corporateBodyService;

  public CorporateBodyController(CorporateBodyService corporateBodyservice) {
    this.corporateBodyService = corporateBodyservice;
  }

  @Operation(summary = "Delete a corporate body")
  @DeleteMapping(
      value = {"/v6/corporatebodies/{uuid}"},
      produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity delete(
      @Parameter(example = "", description = "UUID of the corporate body") @PathVariable("uuid")
          UUID uuid)
      throws ConflictException {
    boolean successful;
    try {
      successful = corporateBodyService.delete(uuid);
    } catch (ServiceException e) {
      return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
    }
    return successful
        ? new ResponseEntity<>(HttpStatus.NO_CONTENT)
        : new ResponseEntity<>(HttpStatus.NOT_FOUND);
  }

  @Override
  protected IdentifiableService<CorporateBody> getService() {
    return corporateBodyService;
  }

  @Operation(summary = "Fetch a corporate body by GND-ID from external system and save it")
  @PostMapping(
      value = {
        "/v6/corporatebodies/gnd/{gndId}",
        "/v5/corporatebodies/gnd/{gndId}",
        "/v3/corporatebodies/gnd/{gndId}"
      },
      produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<CorporateBody> fetchAndSaveByGndId(
      @Parameter(
              example = "",
              description = "GND-ID of the corporate body, e.g. <tt>2007744-0</tt>")
          @PathVariable("gndId")
          String gndId)
      throws ServiceException {
    if (!GNDID_PATTERN.matcher(gndId).matches()) {
      throw new IllegalArgumentException("Invalid GND ID: " + gndId);
    }
    CorporateBody corporateBody = corporateBodyService.fetchAndSaveByGndId(gndId);
    return new ResponseEntity<>(
        corporateBody, corporateBody != null ? HttpStatus.OK : HttpStatus.NOT_FOUND);
  }

  @Operation(summary = "Get all corporate bodies")
  @GetMapping(
      value = {"/v6/corporatebodies"},
      produces = MediaType.APPLICATION_JSON_VALUE)
  public PageResponse<CorporateBody> find(
      @RequestParam(name = "pageNumber", required = false, defaultValue = "0") int pageNumber,
      @RequestParam(name = "pageSize", required = false, defaultValue = "25") int pageSize,
      @RequestParam(name = "sortBy", required = false) List<Order> sortBy,
      @RequestParam(name = "searchTerm", required = false) String searchTerm,
      @RequestParam(name = "label", required = false) String labelTerm,
      @RequestParam(name = "labelLanguage", required = false) Locale labelLanguage,
      @RequestParam(name = "name", required = false) String nameTerm,
      @RequestParam(name = "nameLanguage", required = false) Locale nameLanguage) {
    return super.find(
        pageNumber, pageSize, sortBy, searchTerm, labelTerm, labelLanguage, nameTerm, nameLanguage);
  }

  @Override
  @Operation(
      summary = "Get a corporate body by namespace and id",
      description =
          "Separate namespace and id with a colon, e.g. foo:bar. It is also possible, to add a .json suffix, which will be ignored then")
  @GetMapping(
      value = {
        "/v6/corporatebodies/identifier/**",
        "/v5/corporatebodies/identifier/**",
        "/v3/corporatebodies/identifier/**",
        "/latest/corporatebodies/identifier/**"
      },
      produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<CorporateBody> getByIdentifier(HttpServletRequest request)
      throws ServiceException, ValidationException {
    return super.getByIdentifier(request);
  }

  @Operation(summary = "Get corporate body by refId")
  @GetMapping(
      value = {
        "/v6/corporatebodies/{refId:[0-9]+}",
        "/v5/corporatebodies/{refId:[0-9]+}",
        "/v3/corporatebodies/{refId:[0-9]+}",
        "/latest/corporatebodies/{refId:[0-9]+}"
      },
      produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<CorporateBody> getByRefId(
      @Parameter(example = "", description = "reference id") @PathVariable("refId") long refId)
      throws ServiceException {
    CorporateBody corporateBody = corporateBodyService.getByRefId(refId);
    return new ResponseEntity<>(
        corporateBody, corporateBody != null ? HttpStatus.OK : HttpStatus.NOT_FOUND);
  }

  @Operation(summary = "Get a corporate body by uuid")
  @GetMapping(
      value = {
        "/v6/corporatebodies/{uuid:" + ParameterHelper.UUID_PATTERN + "}",
        "/v5/corporatebodies/{uuid:" + ParameterHelper.UUID_PATTERN + "}",
        "/v2/corporatebodies/{uuid:" + ParameterHelper.UUID_PATTERN + "}",
        "/latest/corporatebodies/{uuid:" + ParameterHelper.UUID_PATTERN + "}"
      },
      produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<CorporateBody> getByUuid(
      @Parameter(
              example = "",
              description =
                  "UUID of the corporate body, e.g. <tt>599a120c-2dd5-11e8-b467-0ed5f89f718b</tt>")
          @PathVariable("uuid")
          UUID uuid,
      @Parameter(
              name = "pLocale",
              description =
                  "Desired locale, e.g. <tt>de_DE</tt>. If unset, contents in all languages will be returned")
          @RequestParam(name = "pLocale", required = false)
          Locale pLocale)
      throws ServiceException {

    CorporateBody corporateBody;
    if (pLocale == null) {
      corporateBody = corporateBodyService.getByUuid(uuid);
    } else {
      corporateBody = corporateBodyService.getByUuidAndLocale(uuid, pLocale);
    }
    return new ResponseEntity<>(
        corporateBody, corporateBody != null ? HttpStatus.OK : HttpStatus.NOT_FOUND);
  }

  @Operation(summary = "Get languages of all corporatebodies")
  @GetMapping(
      value = {
        "/v6/corporatebodies/languages",
        "/v5/corporatebodies/languages",
        "/v3/corporatebodies/languages",
        "/latest/corporatebodies/languages"
      },
      produces = MediaType.APPLICATION_JSON_VALUE)
  public List<Locale> getLanguages() {
    return corporateBodyService.getLanguages();
  }

  @Operation(summary = "Save a newly created corporate body")
  @PostMapping(
      value = {
        "/v6/corporatebodies",
        "/v5/corporatebodies",
        "/v2/corporatebodies",
        "/latest/corporatebodies"
      },
      produces = MediaType.APPLICATION_JSON_VALUE)
  public CorporateBody save(@RequestBody CorporateBody corporateBody, BindingResult errors)
      throws ServiceException, ValidationException {
    corporateBodyService.save(corporateBody);
    return corporateBody;
  }

  @Operation(summary = "Update a corporate body")
  @PutMapping(
      value = {
        "/v6/corporatebodies/{uuid}",
        "/v5/corporatebodies/{uuid}",
        "/v2/corporatebodies/{uuid}",
        "/latest/corporatebodies/{uuid}"
      },
      produces = MediaType.APPLICATION_JSON_VALUE)
  public CorporateBody update(
      @Parameter(
              example = "",
              description =
                  "UUID of the corporate body, e.g. <tt>599a120c-2dd5-11e8-b467-0ed5f89f718b</tt>")
          @PathVariable("uuid")
          UUID uuid,
      @RequestBody CorporateBody corporateBody,
      BindingResult errors)
      throws ServiceException, ValidationException {
    assert Objects.equals(uuid, corporateBody.getUuid());
    corporateBodyService.update(corporateBody);
    return corporateBody;
  }
}
