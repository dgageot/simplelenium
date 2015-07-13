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

import net.codestory.simplelenium.driver.Browser;
import org.junit.Rule;
import org.junit.Test;
import org.junit.contrib.java.lang.system.RestoreSystemProperties;

import static org.assertj.core.api.Assertions.assertThat;

public class CurrentWebDriverTest {
  @Rule
  public final RestoreSystemProperties restoreSystemProperties = new RestoreSystemProperties();

  @Test
  public void default_to_phantomJs() {
    System.setProperty("browser", "");

    Browser browser = CurrentWebDriver.getTargetBrowser();

    assertThat(browser).isEqualTo(Browser.PHANTOM_JS);
  }

  @Test
  public void phantomJs() {
    System.setProperty("browser", "phantom_js");

    Browser browser = CurrentWebDriver.getTargetBrowser();

    assertThat(browser).isEqualTo(Browser.PHANTOM_JS);
  }

  @Test
  public void chrome() {
    System.setProperty("browser", "chrome");

    Browser browser = CurrentWebDriver.getTargetBrowser();

    assertThat(browser).isEqualTo(Browser.CHROME);
  }

  @Test
  public void firefox() {
    System.setProperty("browser", "firefox");

    Browser browser = CurrentWebDriver.getTargetBrowser();

    assertThat(browser).isEqualTo(Browser.FIREFOX);
  }
}
