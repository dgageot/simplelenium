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

import java.util.function.UnaryOperator;
import java.util.stream.Stream;

import static java.util.function.UnaryOperator.identity;

class ElementFilter {
  private static final ElementFilter ANY = new ElementFilter("", identity());

  private final String description;
  private final UnaryOperator<Stream<WebElement>> filter;

  ElementFilter(String description, UnaryOperator<Stream<WebElement>> filter) {
    this.description = description;
    this.filter = filter;
  }

  public String getDescription() {
    return description;
  }

  public UnaryOperator<Stream<WebElement>> getFilter() {
    return filter;
  }

  public static ElementFilter any() {
    return ANY;
  }

  public ElementFilter and(ElementFilter second) {
    if (ANY == this) {
      return second;
    }
    if (ANY == second) {
      return this;
    }
    return new ElementFilter(description + ',' + second.description, stream -> second.filter.apply(filter.apply(stream)));
  }
}
