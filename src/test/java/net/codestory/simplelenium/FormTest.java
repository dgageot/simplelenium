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

import org.junit.Before;
import org.junit.Test;

public class FormTest extends AbstractTest {
  @Before
  public void goToForm() {
    goTo("/form");
  }

  @Test
  public void input_text() {
    find("input#name").should().contain("The Name");
    find("input#name").withText("The Name").should().exist();

    find("input#city").should().contain("The City");
    find("input#city").withText().containing("City").should().exist();
  }

  @Test
  public void find_by_id() {
    find("#name").should().contain("The Name");
    find("#city").should().contain("The City");
  }

  @Test
  public void find_by_name() {
    find("name").should().contain("The Name");
    find("city").should().contain("The City");
  }

  @Test
  public void find_by_tag_name() {
    find("input").should().contain("The Name", "The City");
  }

  @Test
  public void clear() {
    find("input#name").clear();

    find("input#name").should().beEmpty();
  }

  @Test
  public void chain() {
    find("input#name").fill("name")
    .find("input#city").fill("Paris")
    .find("input#name").should().contain("The Name")
    .find("input#city").should().contain("Paris");
  }
}
