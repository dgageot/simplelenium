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

import net.codestory.simplelenium.configuration.Configuration;
import net.codestory.simplelenium.driver.Browser;
import net.codestory.simplelenium.driver.DriverInitializerFactoryImpl;
import net.codestory.simplelenium.driver.SeleniumDriver;
import net.codestory.simplelenium.rules.InjectPageObjects;
import net.codestory.simplelenium.rules.PrintErrorConsole;
import net.codestory.simplelenium.rules.PrintTestName;
import net.codestory.simplelenium.rules.TakeSnapshot;
import org.junit.Rule;
import org.junit.rules.RuleChain;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.remote.RemoteWebDriver;

import static org.junit.rules.RuleChain.outerRule;

public abstract class SeleniumTest implements SectionObject {
  private final PrintTestName printTestName = new PrintTestName();
  private final InjectPageObjects injectPageObjects = new InjectPageObjects(this);
  private final TakeSnapshot takeSnapshot = new TakeSnapshot();
  private final PrintErrorConsole printErrorConsole = new PrintErrorConsole();

  @Rule
  public RuleChain ruleChain = outerRule(printTestName).around(injectPageObjects).around(printErrorConsole).around(takeSnapshot);

  static {
    Browser browser = Configuration.getInstance().getTargetBrowser();
    Context.setCurrentBrowser(browser);

    DriverInitializerFactoryImpl driverInitializerFactory = DriverInitializerFactoryImpl.getInstance();
    SeleniumDriver driver = driverInitializerFactory.getDriverInitializer(browser).createNewDriver();
    Context.setCurrentWebDriver(driver);
  }

  protected SeleniumTest() {
    configureWebDriver(Context.getCurrentWebDriver());
  }

  protected void configureWebDriver(WebDriver driver) {
    driver.manage().window().setSize(new Dimension(2048, 768));
  }

  protected abstract String getDefaultBaseUrl();

  public SeleniumTest takeSnapshot() {
    takeSnapshot.takeSnapshot();
    return this;
  }

  public SeleniumTest goTo(String url) {
    Navigation.setBaseUrl(getDefaultBaseUrl());
    SectionObject.super.goTo(url);
    return this;
  }
}
