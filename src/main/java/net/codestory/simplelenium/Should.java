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

import com.gargoylesoftware.htmlunit.html.HtmlInput;
import com.gargoylesoftware.htmlunit.html.HtmlOption;
import net.codestory.simplelenium.text.Text;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.regex.Pattern;
import java.util.stream.*;

import static java.lang.String.join;
import static java.util.stream.Collectors.joining;
import static java.util.stream.Stream.of;
import static net.codestory.simplelenium.text.Text.plural;

public class Should {
  private final WebDriver driver;
  private final By selector;
  private final Predicate<WebElement> narrowSelection;
  private final Retry retry;
  private final boolean not;

  Should(WebDriver driver, By selector, Predicate<WebElement> narrowSelection, long duration, TimeUnit timeUnit) {
    this(driver, selector, narrowSelection, new Retry(duration, timeUnit), false);
  }

  private Should(WebDriver driver, By selector, Predicate<WebElement> narrowSelection, Retry retry, boolean not) {
    this.driver = driver;
    this.selector = selector;
    this.narrowSelection = narrowSelection;
    this.retry = retry;
    this.not = not;
  }

  public Should not() {
    return new Should(driver, selector, narrowSelection, retry, !not);
  }

  public Should contain(String... texts) {
    return verify(
      doesOrNot("contain") + " (" + join(";", texts) + ")",
      elements -> of(texts).allMatch(expected -> {
        return elements.stream().anyMatch(element -> element.getText().contains(expected));
      }),
      elements -> "It contains " + statuses(elements, element -> element.getText()));
  }

  public Should match(Pattern regexp) {
    return verify(
      doesOrNot("match") + " (" + regexp.pattern() + ")",
      elements -> elements.stream().anyMatch(element -> regexp.matcher(element.getText()).matches()),
      elements -> "It contains " + statuses(elements, element -> element.getText()));
  }

  public Should beEnabled() {
    return verify(
      isOrNot("enabled"),
      elements -> elements.stream().allMatch(element -> element.isEnabled()),
      elements -> "It is " + statuses(elements, element -> enabledStatus(element)));
  }

  public Should beDisplayed() {
    return verify(
      isOrNot("displayed"),
      elements -> elements.stream().allMatch(element -> element.isDisplayed()),
      elements -> "It is " + statuses(elements, element -> displayedStatus(element)));
  }

  public Should beSelected() {
    return verify(
      isOrNot("selected"),
      elements -> elements.stream().allMatch(element -> isSelected(element)),
      elements -> "It is " + statuses(elements, element -> selectedStatus(element)));
  }

  public Should haveLessItemsThan(int maxCount) {
    return verify(
      doesOrNot("contain") + " less than " + plural(maxCount, "element"),
      elements -> elements.size() < maxCount,
      elements -> "It contains " + plural(elements.size(), "element"));
  }

  public Should haveSize(int size) {
    return verify(
      doesOrNot("contain") + " " + plural(size, "element"),
      elements -> elements.size() == size,
      elements -> "It contains " + plural(elements.size(), "element"));
  }

  public Should haveMoreItemsThan(int minCount) {
    return verify(
      doesOrNot("contain") + " more than " + plural(minCount, "element"),
      elements -> elements.size() > minCount,
      elements -> "It contains " + plural(elements.size(), "element"));
  }

  public Should beEmpty() {
    return verify(
      isOrNot("empty"),
      elements -> elements.isEmpty(),
      elements -> "It contains " + plural(elements.size(), "element"));
  }

  public Should exist() {
    return verify(
      doesOrNot("exist"),
      elements -> !elements.isEmpty(),
      elements -> "It contains " + plural(elements.size(), "element"));
  }

  private Should verify(String message, Predicate<List<WebElement>> predicate, Function<List<WebElement>, String> toErrorMessage) {
    String verification = "verify that " + toString(selector) + " " + message;
    System.out.println("   -> " + verification);

    try {
      if (!retry.verify(() -> findElements(), not ? predicate.negate() : predicate)) {
        throw new AssertionError("Failed to " + verification + ". " + toErrorMessage.apply(findElements()));
      }
    } catch (NoSuchElementException e) {
      throw new AssertionError("Element not found. Failed to " + verification);
    }

    return not ? not() : this;
  }

  private static String toString(By selector) {
    return selector.toString().replace("By.selector: ", "");
  }

  private List<WebElement> findElements() {
    return driver.findElements(selector).stream().filter(narrowSelection).collect(Collectors.toList());
  }

  private String doesOrNot(String verb) {
    return Text.doesOrNot(not, verb);
  }

  private String isOrNot(String state) {
    return Text.isOrNot(not, state);
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

  private static String selectedStatus(WebElement element) {
    return isSelectable(element) ? element.isSelected() ? "selected" : "not selected" : "not selectable";
  }

  private static boolean isSelectable(WebElement element) {
    return ((element instanceof HtmlInput) || (element instanceof HtmlOption));
  }
}
