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

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class JavascriptTest extends AbstractTest {
  @Before
  public void goToIndex() {
    goTo("/");
  }

  @Test
  public void basic_javascript() {
    assertThat((Long) executeJavascript("return 3+1")).isEqualTo(4);
  }

  @Test
  public void javascript() {
    assertThat((List) executeJavascript("return window.top.document.querySelectorAll('a')")).hasSize(2);
  }

  @Test
  public void arguments() {
    assertThat((Long) executeJavascript("return arguments[0]+arguments[1]", 3, 1)).isEqualTo(4);
  }
}
