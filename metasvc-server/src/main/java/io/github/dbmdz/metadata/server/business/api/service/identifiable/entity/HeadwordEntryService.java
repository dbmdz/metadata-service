package io.github.dbmdz.metadata.server.business.api.service.identifiable.entity;

import de.digitalcollections.model.identifiable.entity.HeadwordEntry;
import de.digitalcollections.model.identifiable.entity.agent.Agent;
import de.digitalcollections.model.semantic.Headword;
import io.github.dbmdz.metadata.server.business.api.service.exceptions.ServiceException;
import java.util.List;

public interface HeadwordEntryService extends EntityService<HeadwordEntry> {

  public List<HeadwordEntry> getByHeadword(Headword headword) throws ServiceException;

  List<Agent> getCreators(HeadwordEntry headwordEntry) throws ServiceException;
}
