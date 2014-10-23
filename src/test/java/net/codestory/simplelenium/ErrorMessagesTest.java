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

import net.codestory.http.routes.Routes;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.util.regex.Pattern;

import static java.util.concurrent.TimeUnit.MILLISECONDS;
import static org.junit.rules.ExpectedException.none;

public class ErrorMessagesTest extends AbstractTest {
  @Rule
  public ExpectedException thrown = none();

  @Override
  protected void configureTestServer(Routes routes) {
    routes.get("/",
      "<ul>" +
        "   <li class='name'>Bob Morane</li>" +
        "   <li class='name'>Joe l'Indien</li>" +
        "</ul>");
  }

  @Test
  public void fail_on_contains() {
    goTo("/");

    expectError("Failed to verify that .name contains (Wrong name). It contains (Bob Morane;Joe l'Indien)");

    find(".name").shouldWithin(1, MILLISECONDS).contain("Wrong name");
  }

  @Test
  public void fail_on_matches() {
    goTo("/");

    expectError("Failed to verify that .name matches (a*). It contains (Bob Morane;Joe l'Indien)");

    find(".name").shouldWithin(1, MILLISECONDS).match(Pattern.compile("a*"));
  }

  @Test
  public void fail_on_size() {
    goTo("/");

    expectError("Failed to verify that .name contains 1 element. It contains 2 elements");

    find(".name").shouldWithin(1, MILLISECONDS).haveSize(1);
  }

  @Test
  public void fail_on_size_less_than() {
    goTo("/");

    expectError("Failed to verify that .name contains less than 0 element. It contains 2 elements");

    find(".name").shouldWithin(1, MILLISECONDS).haveLessItemsThan(0);
  }

  @Test
  public void fail_on_size_more_than() {
    goTo("/");

    expectError("Failed to verify that .name contains more than 10 elements. It contains 2 elements");

    find(".name").shouldWithin(1, MILLISECONDS).haveMoreItemsThan(10);
  }

  @Test
  public void fail_on_empty() {
    goTo("/");

    expectError("Failed to verify that .name is empty. It contains 2 elements");

    find(".name").shouldWithin(1, MILLISECONDS).beEmpty();
  }

  @Test
  public void fail_on_not_exists() {
    goTo("/");

    expectError("Failed to verify that .name doesn't exist. It contains 2 elements");

    find(".name").shouldWithin(1, MILLISECONDS).not().exist();
  }

  @Test
  public void fail_on_exists() {
    goTo("/");

    expectError("Failed to verify that .unknown exists. It contains 0 element");

    find(".unknown").shouldWithin(1, MILLISECONDS).exist();
  }

  @Test
  public void fail_on_enabled() {
    goTo("/");

    expectError("Failed to verify that .name is not enabled. It is (enabled;enabled)");

    find(".name").shouldWithin(1, MILLISECONDS).not().beEnabled();
  }

  @Test
  public void fail_on_displayed() {
    goTo("/");

    expectError("Failed to verify that .name is not displayed. It is (displayed;displayed)");

    find(".name").shouldWithin(1, MILLISECONDS).not().beDisplayed();
  }

  @Test
  public void fail_on_selected() {
    goTo("/");

    expectError("Failed to verify that .name is selected. It is (not selectable;not selectable)");

    find(".name").shouldWithin(1, MILLISECONDS).beSelected();
  }

  private void expectError(String message) {
    thrown.expect(AssertionError.class);
    thrown.expectMessage(message);
  }
}
