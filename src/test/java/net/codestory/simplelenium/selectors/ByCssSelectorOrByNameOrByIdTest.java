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

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.withSettings;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.SearchContext;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.internal.FindsByCssSelector;
import org.openqa.selenium.internal.FindsById;
import org.openqa.selenium.internal.FindsByName;

public class ByCssSelectorOrByNameOrByIdTest {

  private WebElement mockElement;
  private SearchContext searchContext;
  private List<WebElement> fakeElements;

  @Before
  public void setUp() {
    mockElement = mock(WebElement.class);
    searchContext = mock(
        SearchContext.class,
        withSettings().extraInterfaces(WebDriver.class, FindsByName.class, FindsByCssSelector.class,
            FindsById.class));
    fakeElements = Arrays.asList(mockElement, mock(WebElement.class), mock(WebElement.class));
  }

  @Test
  public void shouldFindElementByCssSelector() {
    String selector = ".class";
    ByCssSelectorOrByNameOrById cssSelector = new ByCssSelectorOrByNameOrById(selector);

    when(((FindsByCssSelector) searchContext).findElementByCssSelector(selector)).thenReturn(mockElement);

    WebElement element = cssSelector.findElement(searchContext);

    assertThat(element).isEqualTo(mockElement);
  }

  @Test
  public void shouldFindElementByNameSelector() {
    String selector = "name";
    ByCssSelectorOrByNameOrById nameSelector = new ByCssSelectorOrByNameOrById(selector);

    when(((FindsByName) searchContext).findElementByName(selector)).thenReturn(mockElement);

    WebElement element = nameSelector.findElement(searchContext);

    assertThat(element).isEqualTo(mockElement);
  }

  @Test
  public void shouldFindElementByIdSelector() {
    String selector = "#id";
    ByCssSelectorOrByNameOrById nameSelector = new ByCssSelectorOrByNameOrById(selector);

    when(((FindsById) searchContext).findElementById(selector)).thenReturn(mockElement);

    WebElement element = nameSelector.findElement(searchContext);

    assertThat(element).isEqualTo(mockElement);
  }
  
  @Test
  public void shouldNotFindElementWithBrakets() {
    String selector = "anything[]";
    ByCssSelectorOrByNameOrById nameSelector = new ByCssSelectorOrByNameOrById(selector);

    when(((FindsByCssSelector) searchContext).findElementByCssSelector(selector)).thenReturn(null);

    WebElement element = nameSelector.findElement(searchContext);

    assertThat(element).isNull();
  }
  
  @Test
  public void shouldFindElementsListWithByCssSelector() {
    String selector = ".class";
    ByCssSelectorOrByNameOrById cssSelector = new ByCssSelectorOrByNameOrById(selector);
    
    when(((FindsByCssSelector) searchContext).findElementsByCssSelector(selector)).thenReturn(fakeElements);

    List<WebElement> elements = cssSelector.findElements(searchContext);

    assertThat(elements).hasSize(3);
  }
  
  @Test
  public void shouldFindElementsListWithByNameSelector() {
    String selector = "name";
    ByCssSelectorOrByNameOrById cssSelector = new ByCssSelectorOrByNameOrById(selector);
    
    List<WebElement> fakeElements = Arrays.asList(mockElement, mock(WebElement.class));

    when(((FindsByName) searchContext).findElementsByName(selector)).thenReturn(fakeElements);

    List<WebElement> elements = cssSelector.findElements(searchContext);

    assertThat(elements).hasSize(2);
  }
  
  @Test
  public void shouldFindElementsListWithByIdSelector() {
    String selector = "#Id";
    ByCssSelectorOrByNameOrById cssSelector = new ByCssSelectorOrByNameOrById(selector);
    
    List<WebElement> fakeElements = Arrays.asList(mockElement);

    when(((FindsById) searchContext).findElementsById(selector)).thenReturn(fakeElements);

    List<WebElement> elements = cssSelector.findElements(searchContext);

    assertThat(elements).hasSize(1);
  }
  
  @Test
  public void shouldNotFindElementsThenReturnAEmptyList() {
    String selector = "empty";
    ByCssSelectorOrByNameOrById nameSelector = new ByCssSelectorOrByNameOrById(selector);

    when(((FindsByCssSelector) searchContext).findElementsByCssSelector(selector)).thenReturn(Collections.emptyList());

    List<WebElement> elements = nameSelector.findElements(searchContext);

    assertThat(elements).isEmpty();
  }
  
  @Test
  public void shouldNotFindElementsThenReturnNull() {
    String selector = "null";
    ByCssSelectorOrByNameOrById nameSelector = new ByCssSelectorOrByNameOrById(selector);

    when(((FindsByCssSelector) searchContext).findElementsByCssSelector(selector)).thenReturn(null);

    List<WebElement> elements = nameSelector.findElements(searchContext);

    assertThat(elements).isEmpty();
  }
  
  @Test
  public void shouldPrintAToStringElement() {
    String printable = "to-print";
    ByCssSelectorOrByNameOrById nameSelector = new ByCssSelectorOrByNameOrById(printable);

    assertThat(nameSelector.toString()).isEqualTo(printable);
  }
}
