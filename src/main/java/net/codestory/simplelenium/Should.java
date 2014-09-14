/**
 * Copyright (C) 2013 all@code-story.net
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
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.regex.Pattern;

import static java.lang.String.join;
import static java.util.stream.Collectors.joining;
import static java.util.stream.Stream.of;

public class Should {
  private final WebDriver driver;
  private final By selector;
  private final Retry retry;
  private final boolean not;

  Should(WebDriver driver, By selector, long duration, TimeUnit timeUnit) {
    this(driver, selector, new Retry(duration, timeUnit), false);
  }

  private Should(WebDriver driver, By selector, Retry retry, boolean not) {
    this.driver = driver;
    this.selector = selector;
    this.retry = retry;
    this.not = not;
  }

  public Should not() {
    return new Should(driver, selector, retry, !not);
  }

  public Should contain(String... texts) {
    return verify(
      "contains(" + join(";", texts) + ")",
      elements -> {
        return of(texts).allMatch(expected -> {
          return elements.stream().anyMatch(element -> element.getText().contains(expected));
        });
      },
      elements -> {
        return "It contains" + statuses(elements, element -> element.getText());
      });
  }

  public Should match(Pattern regexp) {
    return verify(
      "matches(" + regexp.pattern() + ")",
      elements -> {
        return elements.stream().anyMatch(element -> regexp.matcher(element.getText()).matches());
      },
      elements -> {
        return "It contains" + statuses(elements, element -> element.getText());
      });
  }

  public Should beEnabled() {
    return verify(
      isOrIsNot("enabled"),
      elements -> {
        return elements.stream().allMatch(element -> element.isEnabled());
      },
      elements -> {
        return "It is " + statuses(elements, element -> enabledStatus(element));
      });
  }

  public Should beDisplayed() {
    return verify(
      isOrIsNot("displayed"),
      elements -> {
        return elements.stream().allMatch(element -> element.isDisplayed());
      },
      elements -> {
        return "It is " + statuses(elements, element -> displayedStatus(element));
      });
  }

  public Should beSelected() {
    return verify(
      isOrIsNot("selected"),
      elements -> {
        return elements.stream().allMatch(element -> isSelected(element));
      },
      elements -> {
        return "It is " + statuses(elements, element -> selectedStatus(element));
      });
  }

  public Should haveLessItemsThan(int maxCount) {
    return verify(
      "contains less than " + pluralize(maxCount, "element"),
      elements -> {
        return elements.size() < maxCount;
      },
      elements -> {
        return "It contains " + pluralize(elements.size(), "element");
      });
  }

  public Should haveSize(int size) {
    return verify(
      "contains " + pluralize(size, "element"),
      elements -> {
        return elements.size() == size;
      },
      elements -> {
        return "It contains " + pluralize(elements.size(), "element");
      });
  }

  public Should haveMoreItemsThan(int minCount) {
    return verify(
      "contains more than " + pluralize(minCount, "element"),
      elements -> {
        return elements.size() > minCount;
      },
      elements -> {
        return "It contains " + pluralize(elements.size(), "element");
      });
  }

  public Should beEmpty() {
    return verify(
      isOrIsNot("empty"),
      elements -> {
        return elements.isEmpty();
      },
      elements -> {
        return "It contains " + pluralize(elements.size(), "element");
      });
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
    return driver.findElements(selector);
  }

  private String isOrIsNot(String state) {
    return "is " + (not ? "not " : "") + state;
  }

  private static String pluralize(int n, String word) {
    if (n <= 1) {
      return n + " " + word;
    } else {
      return n + " " + word + "s";
    }
  }

  private static boolean isSelected(WebElement element) {
    return ((element instanceof HtmlInput) || (element instanceof HtmlOption)) && element.isSelected();
  }

  private static String statuses(List<WebElement> elements, Function<WebElement, String> elementToStatus) {
    return "(" + elements.stream().map(elementToStatus).collect(joining(";")) + ")";
  }

  private static String enabledStatus(WebElement element) {
    return element.isEnabled() ? "enabled" : "disabled";
  }

  private static String displayedStatus(WebElement element) {
    return element.isDisplayed() ? "displayed" : "hidden";
  }

  private static String selectedStatus(WebElement element) {
    if ((element instanceof HtmlInput) || (element instanceof HtmlOption)) {
      return element.isSelected() ? "selected" : "not selected";
    }
    return "not selectable";
  }
}
