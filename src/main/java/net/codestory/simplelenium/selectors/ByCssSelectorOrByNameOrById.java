/**
 * Copyright (C) 2013-2015 all@code-story.net
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
package net.codestory.simplelenium.selectors;

import org.openqa.selenium.By;
import org.openqa.selenium.SearchContext;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.internal.FindsByCssSelector;
import org.openqa.selenium.internal.FindsById;
import org.openqa.selenium.internal.FindsByName;

import java.io.Serializable;
import java.util.Collections;
import java.util.List;

public class ByCssSelectorOrByNameOrById extends By implements Serializable {
  private static final long serialVersionUID = -3910258723099459239L;

  private final String selector;

  public ByCssSelectorOrByNameOrById(String selector) {
    this.selector = selector;
  }

  @Override
  public WebElement findElement(SearchContext context) {
    WebElement element = ((FindsByCssSelector) context).findElementByCssSelector(selector);
    if (element != null) {
      return element;
    }

    element = ((FindsByName) context).findElementByName(selector);
    if (element != null) {
      return element;
    }

    element = ((FindsById) context).findElementById(selector);
    if (element != null) {
      return element;
    }

    return null;
  }

  @Override
  public List<WebElement> findElements(SearchContext context) {
    List<WebElement> elements = ((FindsByCssSelector) context).findElementsByCssSelector(selector);
    if ((elements != null) && (!elements.isEmpty())) {
      return elements;
    }

    elements = ((FindsByName) context).findElementsByName(selector);
    if ((elements != null) && (!elements.isEmpty())) {
      return elements;
    }

    elements = ((FindsById) context).findElementsById(selector);
    if ((elements != null) && (!elements.isEmpty())) {
      return elements;
    }

    return Collections.emptyList();
  }

  @Override
  public String toString() {
    return selector;
  }
}
