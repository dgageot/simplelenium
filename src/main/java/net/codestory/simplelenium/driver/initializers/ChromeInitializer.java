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
package net.codestory.simplelenium.driver.initializers;

import net.codestory.simplelenium.driver.*;
import org.openqa.selenium.remote.RemoteWebDriver;

/**
 * Created by kag on 07/07/15.
 */
public class ChromeInitializer implements DriverInitializer {
//    ThreadLocal<SeleniumDriver> perThreadDriver = new ThreadLocal<SeleniumDriver>() {
//      @Override
//      protected SeleniumDriver initialValue() {
//        return createNewDriver();
//      }
//    };

  @Override
  public Browser getBrowser() {
    return Browser.CHROME;
  }

  @Override
  public RemoteWebDriver createNewDriver() {
    return new ChromeDriver();
  }
}
