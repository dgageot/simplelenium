/**
 * Copyright (C) 2013-2014 all@code-story.net
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
import net.codestory.http.routes.Routes;

public class TestWebServer {
  private final static WebServer serverStartedOnlyOnce;

  static {
    serverStartedOnlyOnce = new WebServer(TestWebServer::configure).startOnRandomPort();
  }

  public int port() {
    return serverStartedOnlyOnce.port();
  }

  private static void configure(Routes routes) {
    routes
      .get("/",
        "<h1>Hello World</h1>" +
          "<div id='name'>Bob</div>" +
          "<div class='age'>42</div>" +
          "<ul><li><em class='item'>italic</em></li></ul>" +
          "<a href='/'>First Link</a>" +
          "<a href='/list'>Second Link</a>"
      )
      .get("/list",
        "<ul>" +
          "   <li class='name'>Bob Morane</li>" +
          "   <li class='name'>Joe l'Indien</li>" +
          "</ul>"
      );
  }
}
