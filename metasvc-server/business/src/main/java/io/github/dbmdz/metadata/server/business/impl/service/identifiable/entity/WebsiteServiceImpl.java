package io.github.dbmdz.metadata.server.business.impl.service.identifiable.entity;

import de.digitalcollections.cudami.model.config.CudamiConfig;
import de.digitalcollections.model.identifiable.entity.Website;
import de.digitalcollections.model.identifiable.web.Webpage;
import de.digitalcollections.model.list.paging.PageRequest;
import de.digitalcollections.model.list.paging.PageResponse;
import io.github.dbmdz.metadata.server.backend.api.repository.exceptions.RepositoryException;
import io.github.dbmdz.metadata.server.backend.api.repository.identifiable.entity.WebsiteRepository;
import io.github.dbmdz.metadata.server.business.api.service.LocaleService;
import io.github.dbmdz.metadata.server.business.api.service.exceptions.ServiceException;
import io.github.dbmdz.metadata.server.business.api.service.identifiable.IdentifierService;
import io.github.dbmdz.metadata.server.business.api.service.identifiable.alias.UrlAliasService;
import io.github.dbmdz.metadata.server.business.api.service.identifiable.entity.WebsiteService;
import io.github.dbmdz.metadata.server.business.api.service.identifiable.web.WebpageService;
import io.github.dbmdz.metadata.server.config.HookProperties;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

/** Service for Website handling. */
// @Transactional should not be set in derived class to prevent overriding, check base class instead
@Service
public class WebsiteServiceImpl extends EntityServiceImpl<Website> implements WebsiteService {

  private static final Logger LOGGER = LoggerFactory.getLogger(WebsiteServiceImpl.class);

  private final WebpageService webpageService;

  public WebsiteServiceImpl(
      WebsiteRepository repository,
      IdentifierService identifierService,
      UrlAliasService urlAliasService,
      HookProperties hookProperties,
      CudamiConfig cudamiConfig,
      LocaleService localeService,
      WebpageService webpageService) {
    super(
        repository,
        identifierService,
        urlAliasService,
        hookProperties,
        localeService,
        cudamiConfig);
    this.webpageService = webpageService;
  }

  @Override
  public PageResponse<Webpage> findRootWebpages(Website website, PageRequest pageRequest)
      throws ServiceException {
    try {
      return webpageService.findRootWebpagesForWebsite(website, pageRequest);
    } catch (ServiceException e) {
      throw new ServiceException("Backend failure", e);
    }
  }

  @Override
  public List<Webpage> getRootWebpages(Website website) throws ServiceException {
    List<Webpage> rootWebpages;
    try {
      rootWebpages = ((WebsiteRepository) repository).getRootWebpages(website);
    } catch (RepositoryException e) {
      throw new ServiceException("Backend failure", e);
    }
    webpageService.setPublicationStatus(rootWebpages);
    return rootWebpages;
  }

  @Override
  public boolean updateRootWebpagesOrder(Website website, List<Webpage> rootPages)
      throws ServiceException {
    try {
      return ((WebsiteRepository) repository).updateRootWebpagesOrder(website, rootPages);
    } catch (RepositoryException e) {
      throw new ServiceException("Backend failure", e);
    }
  }
}
