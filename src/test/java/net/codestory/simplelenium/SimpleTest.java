package net.codestory.simplelenium;

import net.codestory.http.*;

import org.junit.*;

public class SimpleTest extends SeleniumTest {
  private WebServer webServer = new WebServer(routes -> routes.
      get("/", () -> "<h1>Hello World</h1>")
  ).startOnRandomPort();

  public String getDefaultBaseUrl() {
    return "http://localhost:" + webServer.port();
  }

  @Test
  public void page_not_found() {
    goTo("/unknown");

    find("h1").should().contain("Page not found");
  }

  @Test
  public void simple_page() {
    goTo("/");

    find("h1").should().contain("Hello World");
  }
}
