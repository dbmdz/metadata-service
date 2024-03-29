package io.github.dbmdz.metadata.server.controller.identifiable.web;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import de.digitalcollections.model.identifiable.entity.Website;
import de.digitalcollections.model.identifiable.web.Webpage;
import io.github.dbmdz.metadata.server.business.api.service.LocaleService;
import io.github.dbmdz.metadata.server.business.api.service.identifiable.web.WebpageService;
import io.github.dbmdz.metadata.server.controller.BaseControllerTest;
import java.util.Locale;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;

@WebMvcTest(WebpageController.class)
@DisplayName("The WebpageController")
class WebpageControllerTest extends BaseControllerTest {

  @MockBean private LocaleService localeService;
  @MockBean private WebpageService webpageService;

  @DisplayName("returns the website metadata")
  @ParameterizedTest
  @ValueSource(
      strings = {
        "/v3/webpages/6d52141c-5c5d-48b4-aee8-7df5404d245e/website",
        "/latest/webpages/6d52141c-5c5d-48b4-aee8-7df5404d245e/website"
      })
  public void pagedRootpages(String path) throws Exception {
    Website expected =
        Website.builder()
            .label(Locale.GERMAN, "MDZ Homepage Relaunch")
            .uuid("7a2f1935-c5b8-40fb-8622-c675de0a6242")
            .refId(29)
            .build();

    when(webpageService.getWebsite(any(Webpage.class))).thenReturn(expected);

    testJson(path);
  }
}
