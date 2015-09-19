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

import net.codestory.simplelenium.driver.chrome.ChromeDriverDownloader;
import net.codestory.simplelenium.driver.phantomjs.PhantomJsDownloader;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.remote.RemoteWebDriver;

import java.util.function.Supplier;

import static java.util.stream.Stream.of;

public enum Browser {
  PHANTOM_JS(() -> new PhantomJsDownloader().createNewDriver()),
  CHROME(() -> new ChromeDriverDownloader().createNewDriver()),
  FIREFOX(FirefoxDriver::new);

  private final Supplier<RemoteWebDriver> driverSupplier;
  private final ThreadLocal<SeleniumDriver> perThreadDriver = new ThreadLocal<SeleniumDriver>() {
    @Override
    protected SeleniumDriver initialValue() {
      return ThreadSafeDriver.makeThreadSafe(driverSupplier.get());
    }
  };

  Browser(Supplier<RemoteWebDriver> driverSupplier) {
    this.driverSupplier = driverSupplier;
  }

  public static Browser getCurrentBrowser() {
    String browserName = Configuration.BROWSER.get();

    return of(Browser.values())
      .filter(browser -> browser.name().equalsIgnoreCase(browserName))
      .findFirst()
      .orElseThrow(() -> new IllegalStateException("No selenium driver for " + browserName));
  }

  public static SeleniumDriver getCurrentDriver() {
    return getCurrentBrowser().getDriverForThread();
  }

  public SeleniumDriver getDriverForThread() {
    return perThreadDriver.get();
  }
}
