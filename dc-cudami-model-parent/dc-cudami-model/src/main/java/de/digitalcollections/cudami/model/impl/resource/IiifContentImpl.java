package de.digitalcollections.cudami.model.impl.resource;

import de.digitalcollections.cudami.model.api.Thumbnail;
import de.digitalcollections.cudami.model.api.resource.IiifContent;
import de.digitalcollections.cudami.model.impl.identifiable.IdentifiableImpl;
import de.digitalcollections.iiif.presentation.model.api.enums.Version;
import java.net.URL;
import java.util.HashMap;
import java.util.Locale;

public class IiifContentImpl extends IdentifiableImpl implements IiifContent {

  private HashMap<Locale, String> attributions;
  private HashMap<Locale, String> descriptions;
  private URL logoUrl;
  private HashMap<Locale, String> manifestLabels;
  private URL manifestUrl;
  private Version manifestVersion;
  private Thumbnail thumbnail;

  public Thumbnail getThumbnail() {
    return thumbnail;
  }

  public void setThumbnail(Thumbnail thumbnail) {
    this.thumbnail = thumbnail;
  }

  @Override
  public URL getManifestUrl() {
    return manifestUrl;
  }

  @Override
  public void setManifesturl(URL manifestUrl) {
    this.manifestUrl = manifestUrl;
  }

  @Override
  public URL getLogoUrl() {
    return logoUrl;
  }

  @Override
  public void setLogoUrl(URL logoUrl) {
    this.logoUrl = logoUrl;
  }

  @Override
  public HashMap<Locale, String> getManifestLabels() {
    return manifestLabels;
  }

  @Override
  public void setManifestLabels(HashMap<Locale, String> manifestLabels) {
    this.manifestLabels = manifestLabels;
  }

  @Override
  public HashMap<Locale, String> getDescriptions() {
    return descriptions;
  }

  @Override
  public void setDescriptions(HashMap<Locale, String> descriptions) {
    this.descriptions = descriptions;
  }

  @Override
  public HashMap<Locale, String> getAttributions() {
    return attributions;
  }

  @Override
  public void setAttributions(HashMap<Locale, String> attributions) {
    this.attributions = attributions;
  }

  @Override
  public Version getManifestVersion() {
    return manifestVersion;
  }

  @Override
  public void setManifestVersion(Version version) {
    this.manifestVersion = version;
  }

}
