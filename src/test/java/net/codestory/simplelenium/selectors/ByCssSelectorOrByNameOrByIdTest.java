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

import net.codestory.simplelenium.driver.SeleniumDriver;
import org.junit.Test;
import org.openqa.selenium.WebElement;

import java.util.List;

import static java.util.Arrays.asList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class ByCssSelectorOrByNameOrByIdTest {
  WebElement expectedElement = mock(WebElement.class);
  WebElement anotherElement = mock(WebElement.class);
  SeleniumDriver driver = mock(SeleniumDriver.class);

  WebElement find(String selector) {
    ByCssSelectorOrByNameOrById finder = new ByCssSelectorOrByNameOrById(selector);
    return finder.findElement(driver);
  }

  List<WebElement> findElements(String selector) {
    ByCssSelectorOrByNameOrById finder = new ByCssSelectorOrByNameOrById(selector);
    return finder.findElements(driver);
  }

  @Test
  public void shouldNotFindUnknownElement() {
    WebElement element = find("unknown[]");

    assertThat(element).isNull();
  }

  @Test
  public void shouldFindElementByCssSelector() {
    when(driver.findElementByCssSelector(".class")).thenReturn(expectedElement);

    WebElement element = find(".class");

    assertThat(element).isSameAs(expectedElement);
  }

  @Test
  public void shouldFindElementByNameSelector() {
    when(driver.findElementByName("name")).thenReturn(expectedElement);

    WebElement element = find("name");

    assertThat(element).isSameAs(expectedElement);
  }

  @Test
  public void shouldFindElementByIdSelector() {
    when(driver.findElementById("#id")).thenReturn(expectedElement);

    WebElement element = find("#id");

    assertThat(element).isSameAs(expectedElement);
  }

  @Test
  public void shouldFindElementsByCssSelector() {
    when(driver.findElementsByCssSelector(".class")).thenReturn(asList(expectedElement, anotherElement));

    List<WebElement> elements = findElements(".class");

    assertThat(elements).containsExactly(expectedElement, anotherElement);
  }

  @Test
  public void shouldFindElementsByNameSelector() {
    when(driver.findElementsByName("name")).thenReturn(asList(expectedElement, anotherElement));

    List<WebElement> elements = findElements("name");

    assertThat(elements).containsExactly(expectedElement, anotherElement);
  }

  @Test
  public void shouldFindElementsByIdSelector() {
    when(driver.findElementsById("#id")).thenReturn(asList(expectedElement, anotherElement));

    List<WebElement> elements = findElements("#id");

    assertThat(elements).containsExactly(expectedElement, anotherElement);
  }

  @Test
  public void shouldNotFindElementsThenReturnAnEmptyList() {
    List<WebElement> elements = findElements("empty");

    assertThat(elements).isEmpty();
  }

  @Test
  public void shouldPrintToString() {
    ByCssSelectorOrByNameOrById selector = new ByCssSelectorOrByNameOrById("to-print");

    assertThat(selector.toString()).isEqualTo("to-print");
    assertThat(selector).hasToString("to-print");
  }
}
