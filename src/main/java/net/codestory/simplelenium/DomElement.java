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
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.Select;

import java.util.Objects;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import java.util.function.Predicate;

import static java.util.concurrent.TimeUnit.SECONDS;

public class DomElement {
  private final WebDriver driver;
  private final By selector;
  private final Predicate<WebElement> narrowSelection;
  private final Retry retry;

  DomElement(WebDriver driver, By selector) {
    this(driver, selector, element -> true, new Retry(30, SECONDS));
  }

  DomElement(WebDriver driver, By selector, Predicate<WebElement> narrowSelection, Retry retry) {
    this.driver = driver;
    this.selector = selector;
    this.narrowSelection = narrowSelection;
    this.retry = retry;
  }

  // Narrow find
  //
  public DomElement withText(String text) {
    return new DomElement(driver, selector, element -> Objects.equals(element.getText(), text), retry);
  }

  // Assertions
  //
  public Should should() {
    return new Should(driver, selector, narrowSelection, 5, SECONDS);
  }

  public Should shouldWithin(long duration, TimeUnit timeUnit) {
    return new Should(driver, selector, narrowSelection, duration, timeUnit);
  }

  // Actions
  //
  public void fill(CharSequence text) {
    execute("fill(" + text + ")", element -> element.sendKeys(text));
  }

  public void pressReturn() {
    execute("pressReturn()", element -> element.sendKeys(Keys.RETURN));
  }

  public void sendKeys(CharSequence... keysToSend) {
    execute("sendKeys()", element -> element.sendKeys(keysToSend));
  }

  public void clear() {
    execute("clear()", element -> element.clear());
  }

  public void submit() {
    execute("submit", element -> element.submit());
  }

  public void click() {
    execute("click", element -> element.click());
  }

  public void select(String text) {
    execute("select(" + text + ")", element -> new Select(element).selectByVisibleText(text));
  }

  public void execute(Consumer<? super WebElement> action) {
    execute("execute(" + action + ")", action);
  }

  private void execute(String message, Consumer<? super WebElement> action) {
    System.out.println(" - " + selector + "." + message);
    retry.execute(() -> find(), action);
  }

  private WebElement find() {
    WebElement element = driver.findElement(selector);
    if (!narrowSelection.test(element)) {
      return null;
    }
    return element;
  }
}
