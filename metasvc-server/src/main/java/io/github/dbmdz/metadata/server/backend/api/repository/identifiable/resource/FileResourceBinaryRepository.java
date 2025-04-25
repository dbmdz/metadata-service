package io.github.dbmdz.metadata.server.backend.api.repository.identifiable.resource;

import de.digitalcollections.model.exception.ResourceNotFoundException;
import de.digitalcollections.model.file.MimeType;
import de.digitalcollections.model.identifiable.resource.FileResource;
import io.github.dbmdz.metadata.server.backend.api.repository.exceptions.RepositoryException;
import jakarta.validation.constraints.NotNull;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.UUID;
import org.w3c.dom.Document;

public interface FileResourceBinaryRepository {

  void assertReadability(FileResource resource)
      throws RepositoryException, ResourceNotFoundException;

  default FileResource getByExampleAndMimetype(FileResource fileResource, MimeType mimeType)
      throws RepositoryException, ResourceNotFoundException {
    if (fileResource == null) {
      throw new IllegalArgumentException("get failed: given object must not be null");
    }
    return getByExampleAndMimetype(fileResource.getUuid(), mimeType);
  }

  FileResource getByExampleAndMimetype(UUID uuid, MimeType mimeType)
      throws RepositoryException, ResourceNotFoundException;

  byte[] getAsBytes(FileResource resource) throws RepositoryException, ResourceNotFoundException;

  Document getAsDocument(FileResource resource)
      throws RepositoryException, ResourceNotFoundException;

  InputStream getInputStream(FileResource resource)
      throws RepositoryException, ResourceNotFoundException;

  void save(@NotNull FileResource fileResource, @NotNull InputStream binaryData)
      throws RepositoryException;

  void save(FileResource fileResource, String input, Charset charset) throws RepositoryException;
}
