package io.github.dbmdz.metadata.server.business.impl.service.identifiable.resource;

import de.digitalcollections.cudami.model.config.CudamiConfig;
import de.digitalcollections.model.file.MimeType;
import de.digitalcollections.model.identifiable.resource.FileResource;
import de.digitalcollections.model.identifiable.resource.ImageFileResource;
import io.github.dbmdz.metadata.server.backend.api.repository.identifiable.resource.FileResourceMetadataRepository;
import io.github.dbmdz.metadata.server.backend.api.repository.identifiable.resource.ImageFileResourceRepository;
import io.github.dbmdz.metadata.server.business.api.service.LocaleService;
import io.github.dbmdz.metadata.server.business.api.service.identifiable.IdentifierService;
import io.github.dbmdz.metadata.server.business.api.service.identifiable.alias.UrlAliasService;
import io.github.dbmdz.metadata.server.business.api.service.identifiable.resource.ImageFileResourceService;
import io.github.dbmdz.metadata.server.business.impl.service.identifiable.IdentifiableServiceImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

// @Transactional should not be set in derived class to prevent overriding, check base class instead
@Service
public class ImageFileResourceServiceImpl
    extends IdentifiableServiceImpl<ImageFileResource, ImageFileResourceRepository>
    implements ImageFileResourceService {

  private static final Logger LOGGER = LoggerFactory.getLogger(ImageFileResourceServiceImpl.class);

  public ImageFileResourceServiceImpl(
      ImageFileResourceRepository imageFileResourceRepository,
      IdentifierService identifierService,
      UrlAliasService urlAliasService,
      LocaleService localeService,
      CudamiConfig cudamiConfig) {
    super(
        imageFileResourceRepository,
        identifierService,
        urlAliasService,
        localeService,
        cudamiConfig);
  }

  @Override
  public FileResource createByMimeType(MimeType mimeType) {
    return ((FileResourceMetadataRepository) repository).createByMimeType(mimeType);
  }
}
