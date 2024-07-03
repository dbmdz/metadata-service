package io.github.dbmdz.metadata.server.controller.identifiable.entity.work;

import de.digitalcollections.model.identifiable.entity.manifestation.Manifestation;
import de.digitalcollections.model.list.ListRequest;
import de.digitalcollections.model.list.ListResponse;
import de.digitalcollections.model.list.filtering.Filtering;
import de.digitalcollections.model.list.sorting.Order;
import de.digitalcollections.model.list.sorting.Sorting;
import io.github.dbmdz.metadata.server.backend.api.repository.exceptions.RepositoryException;
import io.github.dbmdz.metadata.server.business.api.service.identifiable.entity.work.DigipressService;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Tag(name = "DigiPress optimised controller")
public class DigipressController {

  private DigipressService service;

  public DigipressController(DigipressService digipressService) {
    service = digipressService;
  }

  @PutMapping(value = "/v6/digipress/refresh")
  @ResponseStatus(code = HttpStatus.NO_CONTENT)
  public void refreshTable() {
    service.refreshTable();
  }

  @GetMapping(path = "/v6/digipress", produces = MediaType.APPLICATION_JSON_VALUE)
  @ResponseBody
  public ListResponse<Manifestation, ListRequest> find(
      @RequestParam(name = "filtering", required = false) Filtering filtering,
      @RequestParam(name = "sortBy", required = false) List<Order> sortBy)
      throws RepositoryException {
    ListRequest listRequest = new ListRequest();
    listRequest.add(filtering);
    if (sortBy != null && !sortBy.isEmpty()) listRequest.add(new Sorting(sortBy));
    return service.getNewspapers(listRequest);
  }
}
