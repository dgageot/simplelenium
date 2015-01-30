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
import net.codestory.simplelenium.text.Text;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.Select;

import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import java.util.function.*;
import java.util.stream.Stream;

import static net.codestory.simplelenium.filters.WebElementHelper.text;

public class LazyDomElement implements DomElement {
  private final LazyDomElement parent;
  private final By selector;
  private final ElementFilter filter;
  private final Retry retry;

  public LazyDomElement(By selector) {
    this(selector, Retry._30_SECONDS);
  }

  public LazyDomElement(By selector, Retry retry) {
    this(null, selector, ElementFilter.any(), retry);
  }

  public LazyDomElement(LazyDomElement parent, By selector) {
    this(parent, selector, Retry._30_SECONDS);
  }

  public LazyDomElement(LazyDomElement parent, By selector, Retry retry) {
    this(parent, selector, ElementFilter.any(), retry);
  }

  private LazyDomElement(LazyDomElement parent, By selector, ElementFilter filter, Retry retry) {
    this.parent = parent;
    this.selector = selector;
    this.filter = filter;
    this.retry = retry;
  }

  // Nested find

  public DomElement find(String selector) {
    return new LazyDomElement(this, By.cssSelector(selector));
  }

  public DomElement find(By selector) {
    return new LazyDomElement(this, selector);
  }

  // Narrow find

  @Override
  public ElementFilterBuilder withText() {
    Function<WebElement, String> toValue = element -> text(element);
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
    return new LazyDomElement(parent, selector, this.filter.and(filter), retry);
  }

  // Assertions

  @Override
  public Should should() {
    return new LazyShould(this, Retry._5_SECONDS, true);
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
  public LazyDomElement pressEnter() {
    return execute("pressEnter()", element -> element.sendKeys(Keys.ENTER));
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
  public LazyDomElement click(int x, int y) {
    return executeActions("click(" + x + "," + y + ")", (element, actions) -> actions.moveToElement(element, x, y).click());
  }

  @Override
  public LazyDomElement doubleClick() {
    return executeActions("doubleClick", (element, actions) -> actions.doubleClick(element));
  }

  @Override
  public LazyDomElement doubleClick(int x, int y) {
    return executeActions("doubleClick(" + x + "," + y + ")", (element, actions) -> actions.moveToElement(element, x, y).doubleClick());
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
      Actions actions = new Actions(driver());
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
  public LazyDomElement execute(Consumer<WebElement> action) {
    return execute("execute(" + action + ")", action);
  }

  // Retry

  @Override
  public LazyDomElement retryFor(long duration, TimeUnit timeUnit) {
    return new LazyDomElement(parent, selector, this.filter.and(filter), new Retry(duration, timeUnit));
  }

  // Internal

  private LazyDomElement execute(String message, Consumer<WebElement> action) {
    System.out.println(" - " + Text.toString(selector) + filter.getDescription() + "." + message);

    Supplier<Optional<WebElement>> findOne = () -> stream().findFirst();
    try {
      retry.execute(findOne, action);
    } catch (NoSuchElementException e) {
      throw new AssertionError("Element not found: " + Text.toString(selector));
    }

    // After an action, go back to root level for future finds
    return new LazyDomElement(parent, selector, filter, retry) {
      public DomElement find(String selector) {
        return new LazyDomElement(By.cssSelector(selector));
      }

      public DomElement find(By selector) {
        return new LazyDomElement(selector);
      }
    };
  }

  @Override
  public String toString() {
    return ((parent == null) ? "" : parent.toString() + " ") + Text.toString(selector) + filter.getDescription();
  }

  LazyDomElement parent() {
    return parent;
  }

  Stream<WebElement> stream() {
    Stream<WebElement> webElements;
    if (parent != null) {
      webElements = parent.stream().flatMap(element -> element.findElements(selector).stream());
    } else {
      webElements = driver().findElements(selector).stream();
    }
    return filter.getFilter().apply(webElements);
  }
}
