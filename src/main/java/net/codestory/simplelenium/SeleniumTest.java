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

import net.codestory.simplelenium.rules.SeleniumRule;
import org.junit.Rule;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.WebDriver;

public abstract class SeleniumTest implements SectionObject {
  @Rule
  public final SeleniumRule seleniumRule = new SeleniumRule(this);

  protected SeleniumTest() {
    configureWebDriver(driver());
  }

  protected void configureWebDriver(WebDriver driver) {
    driver.manage().window().setSize(new Dimension(2048, 768));
  }

  protected abstract String getDefaultBaseUrl();

  public SeleniumTest takeSnapshot() {
    seleniumRule.takeSnapshot();
    return this;
  }

  public SeleniumTest goTo(String url) {
    Navigation.setBaseUrl(getDefaultBaseUrl());
    SectionObject.super.goTo(url);
    return this;
  }
}
