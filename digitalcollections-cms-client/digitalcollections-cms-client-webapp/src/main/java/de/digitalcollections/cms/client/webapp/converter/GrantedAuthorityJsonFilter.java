package de.digitalcollections.cms.client.webapp.converter;

import com.fasterxml.jackson.annotation.JsonIgnore;

public abstract class GrantedAuthorityJsonFilter {

  @JsonIgnore
  abstract String getAuthority();
}
