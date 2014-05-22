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

import net.codestory.simplelenium.misc.WebServer;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;

import static org.simpleframework.http.Status.NOT_FOUND;
import static org.simpleframework.http.Status.OK;

public class SimpleTest extends SeleniumTest {
  private WebServer webServer;

  @Before
  public void startWebServer() throws IOException {
    webServer = new WebServer((req, resp) -> {
      try {
        if ("/".equals(req.getPath().getPath())) {
          resp.setStatus(OK);
          resp.getPrintStream().print("<h1>Hello World</h1>");
        } else {
          resp.setStatus(NOT_FOUND);
          resp.getPrintStream().print("<h1>Page not found</h1>");
        }
        resp.close();
      } catch (IOException e) {
        throw new RuntimeException(e);
      }
    }).startOnRandomPort();
  }

  @After
  public void stopWebServer() throws IOException {
    webServer.stop();
  }

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
