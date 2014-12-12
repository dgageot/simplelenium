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
import net.codestory.simplelenium.ShouldChain;
import net.codestory.simplelenium.text.Text;
import org.openqa.selenium.By;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.Point;
import org.openqa.selenium.WebElement;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static java.lang.String.join;
import static java.util.stream.Collectors.joining;
import static java.util.stream.Stream.of;
import static net.codestory.simplelenium.filters.WebElementHelper.text;
import static net.codestory.simplelenium.text.Text.plural;

class LazyShould implements ShouldChain {
  private final LazyDomElement element;
  private final Retry retry;
  private final boolean ok;

  LazyShould(LazyDomElement element, Retry retry, boolean ok) {
    this.element = element;
    this.retry = retry;
    this.ok = ok;
  }

  // Nested find

  public DomElement find(String selector) {
    if (element.parent() != null) {
      return element.parent().find(selector);
    }
    return new LazyDomElement(By.cssSelector(selector));
  }

  public DomElement find(By selector) {
    if (element.parent() != null) {
      return element.parent().find(selector);
    }
    return new LazyDomElement(selector);
  }

  // Modifiers

  @Override
  public LazyShould within(long duration, TimeUnit timeUnit) {
    return new LazyShould(element, new Retry(duration, timeUnit), ok);
  }

  @Override
  public LazyShould not() {
    return new LazyShould(element, retry, !ok);
  }

  @Override
  public LazyShould and() {
    return this; // For nicer fluent api
  }

  @Override
  public LazyShould should() {
    return this; // For nicer fluent api
  }

  // Expectations

  @Override
  public LazyShould contain(String... texts) {
    return verify(
      doesOrNot("contain") + " (" + join(";", texts) + ")",
      elements -> of(texts).allMatch(expected -> {
        return elements.stream().anyMatch(element -> text(element).contains(expected));
      }),
      elements -> "It contains " + statuses(elements, element -> text(element)));
  }

  @Override
  public LazyShould beEmpty() {
    return verify(
      isOrNot("empty"),
      elements -> !elements.isEmpty() && elements.stream().allMatch(element -> text(element).isEmpty()),
      elements -> "It contains " + statuses(elements, element -> text(element)));
  }

  @Override
  public LazyShould match(Pattern regexp) {
    return verify(
      doesOrNot("match") + " (" + regexp.pattern() + ")",
      elements -> !elements.isEmpty() && elements.stream().anyMatch(element -> regexp.matcher(text(element)).matches()),
      elements -> "It contains " + statuses(elements, element -> text(element)));
  }

  @Override
  public LazyShould beEnabled() {
    return verify(
      isOrNot("enabled"),
      elements -> !elements.isEmpty() && elements.stream().allMatch(element -> element.isEnabled()),
      elements -> "It is " + statuses(elements, element -> enabledStatus(element)));
  }

  @Override
  public LazyShould beDisplayed() {
    return verify(
      isOrNot("displayed"),
      elements -> !elements.isEmpty() && elements.stream().allMatch(element -> element.isDisplayed()),
      elements -> "It is " + statuses(elements, element -> displayedStatus(element)));
  }

  @Override
  public LazyShould beSelected() {
    return verify(
      isOrNot("selected"),
      elements -> !elements.isEmpty() && elements.stream().allMatch(element -> isSelected(element)),
      elements -> "It is " + statuses(elements, element -> selectedStatus(element)));
  }

  @Override
  public LazyShould haveLessItemsThan(int maxCount) {
    return verify(
      doesOrNot("contain") + " less than " + plural(maxCount, "element"),
      elements -> elements.size() < maxCount,
      elements -> "It contains " + plural(elements.size(), "element"));
  }

  @Override
  public LazyShould haveSize(int size) {
    return verify(
      doesOrNot("contain") + " " + plural(size, "element"),
      elements -> elements.size() == size,
      elements -> "It contains " + plural(elements.size(), "element"));
  }

  @Override
  public LazyShould haveMoreItemsThan(int minCount) {
    return verify(
      doesOrNot("contain") + " more than " + plural(minCount, "element"),
      elements -> elements.size() > minCount,
      elements -> "It contains " + plural(elements.size(), "element"));
  }

  @Override
  public LazyShould exist() {
    return verify(
      doesOrNot("exist"),
      elements -> !elements.isEmpty(),
      elements -> "It contains " + plural(elements.size(), "element"));
  }

  @Override
  public LazyShould haveDimension(int width, int height) {
    return verify(
      hasOrNot("dimension"),
      elements -> !elements.isEmpty() && elements.stream().allMatch(element -> hasDimension(element, width, height)),
      elements -> "It measures " + statuses(elements, element -> dimension(element)));
  }

  @Override
  public LazyShould beAtLocation(int x, int y) {
    return verify(
      isOrNot("at location"),
      elements -> !elements.isEmpty() && elements.stream().allMatch(element -> hasLocation(element, x, y)),
      elements -> "It is at location " + statuses(elements, element -> location(element)));
  }

  @Override
  public LazyShould match(Predicate<WebElement> condition) {
    return verify(
      doesOrNot("match") + " (" + condition + ")",
      elements -> !elements.isEmpty() && elements.stream().allMatch(condition),
      elements -> "It is  " + statuses(elements, element -> Boolean.toString(condition.test(element))));
  }

  private LazyShould verify(String message, Predicate<List<WebElement>> predicate, Function<List<WebElement>, String> toErrorMessage) {
    String verification = "verify that " + element + " " + message;
    System.out.println("   -> " + verification);

    try {
      if (!retry.verify(() -> findElements(), ok ? predicate : predicate.negate())) {
        throw Failure.create("Failed to " + verification + ". " + toErrorMessage.apply(findElements()));
      }
    } catch (NoSuchElementException e) {
      throw Failure.create("Element not found. Failed to " + verification);
    }

    return ok ? this : not();
  }

  // Internal

  private List<WebElement> findElements() {
    return element.stream().collect(Collectors.toList());
  }

  private String doesOrNot(String verb) {
    return Text.doesOrNot(!ok, verb);
  }

  private String isOrNot(String state) {
    return Text.isOrNot(!ok, state);
  }

  private String hasOrNot(String what) {
    return Text.hasOrNot(!ok, what);
  }

  private static boolean hasDimension(WebElement element, int width, int height) {
    Dimension dimension = element.getSize();
    return dimension.getWidth() == width && dimension.getHeight() == height;
  }

  private static boolean hasLocation(WebElement element, int x, int y) {
    Point location = element.getLocation();
    return location.getX() == x && location.getY() == y;
  }

  private static boolean isSelected(WebElement element) {
    return isSelectable(element) && element.isSelected();
  }

  private static String statuses(List<WebElement> elements, Function<WebElement, String> toStatus) {
    return elements.stream().map(toStatus).collect(joining(";", "(", ")"));
  }

  private static String enabledStatus(WebElement element) {
    return element.isEnabled() ? "enabled" : "not enabled";
  }

  private static String displayedStatus(WebElement element) {
    return element.isDisplayed() ? "displayed" : "not displayed";
  }

  private static String dimension(WebElement element) {
    return element.getSize().toString();
  }

  private static String location(WebElement element) {
    return element.getLocation().toString();
  }

  private static String selectedStatus(WebElement element) {
    return isSelectable(element) ? element.isSelected() ? "selected" : "not selected" : "not selectable";
  }

  private static boolean isSelectable(WebElement element) {
    return element.getTagName().equals("input") || element.getTagName().equals("option");
  }
}
