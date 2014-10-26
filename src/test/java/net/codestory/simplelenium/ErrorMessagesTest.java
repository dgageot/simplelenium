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

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.util.regex.Pattern;

import static java.util.concurrent.TimeUnit.MILLISECONDS;
import static org.junit.rules.ExpectedException.none;

public class ErrorMessagesTest extends AbstractTest {
  @Rule
  public ExpectedException thrown = none();

  @Test
  public void fail_on_contains() {
    goTo("/list");

    expectError("Failed to verify that .name contains (Wrong name). It contains (Bob Morane;Joe l'Indien)");

    find(".name").should().within(1, MILLISECONDS).contain("Wrong name");
  }

  @Test
  public void fail_on_matches() {
    goTo("/list");

    expectError("Failed to verify that .name matches (a*). It contains (Bob Morane;Joe l'Indien)");

    find(".name").should().within(1, MILLISECONDS).match(Pattern.compile("a*"));
  }

  @Test
  public void fail_on_size() {
    goTo("/list");

    expectError("Failed to verify that .name contains 1 element. It contains 2 elements");

    find(".name").should().within(1, MILLISECONDS).haveSize(1);
  }

  @Test
  public void fail_on_size_less_than() {
    goTo("/list");

    expectError("Failed to verify that .name contains less than 0 element. It contains 2 elements");

    find(".name").should().within(1, MILLISECONDS).haveLessItemsThan(0);
  }

  @Test
  public void fail_on_size_more_than() {
    goTo("/list");

    expectError("Failed to verify that .name contains more than 10 elements. It contains 2 elements");

    find(".name").should().within(1, MILLISECONDS).haveMoreItemsThan(10);
  }

  @Test
  public void fail_on_empty() {
    goTo("/list");

    expectError("Failed to verify that .name is empty. It contains 2 elements");

    find(".name").should().within(1, MILLISECONDS).beEmpty();
  }

  @Test
  public void fail_on_not_exists() {
    goTo("/list");

    expectError("Failed to verify that .name doesn't exist. It contains 2 elements");

    find(".name").should().within(1, MILLISECONDS).not().exist();
  }

  @Test
  public void fail_on_exists() {
    goTo("/list");

    expectError("Failed to verify that .unknown exists. It contains 0 element");

    find(".unknown").should().within(1, MILLISECONDS).exist();
  }

  @Test
  public void fail_on_enabled() {
    goTo("/list");

    expectError("Failed to verify that .name is not enabled. It is (enabled;enabled)");

    find(".name").should().within(1, MILLISECONDS).not().beEnabled();
  }

  @Test
  public void fail_on_displayed() {
    goTo("/list");

    expectError("Failed to verify that .name is not displayed. It is (displayed;displayed)");

    find(".name").should().within(1, MILLISECONDS).not().beDisplayed();
  }

  @Test
  public void fail_on_selected() {
    goTo("/list");

    expectError("Failed to verify that .name is selected. It is (not selectable;not selectable)");

    find(".name").should().within(1, MILLISECONDS).beSelected();
  }

  @Test
  public void fail_with_filter_on_text() {
    goTo("/list");

    expectError("Failed to verify that .name with text that contains [Any Text] exists. It contains 0 element");

    find(".name").withText("Any Text").should().within(1, MILLISECONDS).exist();
  }

  private void expectError(String message) {
    thrown.expect(AssertionError.class);
    thrown.expectMessage(message);
  }
}
