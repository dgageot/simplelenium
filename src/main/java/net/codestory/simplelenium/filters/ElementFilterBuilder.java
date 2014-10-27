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

import net.codestory.simplelenium.FilteredDomElement;
import org.openqa.selenium.WebElement;

import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.UnaryOperator;
import java.util.regex.Pattern;
import java.util.stream.Stream;

class ElementFilterBuilder implements FilteredDomElement {
  private final LazyDomElement domElement;
  private final String description;
  private final Function<WebElement, String> toValue;
  private final boolean ok;

  ElementFilterBuilder(LazyDomElement domElement, String description, Function<WebElement, String> toValue, boolean ok) {
    this.domElement = domElement;
    this.description = description;
    this.toValue = toValue;
    this.ok = ok;
  }

  // Modifiers

  @Override
  public ElementFilterBuilder not() {
    return new ElementFilterBuilder(domElement, description, toValue, !ok);
  }

  // Matchers

  @Override
  public LazyDomElement equalsTo(String text) {
    return build("is equal to", text, StringPredicates.equalsTo(text));
  }

  @Override
  public LazyDomElement contains(String text) {
    return build("contains", text, StringPredicates.contains(text));
  }

  @Override
  public LazyDomElement contains(Pattern regex) {
    return build("contains", regex, StringPredicates.contains(regex));
  }

  @Override
  public LazyDomElement containsWord(String word) {
    return build("has word", word, StringPredicates.containsWord(word));
  }

  @Override
  public LazyDomElement startsWith(String text) {
    return build("starts with", text, StringPredicates.startsWith(text));
  }

  @Override
  public LazyDomElement endsWith(String text) {
    return build("ends with", text, StringPredicates.endsWith(text));
  }

  @Override
  public LazyDomElement matches(Pattern regex) {
    return build("matches", regex, StringPredicates.matches(regex));
  }

  @Override
  public LazyDomElement matches(Predicate<String> predicate) {
    return build("matches", predicate, predicate);
  }

  // Internal

  private LazyDomElement build(String word, Object details, Predicate<String> predicate) {
    String fullDescription = " with " + description + " that " + word + " [" + details + "]";

    UnaryOperator<Stream<WebElement>> filter = stream -> stream.filter(element -> (ok == predicate.test(toValue.apply(element))));

    return domElement.with(new ElementFilter(fullDescription, filter));
  }
}
