package net.codestory.simplelenium;

import org.junit.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class JavascriptTest extends AbstractTest {
  @Test
  public void executeBasicJavaScript() {
    goTo("/");

    assertThat((Long) executeJavascript("return 3+1")).isEqualTo(4);
  }

  @Test
  public void executeJavaScript() {
    goTo("/");

    assertThat((List) executeJavascript("return window.top.document.querySelectorAll('a')")).hasSize(2);
  }

  @Test
  public void execute_javaScript_with_arguments() {
    goTo("/");

    assertThat((Long) executeJavascript("return arguments[0]+arguments[1]", 3, 1)).isEqualTo(4);
  }
}
