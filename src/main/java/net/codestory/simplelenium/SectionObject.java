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
import org.openqa.selenium.support.ByIdOrName;

import static net.codestory.simplelenium.reflection.ReflectionUtil.*;

public interface SectionObject extends Navigation {
  public default String path() {
    String currentUrl = CurrentWebDriver.get().getCurrentUrl();
    String defaultBaseUrl = Navigation.getBaseUrl();

    if (currentUrl.startsWith(defaultBaseUrl)) {
      return currentUrl.substring(defaultBaseUrl.length());
    }
    return currentUrl;
  }

  public default String url() {
    return CurrentWebDriver.get().getCurrentUrl();
  }

  public default String title() {
    return CurrentWebDriver.get().getTitle();
  }

  public default String pageSource() {
    return CurrentWebDriver.get().getPageSource();
  }

  // Injection

  public static void injectMissingPageObjects(Object instance) {
    forEachFieldOfType(SectionObject.class, instance, field -> {
      setIfNull(field, instance, () -> {
        SectionObject pageObject = newInstance((Class<? extends SectionObject>) field.getType());
        injectMissingElements(pageObject);
        return pageObject;
      });
    });
  }

  public static void injectMissingElements(SectionObject pageObject) {
    injectMissingPageObjects(pageObject);
    forEachFieldOfType(DomElement.class, pageObject, field -> {
      setIfNull(field, pageObject, () -> new DomElement(new ByIdOrName(field.getName())));
    });
  }
}
