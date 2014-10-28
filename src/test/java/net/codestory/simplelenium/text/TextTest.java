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
package net.codestory.simplelenium.text;

import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class TextTest {
  @Test
  public void does() {
    assertThat(Text.doesOrNot(false, "start")).isEqualTo("starts");
    assertThat(Text.doesOrNot(false, "match")).isEqualTo("matches");
    assertThat(Text.doesOrNot(false, "start with")).isEqualTo("starts with");
  }

  @Test
  public void doesnt() {
    assertThat(Text.doesOrNot(true, "start")).isEqualTo("doesn't start");
    assertThat(Text.doesOrNot(true, "match")).isEqualTo("doesn't match");
    assertThat(Text.doesOrNot(true, "start with")).isEqualTo("doesn't start with");
  }
}