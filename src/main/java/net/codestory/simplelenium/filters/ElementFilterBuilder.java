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
package net.codestory.simplelenium.filters;

import net.codestory.simplelenium.DomElement;
import org.openqa.selenium.WebElement;

import java.util.Objects;
import java.util.function.Function;
import java.util.function.Predicate;

public class ElementFilterBuilder {
  private final DomElement domElement;
  private final String description;
  private final Function<WebElement, String> toValue;

  public ElementFilterBuilder(DomElement domElement, String description, Function<WebElement, String> toValue) {
    this.domElement = domElement;
    this.description = description;
    this.toValue = toValue;
  }

  // Matchers

  public DomElement equalsTo(String text) {
    return build(" with " + description + "=[" + text + "]", element -> Objects.equals(toValue.apply(element), text));
  }

  public DomElement contains(String text) {
    return build(" with " + description + " contains[" + text + "]", element -> toValue.apply(element).contains(text));
  }

  // Internal

  private DomElement build(String description, Predicate<WebElement> predicate) {
    return domElement.with(new ElementFilter(description, predicate));
  }
}
