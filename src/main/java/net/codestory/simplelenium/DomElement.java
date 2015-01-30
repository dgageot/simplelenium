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

import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.Select;

import java.util.concurrent.TimeUnit;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.UnaryOperator;
import java.util.stream.Stream;

public interface DomElement extends Navigation {
  // Narrow find

  FilteredDomElement withText();

  FilteredDomElement withId();

  FilteredDomElement withName();

  FilteredDomElement withTagName();

  FilteredDomElement withClass();

  FilteredDomElement withAttribute(String name);

  FilteredDomElement withCssValue(String name);

  FilteredDomElement with(String description, Function<WebElement, String> toValue);

  // Limit results

  DomElement first();

  DomElement second();

  DomElement third();

  DomElement nth(int index);

  DomElement limit(int max);

  DomElement skip(int count);

  DomElement last();

  DomElement filter(String description, UnaryOperator<Stream<WebElement>> filter);

  // Shortcuts

  default DomElement withText(String text) {
    return withText().containing(text);
  }

  default DomElement withId(String id) {
    return withId().equalTo(id);
  }

  default DomElement withName(String name) {
    return withName().equalTo(name);
  }

  default DomElement withClass(String cssClass) {
    return withClass().containingWord(cssClass);
  }

  default DomElement withTagName(String name) {
    return withTagName().equalTo(name);
  }

  // Assertions

  Should should();

  // Actions

  DomElement fill(CharSequence text);

  DomElement pressReturn();

  DomElement pressEnter();

  DomElement sendKeys(CharSequence... keysToSend);

  DomElement clear();

  DomElement submit();

  DomElement click();

  DomElement click(int x, int y);

  DomElement doubleClick();

  DomElement doubleClick(int x, int y);

  DomElement clickAndHold();

  DomElement contextClick();

  DomElement release();

  DomElement executeActions(String description, BiConsumer<WebElement, Actions> actionsOnElement);

  // Selection

  DomElement select(String text);

  DomElement deselect();

  DomElement deselectByValue(String value);

  DomElement deselectByVisibleText(String text);

  DomElement deselectByIndex(int index);

  DomElement selectByIndex(int index);

  DomElement selectByValue(String value);

  DomElement executeSelect(String description, Consumer<Select> selectOnElement);

  // Actions on low level elements

  DomElement execute(Consumer<WebElement> action);

  // Retry

  DomElement retryFor(long duration, TimeUnit timeUnit);
}
