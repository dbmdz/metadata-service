package io.github.dbmdz.metadata.server.business.api.service.identifiable.entity;

import de.digitalcollections.model.identifiable.entity.Article;
import de.digitalcollections.model.identifiable.entity.agent.Agent;
import io.github.dbmdz.metadata.server.business.api.service.exceptions.ServiceException;
import java.util.List;

/** Service for Article. */
public interface ArticleService extends EntityService<Article> {

  boolean addCreators(Article article, List<Agent> agents) throws ServiceException;

  List<Agent> getCreators(Article article) throws ServiceException;

  boolean removeCreator(Article article, Agent agent) throws ServiceException;
}
