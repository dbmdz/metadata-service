package io.github.dbmdz.metadata.server.business.api.service.identifiable.resource;

import de.digitalcollections.model.identifiable.entity.digitalobject.DigitalObject;
import de.digitalcollections.model.identifiable.resource.FileResource;
import io.github.dbmdz.metadata.server.business.api.service.exceptions.ServiceException;
import java.util.List;

public interface DigitalObjectRenderingFileResourceService {

  void deleteRenderingFileResources(DigitalObject digitalObject) throws ServiceException;

  List<FileResource> getRenderingFileResources(DigitalObject digitalObject) throws ServiceException;

  void setRenderingFileResources(DigitalObject digitalObject, List<FileResource> renderingResources)
      throws ServiceException;
}
