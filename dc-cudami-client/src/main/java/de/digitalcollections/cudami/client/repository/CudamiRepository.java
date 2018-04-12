package de.digitalcollections.cudami.client.repository;

import de.digitalcollections.cudami.model.api.identifiable.entity.Website;
import java.util.List;
import java.util.Locale;

public interface CudamiRepository {

  public Locale getDefaultLocale() throws Exception;

  public List<Locale> getAllLocales() throws Exception;

  public String getWebpage(String uuid) throws Exception;

  public Website getWebsite(String uuid) throws Exception;
}
