package io.github.dbmdz.metadata.server.business.impl.service.identifiable.entity.work;

import de.digitalcollections.model.identifiable.entity.manifestation.Manifestation;
import de.digitalcollections.model.list.ListRequest;
import de.digitalcollections.model.list.ListResponse;
import io.github.dbmdz.metadata.server.backend.api.repository.exceptions.RepositoryException;
import io.github.dbmdz.metadata.server.backend.api.repository.identifiable.entity.work.DigipressRepository;
import io.github.dbmdz.metadata.server.business.api.service.identifiable.entity.work.DigipressService;
import org.springframework.stereotype.Service;

@Service
public class DigipressServiceImpl implements DigipressService {

  private DigipressRepository repository;

  public DigipressServiceImpl(DigipressRepository repository) {
    this.repository = repository;
  }

  @Override
  public void refreshTable() {
    repository.refreshTable();
  }

  @Override
  public ListResponse<Manifestation, ListRequest> getNewspapers(ListRequest listRequest)
      throws RepositoryException {
    return repository.getNewspapers(listRequest);
  }
}
