/**
 * Copyright (C) 2013-2015 all@code-story.net
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
import net.codestory.http.misc.Env;
import net.codestory.http.routes.Routes;

public class TestWebServer {
  private final static WebServer serverStartedOnlyOnce = new WebServer() {
      @Override
      protected Env createEnv() {
          return Env.prod();
      }
  }.configure(TestWebServer::configure).startOnRandomPort();

  public int port() {
    return serverStartedOnlyOnce.port();
  }

  private static void configure(Routes routes) {
    routes.get("/",
        "<h1>Hello World</h1>" +
        "<h2>SubTitle</h2>" +
        "<div id='name'>Bob</div>" +
        "<div class='age'>42</div>" +
        "<span name='qualifiers[]'></span>" +
        "<ul>" +
        "   <li><em class='item'>italic</em></li>" +
        "</ul>" +
        "<a href='/'>First Link</a>" +
        "<a href='/list'>Second Link</a>"
    );

    routes.get("/list",
      "<h1>Hello World</h1>" +
      "<ul>" +
        "   <li id='bob' name='theBob' class='name man cartoon'>Bob Morane</li>" +
        "   <li id='joe' name='theJoe' class='name man tv'>Joe l'Indien</li>" +
      "</ul>"
    );

    routes.get("/form",
      "<input id=\"name\" name=\"name\" type='text' value='The Name'>" +
      "<input id=\"city\" name=\"city\" type='text' value='The City'>" +
      "<input id=\"the_field_with_a_long_name\" type='text' value=''>" +
      "<input id=\"the.field.with.a.long.name\" type='text' value=''>"
    );

    routes.get("/nested",
      "<div id='first'><div class='child'>First Child</div></div>" +
      "<div id='second'><div class='child'>Second Child</div></div>" +
      "<div id='third'>" +
        "<div class='first_child'>First Child</div>" +
        "<div class='second_child'>Second Child</div>" +
      "</div>"
    );

    routes.get("/console",
      "<script>console.log('Hello World');</script>"
    );

    routes.get("/select",
      "<select>" +
        "<option value=\"1\">FIRST</option>" +
        "<option value=\"2\" selected>SECOND</option>" +
        "<option value=\"3\">THIRD</option>" +
      "</select>"
    );
  }
}
