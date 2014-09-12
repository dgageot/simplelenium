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

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.regex.Pattern;

import static java.lang.String.join;
import static java.util.stream.Stream.of;
import static net.codestory.simplelenium.Verification.KO;
import static net.codestory.simplelenium.Verification.NOT_FOUND;

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
    return verify("contains(" + join(";", texts) + ")", this::find, elements -> {
      return of(texts).allMatch(expected -> {
        return elements.stream().anyMatch(element -> element.getText().contains(expected));
      });
    });
  }

  public Should match(Pattern regexp) {
    return verify("match(" + regexp.pattern() + ")", this::find, elements -> {
      return elements.stream().anyMatch(element -> regexp.matcher(element.getText()).matches());
    });
  }

  public Should beEnabled() {
    return verify("is enabled", this::find, elements -> {
      return elements.stream().allMatch(element -> element.isEnabled());
    });
  }

  public Should beDisplayed() {
    return verify("is displayed", this::find, elements -> {
      return elements.stream().allMatch(element -> element.isDisplayed());
    });
  }

  public Should beSelected() {
    return verify("is selected", this::find, elements -> {
      return elements.stream().allMatch(element -> element.isSelected());
    });
  }

  public Should haveLessItemsThan(int maxCount) {
    return verify("has less than " + maxCount + " items", this::find, elements -> {
      return elements.size() < maxCount;
    });
  }

  public Should haveSize(int size) {
    return verify("has size " + size, this::find, elements -> {
      return elements.size() == size;
    });
  }

  public Should haveMoreItemsThan(int maxCount) {
    return verify("has more than " + maxCount + " items", this::find, elements -> {
      return elements.size() > maxCount;
    });
  }

  public Should beEmpty() {
    return verify("is empty", this::find, elements -> {
      return elements.isEmpty();
    });
  }

  private <T> Should verify(String message, Supplier<T> target, Predicate<T> predicate) {
    String verification = "verify that " + toString(selector) + " " + message;

    System.out.println("   -> " + verification);

    Verification result = retry.verify(target, not ? predicate.negate() : predicate);
    if (result == NOT_FOUND) {
      throw new AssertionError("Element not found. Failed to " + verification);
    }
    if (result == KO) {
      throw new AssertionError("Failed to " + verification);
    }
    return new Should(driver, selector, retry, false);
  }

  private static String toString(By selector) {
    return selector.toString().replace("By.selector: ", "");
  }

  private List<WebElement> find() {
    return driver.findElements(selector);
  }
}
