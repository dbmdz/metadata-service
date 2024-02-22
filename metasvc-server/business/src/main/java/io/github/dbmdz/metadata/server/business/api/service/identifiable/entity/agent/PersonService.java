package io.github.dbmdz.metadata.server.business.api.service.identifiable.entity.agent;

import de.digitalcollections.model.identifiable.entity.agent.Person;
import de.digitalcollections.model.identifiable.entity.geo.location.GeoLocation;
import de.digitalcollections.model.list.paging.PageRequest;
import de.digitalcollections.model.list.paging.PageResponse;
import io.github.dbmdz.metadata.server.business.api.service.exceptions.ServiceException;

public interface PersonService extends AgentService<Person> {

  PageResponse<Person> findByGeoLocationOfBirth(GeoLocation geoLocation, PageRequest pageRequest)
      throws ServiceException;

  PageResponse<Person> findByGeoLocationOfDeath(GeoLocation geoLocation, PageRequest pageRequest)
      throws ServiceException;
}
