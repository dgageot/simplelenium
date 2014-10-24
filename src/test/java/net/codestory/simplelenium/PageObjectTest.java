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

public class PageObjectTest extends AbstractTest {
  private final ThePage page = createPage(ThePage.class);

  @Test
  public void simple_page() {
    goTo(page);

    page.verifyTitle();
    page.verifySubTitle();
    page.verifyBob("Bob", "42");
  }

  static class ThePage implements PageObject {
    DomElement h1 = find("h1");
    DomElement h4 = find("h4");
    DomElement name = find("#name");
    DomElement age = find(".age");

    @Override
    public String url() {
      return "/";
    }

    void verifyTitle() {
      h1.should().contain("Hello World");
      h1.should().exist();
    }

    void verifySubTitle() {
      h4.should().not().exist();
    }

    void verifyBob(String expectedName, String expectedAge) {
      name.should().contain(expectedName);
      age.should().contain(expectedAge);
    }
  }
}
