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

public class SelectTest extends AbstractTest {
  @Test
  public void find_selected_option_by_value() {
    goTo("/select");

    find("select").should().beDisplayed();
    find("select option").withAttribute("value").equalTo("1").should().not().beSelected();
    find("select option").withAttribute("value").equalTo("2").should().beSelected();
    find("select option").withAttribute("value").equalTo("3").should().not().beSelected();
  }

  @Test
  public void find_selected_option_by_text() {
    goTo("/select");

    find("select").should().beDisplayed();
    find("select option").withText("FIRST").should().not().beSelected();
    find("select option").withText("SECOND").should().beSelected();
    find("select option").withText("THIRD").should().not().beSelected();
  }

  @Test
  public void change_selection() {
    goTo("/select");

    find("select").selectByValue("1");
    find("select option").withText("FIRST").should().beSelected();
    find("select option").withText("SECOND").should().not().beSelected();
    find("select option").withText("THIRD").should().not().beSelected();

    find("select").selectByValue("2");
    find("select option").withText("FIRST").should().not().beSelected();
    find("select option").withText("SECOND").should().beSelected();
    find("select option").withText("THIRD").should().not().beSelected();

    find("select").selectByValue("3");
    find("select option").withText("FIRST").should().not().beSelected();
    find("select option").withText("SECOND").should().not().beSelected();
    find("select option").withText("THIRD").should().beSelected();
  }
}
