package io.github.dbmdz.metadata.server.business.impl.service.identifiable.resource;

import de.digitalcollections.model.exception.ResourceNotFoundException;
import de.digitalcollections.model.file.MimeType;
import de.digitalcollections.model.identifiable.resource.FileResource;
import de.digitalcollections.model.validation.ValidationException;
import io.github.dbmdz.metadata.server.backend.api.repository.exceptions.RepositoryException;
import io.github.dbmdz.metadata.server.backend.api.repository.identifiable.resource.FileResourceBinaryRepository;
import io.github.dbmdz.metadata.server.business.api.service.exceptions.ServiceException;
import io.github.dbmdz.metadata.server.business.api.service.identifiable.resource.FileResourceBinaryService;
import io.github.dbmdz.metadata.server.business.api.service.identifiable.resource.FileResourceMetadataService;
import java.io.InputStream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import org.w3c.dom.Document;

@Service
@Transactional(rollbackFor = Exception.class, isolation = Isolation.READ_COMMITTED)
public class FileResourceBinaryServiceImpl implements FileResourceBinaryService {

  private static final Logger LOGGER = LoggerFactory.getLogger(FileResourceBinaryServiceImpl.class);

  private final FileResourceBinaryRepository binaryRepository;
  private final FileResourceMetadataService<FileResource> metadataService;

  public FileResourceBinaryServiceImpl(
      FileResourceBinaryRepository binaryRepository,
      @Qualifier("fileResourceMetadataService")
          FileResourceMetadataService<FileResource> metadataService) {
    this.binaryRepository = binaryRepository;
    this.metadataService = metadataService;
  }

  @Override
  public void assertReadability(FileResource resource) throws ServiceException {
    try {
      binaryRepository.assertReadability(resource);
    } catch (ResourceNotFoundException | RepositoryException ex) {
      throw new ServiceException("File resource " + resource.getUri() + " not readable.", ex);
    }
  }

  @Override
  public byte[] getAsBytes(FileResource resource) throws ServiceException {
    try {
      return binaryRepository.getAsBytes(resource);
    } catch (ResourceNotFoundException | RepositoryException ex) {
      throw new ServiceException(
          "Can not return file resource " + resource.getUri() + " as bytes.", ex);
    }
  }

  @Override
  public Document getAsDocument(FileResource resource) throws ServiceException {
    try {
      return binaryRepository.getAsDocument(resource);
    } catch (ResourceNotFoundException | RepositoryException ex) {
      throw new ServiceException(
          "Can not return file resource " + resource.getUri() + " as document.", ex);
    }
  }

  @Override
  public FileResource getByExampleAndMimetype(FileResource example, MimeType mimeType)
      throws ServiceException {
    try {
      return binaryRepository.getByExampleAndMimetype(example, mimeType);
    } catch (ResourceNotFoundException | RepositoryException ex) {
      throw new ServiceException("File resource " + example + " not found.", ex);
    }
  }

  @Override
  public InputStream getInputStream(FileResource resource) throws ServiceException {
    try {
      return binaryRepository.getInputStream(resource);
    } catch (ResourceNotFoundException | RepositoryException ex) {
      throw new ServiceException(
          "Can not return file resource " + resource.getUri() + " as inputstream.", ex);
    }
  }

  @Override
  public void save(FileResource fileResource, InputStream binaryData)
      throws ServiceException, ValidationException {
    try {
      binaryRepository.save(fileResource, binaryData);
      metadataService.save(fileResource);
    } catch (RepositoryException e) {
      throw new ServiceException(
          "Cannot save fileResource %s: %s".formatted(fileResource.getFilename(), e.getMessage()),
          e);
    }
  }
}
