package io.github.dbmdz.metadata.server.business.impl.service.identifiable.resource;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import de.digitalcollections.model.file.MimeType;
import de.digitalcollections.model.identifiable.entity.digitalobject.DigitalObject;
import de.digitalcollections.model.identifiable.resource.FileResource;
import de.digitalcollections.model.identifiable.resource.TextFileResource;
import io.github.dbmdz.metadata.server.backend.api.repository.exceptions.RepositoryException;
import io.github.dbmdz.metadata.server.backend.api.repository.identifiable.resource.DigitalObjectRenderingFileResourceRepository;
import io.github.dbmdz.metadata.server.business.api.service.exceptions.ConflictException;
import io.github.dbmdz.metadata.server.business.api.service.exceptions.ServiceException;
import io.github.dbmdz.metadata.server.business.api.service.identifiable.resource.DigitalObjectRenderingFileResourceService;
import io.github.dbmdz.metadata.server.business.api.service.identifiable.resource.FileResourceMetadataService;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("The DigitalObjectRenderingFileResourceService")
class DigitalObjectRenderingFileResourceServiceImplTest {

  private DigitalObjectRenderingFileResourceRepository repo;
  private DigitalObjectRenderingFileResourceService service;

  private ApplicationFileResourceServiceImpl applicationFileResourceService;
  private AudioFileResourceServiceImpl audioFileResourceService;
  private FileResourceMetadataService<FileResource> fileResourceMetadataService;
  private ImageFileResourceServiceImpl imageFileResourceService;
  private LinkedDataFileResourceServiceImpl linkedDataFileResourceService;
  private TextFileResourceServiceImpl textFileResourceService;
  private VideoFileResourceServiceImpl videoFileResourceService;

  @BeforeEach
  public void beforeEach() throws ServiceException {
    applicationFileResourceService = mock(ApplicationFileResourceServiceImpl.class);
    audioFileResourceService = mock(AudioFileResourceServiceImpl.class);
    fileResourceMetadataService = mock(FileResourceMetadataService.class);
    imageFileResourceService = mock(ImageFileResourceServiceImpl.class);
    linkedDataFileResourceService = mock(LinkedDataFileResourceServiceImpl.class);
    textFileResourceService = mock(TextFileResourceServiceImpl.class);
    repo = mock(DigitalObjectRenderingFileResourceRepository.class);
    videoFileResourceService = mock(VideoFileResourceServiceImpl.class);
    service =
        new DigitalObjectRenderingFileResourceServiceImpl(
            applicationFileResourceService,
            audioFileResourceService,
            fileResourceMetadataService,
            imageFileResourceService,
            linkedDataFileResourceService,
            textFileResourceService,
            videoFileResourceService,
            repo);
  }

  @DisplayName("can delete resource and relation, when the resource is not referenced elsewhere")
  @Test
  public void deleteResourceAndRelation()
      throws ServiceException, ConflictException, RepositoryException {
    UUID uuid = UUID.randomUUID();
    DigitalObject digitalObject = DigitalObject.builder().uuid(uuid).label("Label").build();
    TextFileResource renderingFileResource =
        TextFileResource.builder()
            .uuid(UUID.randomUUID())
            .mimeType(MimeType.fromTypename("text/html"))
            .build();

    digitalObject.setRenderingResources(List.of(renderingFileResource));

    when(repo.getRenderingFileResources(eq(digitalObject)))
        .thenReturn(List.of(renderingFileResource));
    when(repo.countDigitalObjectsForResource(eq(renderingFileResource.getUuid()))).thenReturn(0);
    when(repo.delete(any(TextFileResource.class))).thenReturn(1);

    service.deleteRenderingFileResources(digitalObject);

    verify(repo, times(1)).delete(renderingFileResource);
    verify(fileResourceMetadataService, times(1)).delete(renderingFileResource);
  }

  @DisplayName("can delete relation only, when the resource is referenced elsewhere")
  @Test
  public void deleteOnlyRelation() throws ServiceException, ConflictException, RepositoryException {
    UUID uuid = UUID.randomUUID();
    DigitalObject digitalObject = DigitalObject.builder().uuid(uuid).label("Label").build();
    TextFileResource renderingFileResource =
        TextFileResource.builder()
            .uuid(UUID.randomUUID())
            .mimeType(MimeType.fromTypename("text/html"))
            .build();

    digitalObject.setRenderingResources(List.of(renderingFileResource));

    when(repo.getRenderingFileResources(eq(digitalObject)))
        .thenReturn(List.of(renderingFileResource));
    when(repo.countDigitalObjectsForResource(eq(renderingFileResource.getUuid()))).thenReturn(1);
    when(repo.delete(any(TextFileResource.class))).thenReturn(1);

    service.deleteRenderingFileResources(digitalObject);

    verify(repo, times(1)).delete(renderingFileResource);
    verify(fileResourceMetadataService, never()).delete(renderingFileResource);
  }
}
