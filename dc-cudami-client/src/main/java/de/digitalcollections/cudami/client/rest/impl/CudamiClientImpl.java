package de.digitalcollections.cudami.client.rest.impl;

import de.digitalcollections.cudami.client.rest.api.CudamiClient;
import de.digitalcollections.cudami.client.rest.backend.CudamiBackend;
import java.util.List;
import java.util.Locale;

public class CudamiClientImpl implements CudamiClient {

  private final CudamiBackend backend;

  public CudamiClientImpl(CudamiBackend backend) {
    this.backend = backend;
  }

  @Override
  public List<Locale> getAllLocales() throws Exception {
    return backend.getAllLocales();
  }

  @Override
  public Locale getDefaultLocale() throws Exception {
    return backend.getDefaultLocale();
  }
}
