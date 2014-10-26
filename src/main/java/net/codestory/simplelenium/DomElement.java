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

import net.codestory.simplelenium.driver.CurrentWebDriver;
import net.codestory.simplelenium.filters.ElementFilter;
import net.codestory.simplelenium.filters.ElementFilterBuilder;
import net.codestory.simplelenium.filters.StreamFilters;
import net.codestory.simplelenium.text.Text;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.Select;

import java.util.concurrent.TimeUnit;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.UnaryOperator;
import java.util.stream.Stream;

public class DomElement {
  private final By selector;
  private final ElementFilter filter;
  private final Retry retry;

  public DomElement(By selector) {
    this(selector, ElementFilter.any(), Retry._30_SECONDS);
  }

  private DomElement(By selector, ElementFilter filter, Retry retry) {
    this.selector = selector;
    this.filter = filter;
    this.retry = retry;
  }

  // Narrow find

  public DomElement with(ElementFilter filter) {
    return new DomElement(selector, this.filter.and(filter), retry);
  }

  public ElementFilterBuilder withText() {
    return narrow("text", element -> element.getText());
  }

  public ElementFilterBuilder withId() {
    return narrow("id", element -> element.getAttribute("id"));
  }

  public ElementFilterBuilder withName() {
    return narrow("id", element -> element.getAttribute("name"));
  }

  public ElementFilterBuilder withTagName() {
    return narrow("tag name", element -> element.getTagName());
  }

  public ElementFilterBuilder withClass() {
    return narrow("class", element -> element.getAttribute("class"));
  }

  public ElementFilterBuilder withAttribute(String name) {
    return narrow("attribute[" + name + "]", element -> element.getAttribute(name));
  }

  public ElementFilterBuilder withCssValue(String name) {
    return narrow("cssValue[" + name + "]", element -> element.getCssValue(name));
  }

  private ElementFilterBuilder narrow(String description, Function<WebElement, String> toValue) {
    return new ElementFilterBuilder(this, description, toValue, true);
  }

  // Limit results

  public DomElement first() {
    return filter("first", StreamFilters.first());
  }

  public DomElement second() {
    return filter("second", StreamFilters.second());
  }

  public DomElement third() {
    return filter("third", StreamFilters.third());
  }

  public DomElement nth(int index) {
    return filter("nth[" + index + "]", StreamFilters.nth(index));
  }

  public DomElement limit(int max) {
    return filter("limit[" + max + "]", StreamFilters.limit(max));
  }

  public DomElement skip(int count) {
    return filter("skip[" + count + "]", StreamFilters.skip(count));
  }

  public DomElement last() {
    return filter("last", StreamFilters.last());
  }

  private DomElement filter(String description, UnaryOperator<Stream<WebElement>> filter) {
    return with(new ElementFilter(", " + description, filter));
  }

  // Shortcuts

  public DomElement withText(String text) {
    return withText().contains(text);
  }

  public DomElement withId(String id) {
    return withId().equalsTo(id);
  }

  public DomElement withName(String name) {
    return withName().equalsTo(name);
  }

  public DomElement withClass(String cssClass) {
    return withClass().containsWord(cssClass);
  }

  public DomElement withTagName(String name) {
    return withTagName().equalsTo(name);
  }

  // Assertions

  public Should should() {
    return new Should(selector, filter, Retry._5_SECONDS, false);
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
    executeActions("doubleClick", (element, actions) -> actions.doubleClick(element));
  }

  public void clickAndHold() {
    executeActions("clickAndHold", (element, actions) -> actions.clickAndHold(element));
  }

  public void contextClick() {
    executeActions("contextClick", (element, actions) -> actions.contextClick(element));
  }

  public void release() {
    executeActions("release", (element, actions) -> actions.release(element));
  }

  private void executeActions(String description, BiConsumer<WebElement, Actions> actionsOnElement) {
    execute(description, element -> {
      Actions actions = new Actions(CurrentWebDriver.get());
      actionsOnElement.accept(element, actions);
      actions.perform();
    });
  }

  // Selection

  public void select(String text) {
    executeSelect("select(" + text + ")", select -> select.selectByVisibleText(text));
  }

  public void deselect() {
    executeSelect("deselect()", select -> select.deselectAll());
  }

  public void deselectByValue(String value) {
    executeSelect("deselectByValue(" + value + ")", select -> select.deselectByValue(value));
  }

  public void deselectByVisibleText(String text) {
    executeSelect("deselectByVisibleText(" + text + ")", select -> select.deselectByVisibleText(text));
  }

  public void deselectByIndex(int index) {
    executeSelect("deselectByIndex(" + index + ")", select -> select.deselectByIndex(index));
  }

  public void selectByIndex(int index) {
    executeSelect("selectByIndex(" + index + ")", select -> select.selectByIndex(index));
  }

  public void selectByValue(String value) {
    executeSelect("selectByValue(" + value + ")", select -> select.selectByValue(value));
  }

  private void executeSelect(String description, Consumer<Select> selectOnElement) {
    execute(description, element -> {
      Select select = new Select(element);
      selectOnElement.accept(select);
    });
  }

  // Actions on low level elements

  public void execute(Consumer<? super WebElement> action) {
    execute("execute(" + action + ")", action);
  }

  // Retry

  public DomElement retryFor(long duration, TimeUnit timeUnit) {
    return new DomElement(selector, this.filter.and(filter), new Retry(duration, timeUnit));
  }

  // Internal

  private void execute(String message, Consumer<? super WebElement> action) {
    System.out.println(" - " + Text.toString(selector) + filter.getDescription() + "." + message);

    retry.execute(() -> findOne(), action);
  }

  private WebElement findOne() {
    Stream<WebElement> webElements = CurrentWebDriver.get().findElements(selector).stream();
    Stream<WebElement> filtered = filter.getFilter().apply(webElements);
    return filtered.findFirst().orElse(null);
  }
}
