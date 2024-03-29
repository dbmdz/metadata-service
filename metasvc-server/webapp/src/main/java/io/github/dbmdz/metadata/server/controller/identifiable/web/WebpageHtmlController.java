package io.github.dbmdz.metadata.server.controller.identifiable.web;

import de.digitalcollections.model.identifiable.web.Webpage;
import de.digitalcollections.model.view.RenderingHints;
import io.github.dbmdz.metadata.server.business.api.service.exceptions.ServiceException;
import io.github.dbmdz.metadata.server.business.api.service.identifiable.web.WebpageService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.Locale;
import java.util.UUID;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@Tag(name = "Webpage HTML controller")
public class WebpageHtmlController {

  private final WebpageService service;

  public WebpageHtmlController(WebpageService webpageService) {
    this.service = webpageService;
  }

  @Operation(summary = "Get a webpage as HTML")
  @GetMapping(
      value = {
        "/latest/webpages/{uuid}.html",
        "/v3/webpages/{uuid}.html",
        "/v2/webpages/{uuid}.html",
        "/v1/webpages/{uuid}.html"
      },
      produces = MediaType.TEXT_HTML_VALUE)
  public String getByUuidAsHtml(
      @Parameter(
              example = "",
              description =
                  "UUID of the webpage, e.g. <tt>599a120c-2dd5-11e8-b467-0ed5f89f718b</tt>")
          @PathVariable("uuid")
          UUID uuid,
      @Parameter(
              name = "pLocale",
              description =
                  "Desired locale, e.g. <tt>de_DE</tt>. If unset, contents in all languages will be returned")
          @RequestParam(name = "pLocale", required = false)
          Locale pLocale,
      @Parameter(
              name = "renderLabel",
              description =
                  "Switch for (de)activating rendering of webpage label. If 'false', label rendering will be skipped")
          @RequestParam(name = "renderLabel", required = false, defaultValue = "true")
          String renderLabel,
      Model model)
      throws ServiceException {
    Webpage webpage;
    if (pLocale == null) {
      webpage = service.getByExample(Webpage.builder().uuid(uuid).build());
    } else {
      webpage = service.getByExampleAndLocale(Webpage.builder().uuid(uuid).build(), pLocale);
      Locale returnedLocale = getLocale(webpage);
      model.addAttribute("locale", returnedLocale);
    }

    RenderingHints renderingHints = webpage.getRenderingHints();
    if (renderingHints != null) {
      renderingHints.setShowInPageNavigation(false);
    }

    model.addAttribute("renderLabel", Boolean.valueOf(renderLabel));
    model.addAttribute("webpage", webpage);
    return "webpage";
  }

  private Locale getLocale(Webpage webpage) {
    if (webpage == null) {
      return null;
    }
    Locale returnedLocale = webpage.getLabel().getLocales().iterator().next();
    return returnedLocale;
  }
}
