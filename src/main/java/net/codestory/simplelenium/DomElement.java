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
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.Select;

import java.util.function.Consumer;

import static net.codestory.simplelenium.filters.ElementFilter.*;

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

  public DomElement withText(String text) {
    return with(text(text));
  }

  public DomElement withTagName(String name) {
    return with(tagName(name));
  }

  public DomElement withAttribute(String name, String value) {
    return with(attribute(name, value));
  }

  public DomElement withCssValue(String name, String value) {
    return with(cssValue(name, value));
  }

  public DomElement with(ElementFilter filter) {
    return new DomElement(selector, filter, retry);
  }

  // Assertions

  public Should should() {
    return new Should(selector, narrowSelection, Retry._5_SECONDS, false);
  }

  // Actions

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

  public void doubleClick() {
    execute("doubleClick", element -> actions().doubleClick(element).perform());
  }

  public void clickAndHold() {
    execute("clickAndHold", element -> actions().clickAndHold(element).perform());
  }

  public void contextClick() {
    execute("contextClick", element -> actions().contextClick(element).perform());
  }

  public void release() {
    execute("release", element -> actions().release(element).perform());
  }

  // Selection

  public void select(String text) {
    execute("select(" + text + ")", element -> selection(element).selectByVisibleText(text));
  }

  public void deselect() {
    execute("deselect()", element -> selection(element).deselectAll());
  }

  public void deselectByValue(String value) {
    execute("deselectByValue(" + value + ")", element -> selection(element).deselectByValue(value));
  }

  public void deselectByVisibleText(String text) {
    execute("deselectByVisibleText(" + text + ")", element -> selection(element).deselectByVisibleText(text));
  }

  public void deselectByIndex(int index) {
    execute("deselectByIndex(" + index + ")", element -> selection(element).deselectByIndex(index));
  }

  public void selectByIndex(int index) {
    execute("selectByIndex(" + index + ")", element -> selection(element).selectByIndex(index));
  }

  public void selectByValue(String value) {
    execute("selectByValue(" + value + ")", element -> selection(element).selectByValue(value));
  }

  // Actions on low level elements

  public void execute(Consumer<? super WebElement> action) {
    execute("execute(" + action + ")", action);
  }

  public void executeActions(ActionsOnElement actionsOnElement) {
    execute("execute actions", element -> {
      Actions actions = actions();
      actionsOnElement.act(actions, element);
      actions.perform();
    });
  }

  // Internal

  private Select selection(WebElement element) {
    return new Select(element);
  }

  private Actions actions() {
    return new Actions(CurrentWebDriver.get());
  }

  private void execute(String message, Consumer<? super WebElement> action) {
    System.out.println(" - " + Text.toString(selector) + narrowSelection.getDescription() + "." + message);
    retry.execute(() -> findOne(), action);
  }

  private WebElement findOne() {
    return CurrentWebDriver.get().findElements(selector).stream().filter(narrowSelection).findFirst().orElse(null);
  }
}
