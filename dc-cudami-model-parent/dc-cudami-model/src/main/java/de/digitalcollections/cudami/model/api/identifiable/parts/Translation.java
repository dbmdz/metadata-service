package de.digitalcollections.cudami.model.api.identifiable.parts;

public interface Translation {

  public String getLang();

  public void setLang(String lang);

  public String getText();

  public void setText(String text);

  public boolean has(String lang);

}
