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
package net.codestory.simplelenium.driver;

import static java.util.stream.Stream.of;

public class CurrentWebDriver {
  private static final ThreadLocal<SeleniumDriver> perThreadDriver = new ThreadLocal<SeleniumDriver>() {
    @Override
    protected SeleniumDriver initialValue() {
      SeleniumDriver driver = getTargetBrowser().createNewDriver();
      return ThreadSafeDriver.makeThreadSafe(driver);
    }
  };

  public static Browser getTargetBrowser() {
    String browserProperty = System.getProperty("browser");
    if ((browserProperty == null) || ("".equals(browserProperty))) {
      return Browser.PHANTOM_JS;
    }

    return of(Browser.values()).filter(browser -> browser.name().equalsIgnoreCase(browserProperty)).findFirst().orElseThrow(() -> new IllegalStateException("No selenium driver for " + browserProperty));
  }

  public static SeleniumDriver get() {
    return perThreadDriver.get();
  }
}
