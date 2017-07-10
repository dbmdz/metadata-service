package de.digitalcollections.cudami.client.webapp.propertyeditor;

import de.digitalcollections.cudami.model.api.security.enums.Role;
import java.beans.PropertyEditorSupport;
import org.springframework.stereotype.Component;

@Component
public class RoleEditor extends PropertyEditorSupport {

  @Override
  public void setAsText(String roleName) {
    setValue(Role.valueOf(roleName).getAuthority());
  }
}
