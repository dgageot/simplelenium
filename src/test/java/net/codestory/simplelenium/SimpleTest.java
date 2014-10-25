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

    find("h1").withText("Hello World").should().exist();
    find("h1").withText("UNKNOWN").should().beEmpty();

    find("h1").withText().not().contains("UNKNOWN").should().exist();

    find("h1").withText().equalsTo("Hello World").should().exist();
    find("h1").withText().equalsTo("UNKNOWN").should().beEmpty();
  }

  @Test
  public void find_with_tag_name() {
    goTo("/");

    find("ul li .item").withTagName("em").should().exist();
    find("ul li .item").withTagName("object").should().beEmpty();

    find("ul li .item").withTagName().equalsTo("em").should().exist();
    find("ul li .item").withTagName().equalsTo("object").should().beEmpty();
  }
}
