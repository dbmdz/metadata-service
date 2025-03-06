package de.digitalcollections.model.security;

/** A user's role. */
public enum Role {
  ADMIN,
  CONTENT_MANAGER;

  /** Prefix needed by Spring Security */
  public static final String PREFIX = "ROLE_";

  public String getAuthority() {
    return PREFIX + name();
  }
}
