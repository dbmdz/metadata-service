package de.digitalcollections.cudami.server.backend.api.repository.identifiable.entity;

import de.digitalcollections.model.api.identifiable.entity.Collection;
import de.digitalcollections.model.api.identifiable.entity.DigitalObject;
import de.digitalcollections.model.api.identifiable.entity.Project;
import de.digitalcollections.model.api.identifiable.resource.FileResource;
import de.digitalcollections.model.api.identifiable.resource.ImageFileResource;
import de.digitalcollections.model.api.paging.PageRequest;
import de.digitalcollections.model.api.paging.PageResponse;
import java.util.List;
import java.util.UUID;

/** Repository for Digital object persistence handling. */
public interface DigitalObjectRepository extends EntityRepository<DigitalObject> {

  DigitalObject findByIdentifier(String namespace, String id);

  default PageResponse<Collection> getCollections(
      DigitalObject digitalObject, PageRequest pageRequest) {
    return getCollections(digitalObject.getUuid(), pageRequest);
  }

  PageResponse<Collection> getCollections(UUID digitalObjectUuid, PageRequest pageRequest);

  default List<FileResource> getFileResources(DigitalObject digitalObject) {
    return getFileResources(digitalObject.getUuid());
  }

  List<FileResource> getFileResources(UUID digitalObjectUuid);

  default List<ImageFileResource> getImageFileResources(DigitalObject digitalObject) {
    return getImageFileResources(digitalObject.getUuid());
  }

  List<ImageFileResource> getImageFileResources(UUID digitalObjectUuid);

  default PageResponse<Project> getProjects(DigitalObject digitalObject, PageRequest pageRequest) {
    return getProjects(digitalObject.getUuid(), pageRequest);
  }

  PageResponse<Project> getProjects(UUID digitalObjectUuid, PageRequest pageRequest);

  List<FileResource> saveFileResources(
      DigitalObject digitalObject, List<FileResource> fileResources);

  List<FileResource> saveFileResources(UUID digitalObjectUuid, List<FileResource> fileResources);

  void deleteFileResources(UUID digitalObjectUuid);

  boolean deleteIdentifiers(UUID digitalObjectUuid);
}
