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
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.util.regex.Pattern;

import static java.util.concurrent.TimeUnit.MILLISECONDS;

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
        "<ul>" +
          "<li class='name'>Bob Morane</li>" +
          "<li class='name'>Joe l'Indien</li>" +
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

  @Rule
  public ExpectedException thrown = ExpectedException.none();

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

    find(".name").should().contain("Bob Morane", "Joe l'Indien");
    find(".name").should().beDisplayed();
    find(".name").should().haveSize(2);
    find(".name").should().haveLessItemsThan(3);
    find(".name").should().haveMoreItemsThan(1);
    find(".name").should().not().beEmpty();
    find(".name").should().not().contain("Casper", "Zorro");
    find(".name").should().match(Pattern.compile("([a-zA-Z ]+)"));
  }

  @Test
  public void list_with_verification_chaining() {
    goTo("/list");

    find(".name").should()
      .contain("Bob Morane", "Joe l'Indien")
      .beDisplayed()
      .haveSize(2)
      .haveLessItemsThan(3)
      .haveMoreItemsThan(1)
      .not().beEmpty()
      .not().contain("Casper", "Zorro")
      .match(Pattern.compile("([a-zA-Z ]+)"));
  }

  @Test
  public void fail_on_contains() {
    goTo("/list");

    expectAssertionError("Failed to verify that .name contains (Wrong name). It contains (Bob Morane;Joe l'Indien)");

    find(".name").shouldWithin(1, MILLISECONDS).contain("Wrong name");
  }

  @Test
  public void fail_on_matches() {
    goTo("/list");

    expectAssertionError("Failed to verify that .name matches (a*). It contains (Bob Morane;Joe l'Indien)");

    find(".name").shouldWithin(1, MILLISECONDS).match(Pattern.compile("a*"));
  }

  @Test
  public void fail_on_size() {
    goTo("/list");

    expectAssertionError("Failed to verify that .name contains 1 element. It contains 2 elements");

    find(".name").shouldWithin(1, MILLISECONDS).haveSize(1);
  }

  @Test
  public void fail_on_size_less_than() {
    goTo("/list");

    expectAssertionError("Failed to verify that .name contains less than 0 element. It contains 2 elements");

    find(".name").shouldWithin(1, MILLISECONDS).haveLessItemsThan(0);
  }

  @Test
  public void fail_on_size_more_than() {
    goTo("/list");

    expectAssertionError("Failed to verify that .name contains more than 10 elements. It contains 2 elements");

    find(".name").shouldWithin(1, MILLISECONDS).haveMoreItemsThan(10);
  }

  @Test
  public void fail_on_empty() {
    goTo("/list");

    expectAssertionError("Failed to verify that .name is empty. It contains 2 elements");

    find(".name").shouldWithin(1, MILLISECONDS).beEmpty();
  }

  @Test
  public void fail_on_enabled() {
    goTo("/list");

    expectAssertionError("Failed to verify that .name is not enabled. It is (enabled;enabled)");

    find(".name").shouldWithin(1, MILLISECONDS).not().beEnabled();
  }

  @Test
  public void fail_on_displayed() {
    goTo("/list");

    expectAssertionError("Failed to verify that .name is not displayed. It is (displayed;displayed)");

    find(".name").shouldWithin(1, MILLISECONDS).not().beDisplayed();
  }

  @Test
  public void fail_on_selected() {
    goTo("/list");

    expectAssertionError("Failed to verify that .name is selected. It is (not selectable;not selectable)");

    find(".name").shouldWithin(1, MILLISECONDS).beSelected();
  }

  @Test
  public void find_with_text() {
    goTo("/list");

    find("h1").withText("Page not found").should().beDisplayed();
  }

  private void expectAssertionError(String message) {
    thrown.expect(AssertionError.class);
    thrown.expectMessage(message);
  }
}
