package io.github.dbmdz.metadata.server.business.impl.service.semantic;

import de.digitalcollections.model.semantic.Tag;
import io.github.dbmdz.metadata.server.backend.api.repository.exceptions.RepositoryException;
import io.github.dbmdz.metadata.server.backend.api.repository.semantic.TagRepository;
import io.github.dbmdz.metadata.server.business.api.service.exceptions.ServiceException;
import io.github.dbmdz.metadata.server.business.api.service.semantic.TagService;
import io.github.dbmdz.metadata.server.business.impl.service.UniqueObjectServiceImpl;
import org.springframework.stereotype.Service;

@Service
// @Transactional(rollbackFor = Exception.class) //is set on super class
public class TagServiceImpl extends UniqueObjectServiceImpl<Tag, TagRepository>
    implements TagService {

  public TagServiceImpl(TagRepository repository) {
    super(repository);
  }

  @Override
  public Tag getByValue(String value) throws ServiceException {
    try {
      return repository.getByValue(value);
    } catch (RepositoryException e) {
      throw new ServiceException("cannot get by value=" + value + ": " + e, e);
    }
  }
}
