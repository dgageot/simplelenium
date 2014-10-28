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
import net.codestory.simplelenium.Should;
import net.codestory.simplelenium.driver.CurrentWebDriver;
import net.codestory.simplelenium.text.Text;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.Select;

import java.util.NoSuchElementException;
import java.util.concurrent.TimeUnit;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.UnaryOperator;
import java.util.stream.Stream;

public class LazyDomElement implements DomElement {
  private final By selector;
  private final ElementFilter filter;
  private final Retry retry;

  public LazyDomElement(By selector) {
    this(selector, Retry._30_SECONDS);
  }

  public LazyDomElement(By selector, Retry retry) {
    this(selector, ElementFilter.any(), retry);
  }

  private LazyDomElement(By selector, ElementFilter filter, Retry retry) {
    this.selector = selector;
    this.filter = filter;
    this.retry = retry;
  }

  // Narrow find

  @Override
  public ElementFilterBuilder withText() {
    Function<WebElement, String> toValue = element -> element.getText();
    return with("text", toValue);
  }

  @Override
  public ElementFilterBuilder withId() {
    Function<WebElement, String> toValue = element -> element.getAttribute("id");
    return with("id", toValue);
  }

  @Override
  public ElementFilterBuilder withName() {
    Function<WebElement, String> toValue = element -> element.getAttribute("name");
    return with("id", toValue);
  }

  @Override
  public ElementFilterBuilder withTagName() {
    Function<WebElement, String> toValue = element -> element.getTagName();
    return with("tag name", toValue);
  }

  @Override
  public ElementFilterBuilder withClass() {
    Function<WebElement, String> toValue = element -> element.getAttribute("class");
    return with("class", toValue);
  }

  @Override
  public ElementFilterBuilder withAttribute(String name) {
    Function<WebElement, String> toValue = element -> element.getAttribute(name);
    return with("attribute[" + name + "]", toValue);
  }

  @Override
  public ElementFilterBuilder withCssValue(String name) {
    Function<WebElement, String> toValue = element -> element.getCssValue(name);
    return with("cssValue[" + name + "]", toValue);
  }

  @Override
  public ElementFilterBuilder with(String description, Function<WebElement, String> toValue) {
    return new ElementFilterBuilder(this, description, toValue, true);
  }

  // Limit results

  @Override
  public LazyDomElement first() {
    return filter("first", StreamFilters.first());
  }

  @Override
  public LazyDomElement second() {
    return filter("second", StreamFilters.second());
  }

  @Override
  public LazyDomElement third() {
    return filter("third", StreamFilters.third());
  }

  @Override
  public LazyDomElement nth(int index) {
    return filter("nth[" + index + "]", StreamFilters.nth(index));
  }

  @Override
  public LazyDomElement limit(int max) {
    return filter("limit[" + max + "]", StreamFilters.limit(max));
  }

  @Override
  public LazyDomElement skip(int count) {
    return filter("skip[" + count + "]", StreamFilters.skip(count));
  }

  @Override
  public LazyDomElement last() {
    return filter("last", StreamFilters.last());
  }

  @Override
  public LazyDomElement filter(String description, UnaryOperator<Stream<WebElement>> filter) {
    return with(new ElementFilter(", " + description, filter));
  }

  LazyDomElement with(ElementFilter filter) {
    return new LazyDomElement(selector, this.filter.and(filter), retry);
  }

  // Assertions

  @Override
  public Should should() {
    return new LazyShould(selector, filter, Retry._5_SECONDS, false);
  }

  // Actions

  @Override
  public LazyDomElement fill(CharSequence text) {
    return execute("fill(" + text + ")", element -> element.sendKeys(text));
  }

  @Override
  public LazyDomElement pressReturn() {
    return execute("pressReturn()", element -> element.sendKeys(Keys.RETURN));
  }

  @Override
  public LazyDomElement sendKeys(CharSequence... keysToSend) {
    return execute("sendKeys()", element -> element.sendKeys(keysToSend));
  }

  @Override
  public LazyDomElement clear() {
    return execute("clear()", element -> element.clear());
  }

  @Override
  public LazyDomElement submit() {
    return execute("submit", element -> element.submit());
  }

  @Override
  public LazyDomElement click() {
    return execute("click", element -> element.click());
  }

  @Override
  public LazyDomElement doubleClick() {
    return executeActions("doubleClick", (element, actions) -> actions.doubleClick(element));
  }

  @Override
  public LazyDomElement clickAndHold() {
    return executeActions("clickAndHold", (element, actions) -> actions.clickAndHold(element));
  }

  @Override
  public LazyDomElement contextClick() {
    return executeActions("contextClick", (element, actions) -> actions.contextClick(element));
  }

  @Override
  public LazyDomElement release() {
    return executeActions("release", (element, actions) -> actions.release(element));
  }

  @Override
  public LazyDomElement executeActions(String description, BiConsumer<WebElement, Actions> actionsOnElement) {
    return execute(description, element -> {
      Actions actions = new Actions(CurrentWebDriver.get());
      actionsOnElement.accept(element, actions);
      actions.build().perform();
    });
  }

  // Selection

  @Override
  public LazyDomElement select(String text) {
    return executeSelect("select(" + text + ")", select -> select.selectByVisibleText(text));
  }

  @Override
  public LazyDomElement deselect() {
    return executeSelect("deselect()", select -> select.deselectAll());
  }

  @Override
  public LazyDomElement deselectByValue(String value) {
    return executeSelect("deselectByValue(" + value + ")", select -> select.deselectByValue(value));
  }

  @Override
  public LazyDomElement deselectByVisibleText(String text) {
    return executeSelect("deselectByVisibleText(" + text + ")", select -> select.deselectByVisibleText(text));
  }

  @Override
  public LazyDomElement deselectByIndex(int index) {
    return executeSelect("deselectByIndex(" + index + ")", select -> select.deselectByIndex(index));
  }

  @Override
  public LazyDomElement selectByIndex(int index) {
    return executeSelect("selectByIndex(" + index + ")", select -> select.selectByIndex(index));
  }

  @Override
  public LazyDomElement selectByValue(String value) {
    return executeSelect("selectByValue(" + value + ")", select -> select.selectByValue(value));
  }

  @Override
  public LazyDomElement executeSelect(String description, Consumer<Select> selectOnElement) {
    return execute(description, element -> selectOnElement.accept(new Select(element)));
  }

  // Actions on low level elements

  @Override
  public LazyDomElement execute(Consumer<? super WebElement> action) {
    return execute("execute(" + action + ")", action);
  }

  // Retry

  @Override
  public LazyDomElement retryFor(long duration, TimeUnit timeUnit) {
    return new LazyDomElement(selector, this.filter.and(filter), new Retry(duration, timeUnit));
  }

  // Internal

  private LazyDomElement execute(String message, Consumer<? super WebElement> action) {
    System.out.println(" - " + Text.toString(selector) + filter.getDescription() + "." + message);

    try {
      retry.execute(() -> findOne(), action);
    } catch (NoSuchElementException e) {
      throw new AssertionError("Element not found: " + Text.toString(selector));
    }

    return this;
  }

  private WebElement findOne() {
    Stream<WebElement> webElements = CurrentWebDriver.get().findElements(selector).stream();
    Stream<WebElement> filtered = filter.getFilter().apply(webElements);
    return filtered.findFirst().orElse(null);
  }
}
