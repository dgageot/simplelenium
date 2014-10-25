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

import org.openqa.selenium.WebElement;

import java.util.Objects;
import java.util.function.Predicate;

public class ElementFilter implements Predicate<WebElement> {
  private static final ElementFilter ANY = new ElementFilter("", element -> true);

  private final String description;
  private final Predicate<WebElement> predicate;

  private ElementFilter(String description, Predicate<WebElement> predicate) {
    this.description = description;
    this.predicate = predicate;
  }

  public static ElementFilter any() {
    return ANY;
  }

  public static ElementFilter withText(String text) {
    return new ElementFilter(" with text [" + text + "]", element -> Objects.equals(element.getText(), text));
  }

  public static ElementFilter withTagName(String name) {
    return new ElementFilter(" with tag name [" + name + "]", element -> Objects.equals(element.getTagName(), name));
  }

  public String getDescription() {
    return description;
  }

  @Override
  public boolean test(WebElement element) {
    return predicate.test(element);
  }
}
