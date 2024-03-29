package io.github.dbmdz.metadata.server.business.impl.service.identifiable.resource;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import de.digitalcollections.model.identifiable.entity.digitalobject.DigitalObject;
import de.digitalcollections.model.identifiable.resource.LinkedDataFileResource;
import io.github.dbmdz.metadata.server.backend.api.repository.exceptions.RepositoryException;
import io.github.dbmdz.metadata.server.backend.api.repository.identifiable.resource.DigitalObjectLinkedDataFileResourceRepository;
import io.github.dbmdz.metadata.server.business.api.service.exceptions.ConflictException;
import io.github.dbmdz.metadata.server.business.api.service.exceptions.ServiceException;
import io.github.dbmdz.metadata.server.business.api.service.identifiable.resource.DigitalObjectLinkedDataFileResourceService;
import io.github.dbmdz.metadata.server.business.api.service.identifiable.resource.LinkedDataFileResourceService;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("The DigitalObjectLinkedDataFileResourceService")
class DigitalObjectLinkedDataFileResourceServiceImplTest {

  private DigitalObjectLinkedDataFileResourceRepository repo;
  private DigitalObjectLinkedDataFileResourceService service;

  private LinkedDataFileResourceService linkedDataFileResourceService;

  @BeforeEach
  public void beforeEach() throws ServiceException {
    linkedDataFileResourceService = mock(LinkedDataFileResourceService.class);
    repo = mock(DigitalObjectLinkedDataFileResourceRepository.class);
    service =
        new DigitalObjectLinkedDataFileResourceServiceImpl(repo, linkedDataFileResourceService);
  }

  @DisplayName("can delete resource and relation, when the resource is not referenced elsewhere")
  @Test
  public void deleteResourceAndRelation()
      throws ServiceException, ConflictException, RepositoryException {
    UUID uuid = UUID.randomUUID();
    DigitalObject digitalObject = DigitalObject.builder().uuid(uuid).label("Label").build();
    LinkedDataFileResource linkedDataFileResource =
        LinkedDataFileResource.builder().uuid(UUID.randomUUID()).build();

    digitalObject.setLinkedDataResources(List.of(linkedDataFileResource));

    when(repo.getLinkedDataFileResources(eq(digitalObject)))
        .thenReturn(List.of(linkedDataFileResource));
    when(repo.countDigitalObjectsForResource(eq(linkedDataFileResource.getUuid()))).thenReturn(0);
    when(repo.delete(any(UUID.class))).thenReturn(1);

    service.deleteLinkedDataFileResources(digitalObject);

    verify(repo, times(1)).delete(linkedDataFileResource.getUuid());
    verify(linkedDataFileResourceService, times(1)).delete(linkedDataFileResource);
  }

  @DisplayName("can delete relation only, when the resource is referenced elsewhere")
  @Test
  public void deleteRelationOnly() throws ServiceException, ConflictException, RepositoryException {
    UUID uuid = UUID.randomUUID();
    DigitalObject digitalObject = DigitalObject.builder().uuid(uuid).label("Label").build();
    LinkedDataFileResource linkedDataFileResource =
        LinkedDataFileResource.builder().uuid(UUID.randomUUID()).build();

    digitalObject.setLinkedDataResources(List.of(linkedDataFileResource));

    when(repo.getLinkedDataFileResources(eq(digitalObject)))
        .thenReturn(List.of(linkedDataFileResource));
    when(repo.countDigitalObjectsForResource(eq(linkedDataFileResource.getUuid()))).thenReturn(1);
    when(repo.delete(any(UUID.class))).thenReturn(1);

    service.deleteLinkedDataFileResources(digitalObject);

    verify(repo, times(1)).delete(linkedDataFileResource.getUuid());
    verify(linkedDataFileResourceService, never()).delete(linkedDataFileResource);
  }
}
