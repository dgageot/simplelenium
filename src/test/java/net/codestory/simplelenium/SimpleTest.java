/**
 * Copyright (C) 2013 all@code-story.net
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License
 */
package net.codestory.simplelenium;

import net.codestory.http.WebServer;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

public class SimpleTest extends SeleniumTest {
  private static WebServer webServer;

  @BeforeClass
  public static void startWebServer() {
    webServer = new WebServer(routes -> routes
      .get("/",
        "<h1>Hello World</h1>" +
          "<div id='name'>Bob</div>" +
          "<div class='age'>42</div>" +
          "<ul><li><em>italic</em></li></ul>")
      .get("/list",
        "<ul class='names'>" +
          "<li>Bob Morane</li>" +
          "<li>Joe l'Indien</li>" +
          "</ul>"))
      .startOnRandomPort();
  }

  protected String getDefaultBaseUrl() {
    return "http://localhost:" + webServer.port();
  }

  @AfterClass
  public static void stopWebServer() {
    webServer.stop();
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
    find("#name").should().contain("Bob");
    find(".age").should().contain("42");
    find("ul li em").should().contain("italic");
  }

  @Test
  public void list() {
    goTo("/list");

    find(".names").should().contain("Bob Morane", "Joe l'Indien");
  }
}
