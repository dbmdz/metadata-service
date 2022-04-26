package de.digitalcollections.cudami.server.backend.api.repository.identifiable.resource;

import de.digitalcollections.model.identifiable.resource.FileResource;
import java.util.List;
import java.util.UUID;

public interface DigitalObjectRenderingFileResourceRepository {

  /**
   * Retrieve the list of rendering FileResources for a DigitalObject, identified by its UUID
   *
   * @param digitalObjectUuid the UUID of the DigitalObject
   * @return list of rendering FileResources
   */
  List<FileResource> findByDigitalObject(UUID digitalObjectUuid);

  public void removeByDigitalObject(UUID digitalObjectUuid);

  public void saveForDigitalObject(UUID digitalObjectUuid, List<FileResource> renderingResources);
}