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
import org.openqa.selenium.WebDriver;

import java.net.URI;

public interface Navigation extends DomElementFinder {
  ThreadLocal<String> baseUrl = new ThreadLocal<>();

  public static String getBaseUrl() {
    return baseUrl.get();
  }

  public static void setBaseUrl(String url) {
    baseUrl.set(url);
  }

  public default Navigation goTo(String url) {
    URI uri = URI.create(url);
    if (!uri.isAbsolute()) {
      url = getBaseUrl() + url;
    }

    System.out.println("goTo " + url);

    WebDriver webDriver = CurrentWebDriver.get();
    webDriver.get(url);

    System.out.println(" - current url " + webDriver.getCurrentUrl());

    return this;
  }

  public default Navigation goTo(PageObject page) {
    goTo(page.url());
    return this;
  }
}
