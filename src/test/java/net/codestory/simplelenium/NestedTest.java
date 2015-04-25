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

import org.junit.Test;

public class NestedTest extends AbstractTest {
  @Test
  public void nested_find() {
    goTo("/nested");

    find("#first .child").should().haveSize(1).contain("First Child");
    find("#second .child").should().haveSize(1).contain("Second Child");
    find("#first").find(".child").should().haveSize(1).contain("First Child");
    find("#second").find(".child").should().haveSize(1).contain("Second Child");
  }

  @Test
  public void cascade_find() {
    goTo("/nested");

    find("#third")
      .find(".first_child").should().haveSize(1).contain("First Child")
      .find(".second_child").should().haveSize(1).contain("Second Child");
  }

  TheSection theSection;

  static class TheSection implements SectionObject {
    DomElement third;
  }

  @Test
  public void cascade_find_with_section_object() {
    goTo("/nested");

    theSection.third
      .find(".first_child").should().haveSize(1).contain("First Child")
      .find(".second_child").should().haveSize(1).contain("Second Child");
  }

  ThePage thePage;

  static class ThePage implements PageObject {
    @Override
    public String url() {
      return "/nested";
    }

    DomElement third;
  }

  @Test
  public void cascade_find_with_page_object() {
    goTo(thePage);

    thePage.third
      .find(".first_child").should().haveSize(1).contain("First Child")
      .find(".second_child").should().haveSize(1).contain("Second Child");
  }
}
