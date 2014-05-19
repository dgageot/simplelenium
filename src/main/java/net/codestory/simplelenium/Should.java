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

import static java.lang.String.*;
import static java.util.stream.Stream.*;

import java.util.*;
import java.util.concurrent.*;
import java.util.function.*;

import org.openqa.selenium.*;

public class Should {
  private final WebDriver driver;
  private final By selector;
  private final String selectorDesc;
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
    this.selectorDesc = toString(selector);
  }

  public Should not() {
    return new Should(driver, selector, retry, !not);
  }

  public void contain(String... texts) {
    String textsDesc = join(";", texts);

    verify(
        this::find,
        new Check<WebElement>(selectorDesc + " contains(" + textsDesc + ")", selectorDesc + " does not contain(" + textsDesc + ")") {
          @Override
          protected Result execute(WebElement element) {
            String elementText = element.getText();
            String actualState = selectorDesc + " hasText(" + elementText + ")";

            if (of(texts).allMatch(expected -> elementText.contains(expected))) {
              return ok(actualState);
            }
            return ko(actualState);
          }
        }
    );
  }

  public void notContain(String text) {
    verify("does not contain (" + text + ")", this::find, element -> !element.getText().contains(text));
  }

  public void beEnabled() {
    verify("is enabled", this::find, element -> element.isEnabled());
  }

  public void beDisabled() {
    verify("is disabled", this::find, element -> !element.isEnabled());
  }

  public void beDisplayed() {
    verify("is displayed", this::find, element -> element.isDisplayed());
  }

  public void beHidden() {
    verify("is hidden", this::find, element -> !element.isDisplayed());
  }

  public void beSelected() {
    verify("is selected", this::find, element -> element.isSelected());
  }

  public void haveNoMoreItemsThan(int maxCount) {
    verify("has at most " + maxCount + " items", this::findMultiple, elements -> elements.size() <= maxCount);
  }

  public void haveSize(int size) {
    verify("has size " + size, this::findMultiple, elements -> elements.size() == size);
  }

  public void beEmpty() {
    verify("is empty", this::findMultiple, elements -> elements.isEmpty());
  }

  private <T> void verify(Supplier<T> target, Check<T> check) {
    if (not) {
      check = check.negate();
    }

    String verificationDesc = check.getDescription();

    System.out.println("   -> verify that " + verificationDesc);

    Verification result = retry.verify(target, check);
    if (result.isOk()) {
      return; // success
    }

    throw new AssertionError(result.description(verificationDesc));
  }

  private <T> void verify(String message, Supplier<T> target, Predicate<T> predicate) {
    String verification = "verify that " + toString(selector) + " " + message;

    System.out.println("   -> " + verification);

    Verification result = retry.verify(target, predicate);
    if (result.isNotFound()) {
      throw new AssertionError("Element not found. Failed to " + verification);
    }

    if ((not && result.isOk()) || (!not && !result.isOk())) {
      throw new AssertionError("Failed to " + verification);
    }
  }

  private static String toString(By selector) {
    return selector.toString().replace("By.selector: ", "");
  }

  private WebElement find() {
    return driver.findElement(selector);
  }

  private List<WebElement> findMultiple() {
    return driver.findElements(selector);
  }
}
