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

import net.codestory.simplelenium.driver.chrome.ChromeDriver;
import net.codestory.simplelenium.driver.firefox.FirefoxDriver;
import net.codestory.simplelenium.driver.phantomjs.PhantomJsDownloader;

import java.util.function.Supplier;

public enum Browser {
  PHANTOM_JS(() -> new PhantomJsDownloader().createNewDriver()),
  CHROME(ChromeDriver::new),
  FIREFOX(FirefoxDriver::new);

  private final Supplier<SeleniumDriver> driverSupplier;

  Browser(Supplier<SeleniumDriver> driverSupplier) {
    this.driverSupplier = driverSupplier;
  }

  public SeleniumDriver createNewDriver() {
    return driverSupplier.get();
  }
}
