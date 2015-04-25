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
package net.codestory.simplelenium;

import net.codestory.simplelenium.driver.CurrentWebDriver;
import net.codestory.simplelenium.driver.PhantomJSDriver;

import java.net.URI;
import java.util.List;

import static java.util.Objects.requireNonNull;
import static java.util.stream.Collectors.toList;

public interface Navigation extends DomElementFinder {
  ThreadLocal<String> baseUrl = new ThreadLocal<>();

  static String getBaseUrl() {
    return baseUrl.get();
  }

  static void setBaseUrl(String url) {
    baseUrl.set(url);
  }

  default PhantomJSDriver driver() {
    return CurrentWebDriver.get();
  }

  default List<String> console() {
    return driver().manage().logs().get("browser").getAll().stream().map(log -> log.getMessage()).collect(toList());
  }

  default Object executeJavascript(String javascriptCode, Object... args) {
    return driver().executeScript(javascriptCode, args);
  }

  default Navigation goTo(String url) {
    requireNonNull(url, "The url cannot be null");

    URI uri = URI.create(url);
    if (!uri.isAbsolute()) {
      url = getBaseUrl() + url;
    }

    System.out.println("goTo " + url);

    driver().get(url);

    System.out.println(" - current url " + driver().getCurrentUrl());

    return this;
  }

  default Navigation goTo(PageObject page) {
    goTo(page.url());
    return this;
  }
}
