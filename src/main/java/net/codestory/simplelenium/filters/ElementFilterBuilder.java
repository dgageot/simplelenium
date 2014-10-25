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

import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.UnaryOperator;
import java.util.regex.Pattern;
import java.util.stream.Stream;

public class ElementFilterBuilder {
  private final DomElement domElement;
  private final String description;
  private final Function<WebElement, String> toValue;
  private final Predicate<Boolean> ok;

  public ElementFilterBuilder(DomElement domElement, String description, Function<WebElement, String> toValue, Predicate<Boolean> ok) {
    this.domElement = domElement;
    this.description = description;
    this.toValue = toValue;
    this.ok = ok;
  }

  // Modifiers

  public ElementFilterBuilder not() {
    return new ElementFilterBuilder(domElement, description, toValue, ok.negate());
  }

  // Matchers

  public DomElement equalsTo(String text) {
    return build("=[" + text + "]", value -> value.equals(text));
  }

  public DomElement contains(String text) {
    return build(" contains[" + text + "]", value -> value.contains(text));
  }

  public DomElement contains(Pattern regex) {
    return build(" contains[" + regex + "]", value -> regex.matcher(value).find());
  }

  public DomElement containsWord(String word) {
    Pattern pattern = patternForWord(word);
    return build(" has word[" + word + "]", value -> pattern.matcher(value).find());
  }

  public DomElement startsWith(String text) {
    return build(" startsWith[" + text + "]", value -> value.startsWith(text));
  }

  public DomElement endsWith(String text) {
    return build(" endsWith[" + text + "]", value -> value.endsWith(text));
  }

  public DomElement matches(Pattern regex) {
    return build(" matches[" + regex + "]", value -> regex.matcher(value).matches());
  }

  public DomElement matches(Predicate<String> predicate) {
    return build(" matches[" + predicate + "]", predicate);
  }

  // Internal

  static Pattern patternForWord(String word) {
    return Pattern.compile("\\b(" + word + ")\\b");
  }

  private DomElement build(String details, Predicate<String> predicate) {
    UnaryOperator<Stream<WebElement>> filter = stream -> stream.filter(element -> ok.test(predicate.test(toValue.apply(element))));
    return domElement.with(new ElementFilter(" with " + description + details, filter));
  }
}
