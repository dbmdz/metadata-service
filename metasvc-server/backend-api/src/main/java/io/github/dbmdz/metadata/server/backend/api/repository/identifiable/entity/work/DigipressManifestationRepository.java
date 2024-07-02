package io.github.dbmdz.metadata.server.backend.api.repository.identifiable.entity.work;

import de.digitalcollections.model.identifiable.entity.manifestation.Manifestation;
import de.digitalcollections.model.list.ListRequest;
import de.digitalcollections.model.list.ListResponse;
import io.github.dbmdz.metadata.server.backend.api.repository.exceptions.RepositoryException;

public interface DigipressManifestationRepository {

  void refreshTable();

  ListResponse<Manifestation, ListRequest> getNewspapers(ListRequest listRequest)
      throws RepositoryException;
}
