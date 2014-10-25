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

import net.codestory.simplelenium.filters.ElementFilter;
import net.codestory.simplelenium.text.Text;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.Select;

import java.util.function.Consumer;

public class DomElement {
  private final By selector;
  private final ElementFilter narrowSelection;
  private final Retry retry;

  public DomElement(By selector) {
    this(selector, ElementFilter.any(), Retry._30_SECONDS);
  }

  private DomElement(By selector, ElementFilter narrowSelection, Retry retry) {
    this.selector = selector;
    this.narrowSelection = narrowSelection;
    this.retry = retry;
  }

  // Narrow find
  //
  public DomElement withText(String text) {
    return new DomElement(selector, ElementFilter.withText(text), retry);
  }

  public DomElement withTagName(String name) {
    return new DomElement(selector, ElementFilter.withTagName(name), retry);
  }

  // Assertions
  //
  public Should should() {
    return new Should(selector, narrowSelection, Retry._5_SECONDS);
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

  // Internal
  //

  private void execute(String message, Consumer<? super WebElement> action) {
    System.out.println(" - " + Text.toString(selector) + narrowSelection.getDescription() + "." + message);
    retry.execute(() -> findOne(), action);
  }

  private WebElement findOne() {
    return CurrentWebDriver.get().findElements(selector).stream().filter(narrowSelection).findFirst().orElse(null);
  }
}
