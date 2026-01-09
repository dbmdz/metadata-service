package io.github.dbmdz.metadata.server.controller.semantic;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import de.digitalcollections.model.semantic.Tag;
import io.github.dbmdz.metadata.server.business.api.service.semantic.TagService;
import io.github.dbmdz.metadata.server.controller.BaseControllerTest;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;

@WebMvcTest(TagController.class)
@DisplayName("The TagController")
class TagControllerTest extends BaseControllerTest {

  @MockBean private TagService tagService;

  @DisplayName("can retrieve by non-encoded value")
  @ParameterizedTest
  @ValueSource(strings = {"/v6/tags/value/foobar"})
  public void findByUnencodedValue(String path) throws Exception {
    when(tagService.getByValue(any())).thenReturn(Tag.builder().build());
    testHttpGet(path);
    verify(tagService, times(1)).getByValue(eq("foobar"));
  }

  @DisplayName("can retrieve by base 64 encoded value with special characters")
  @ParameterizedTest
  @ValueSource(
      strings = {
        "/v6/tags/value/SURFTlRJRklFUjpIYW5kc2NocmlmdGVuLUlEOkJTQi1Ic3MgQW5hIDUwMC5CLiBTY2hsb8OfLCBKdWxpdXMuSQ"
      })
  public void findByBase64EncodedValueSpecial(String path) throws Exception {
    String encodedValue = path.split("/")[4];
    String decodedValue = "IDENTIFIER:Handschriften-ID:BSB-Hss Ana 500.B. Schloß, Julius.I";

    when(tagService.getByValue(eq(encodedValue))).thenReturn(null);
    when(tagService.getByValue(eq(decodedValue))).thenReturn(Tag.builder().build());
    testHttpGet(path);
    verify(tagService, times(1)).getByValue(eq(encodedValue));
    verify(tagService, times(1)).getByValue(eq(decodedValue));
  }

  @DisplayName("can retrieve by encoded value")
  @Test
  public void findByEncodedValue() throws Exception {
    final String encodedValue =
        Base64.getUrlEncoder()
            .withoutPadding()
            .encodeToString("foobar".getBytes(StandardCharsets.UTF_8));

    when(tagService.getByValue(eq(encodedValue))).thenReturn(null);
    when(tagService.getByValue(eq("foobar"))).thenReturn(Tag.builder().build());
    testHttpGet("/v6/tags/value/" + encodedValue);
    verify(tagService, times(1)).getByValue(eq(encodedValue));
    verify(tagService, times(1)).getByValue(eq("foobar"));
  }
}
