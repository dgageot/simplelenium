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

import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class SimpleTest extends AbstractTest {
  @Test
  public void simple_page() {
    goTo("/");

    find("h1").should().contain("Hello World");
    find("#name").should().contain("Bob");
    find(".age").should().contain("42");
    find("ul li em").should().contain("italic");
    find("h1").should().exist();
    find("h4").should().not().exist();
  }

  @Test
  public void find_with_text() {
    goTo("/");

    find("h1").withText("Hello World").should().beDisplayed();
  }

  @Test
  public void click() {
    goTo("/");

    find("a").click();

    assertThat(currentUrl()).endsWith("/list");
  }

  @Test
  public void click_with_text() {
    goTo("/");

    find("a").withText("Show list").click();

    assertThat(currentUrl()).endsWith("/list");
  }
}
