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
package net.codestory.simplelenium.configuration;

import net.codestory.simplelenium.driver.Browser;
import org.junit.After;
import org.junit.Test;

import java.util.Set;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * Created by kag on 08/07/15.
 */
public class ConfigurationTest {

  @After
  public void tearDown () {
    Configuration.clearConfiguration();
    System.getProperties().remove("browser");
  }

  @Test
  public void testGetTargetBrowsers () {
    // NOTE: after each operation, 'clearConfiguration' is required since the target browsers are cached internally:

    // check default browser:
//    Set<Browser> browsers = Configuration.getInstance().getTargetBrowsers();
//    assertEquals(1, browsers.size());
//    assertEquals(Browser.PHANTOM_JS, browsers.iterator().next());
    Browser browser = Configuration.getInstance().getTargetBrowser();
    assertEquals(Browser.PHANTOM_JS, browser);
    Configuration.clearConfiguration();

    setProperty("browser", "chrome");
//    browsers = Configuration.getInstance().getTargetBrowsers();
//    assertEquals(1, browsers.size());
////    assertTrue(browsers.contains(Browser.IE));
////    assertTrue(browsers.contains(Browser.FIREFOX));
//    assertTrue(browsers.contains(Browser.CHROME));
    browser = Configuration.getInstance().getTargetBrowser();
    assertEquals(Browser.CHROME, browser);
    Configuration.clearConfiguration();

//    // try 'all' browsers
//    setProperty("browser", "all");
//    browsers = Configuration.getInstance().getTargetBrowsers();
//    assertEquals(Browser.values().length, browsers.size());
//    for (Browser browser: Browser.values()) {
//      assertTrue(browsers.contains(browser));
//    }
  }

  private void setProperty (String key, String value) {
    System.setProperty(key, value);
  }
}
