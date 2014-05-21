/**
 * Copyright (C) 2013 all@code-story.net
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

import static org.junit.rules.RuleChain.*;
import static org.openqa.selenium.OutputType.*;

import java.io.*;

import org.junit.*;
import org.junit.rules.*;
import org.junit.runner.*;
import org.openqa.selenium.*;

import com.google.common.io.*;

public abstract class SeleniumTest {
  private static final PhantomJsDownloader phantomJsDownloader = new PhantomJsDownloader();

  private final WebDriver driver = createWebDriver();

  protected WebDriver createWebDriver() {
    WebDriver driver = phantomJsDownloader.getDriverForThread();
    driver.manage().window().setSize(new Dimension(2048, 768));
    return driver;
  }

  public TestWatcher printTestName = new TestWatcher() {
    @Override
    protected void starting(Description desc) {
      System.out.println("-----------------------------------------");
      System.out.println(desc.getTestClass().getSimpleName() + "." + desc.getMethodName());
      System.out.println("-----------------------------------------");
    }
  };

  public TestWatcher takeSnapshot = new TestWatcher() {
    @Override
    protected void failed(Throwable e, Description desc) {
      if (driver == null) {
        return;
      }

      try {
        byte[] snapshotData = ((TakesScreenshot) driver).getScreenshotAs(BYTES);
        File snapshot = new File("snapshots", desc.getTestClass().getSimpleName() + "_" + desc.getMethodName() + ".png");
        snapshot.getParentFile().mkdirs();
        Files.write(snapshotData, snapshot);
      } catch (IOException ioe) {
        throw new RuntimeException("Unable to take snapshot", ioe);
      }
    }
  };

  @Rule
  public RuleChain ruleChain = outerRule(printTestName).around(takeSnapshot);

  public abstract String getDefaultBaseUrl();

  public void goTo(String url) {
    System.out.println("goTo " + url);
    driver.get(getDefaultBaseUrl() + url);
    System.out.println(" - current url " + driver.getCurrentUrl());
  }

  public WebDriver getDriver() {
    return driver;
  }

  public String currentUrl() {
    return driver.getCurrentUrl();
  }

  public String title() {
    return driver.getTitle();
  }

  public String pageSource() {
    return driver.getPageSource();
  }

  public DomElement find(String selector) {
    return find(By.cssSelector(selector));
  }

  public DomElement find(By selector) {
    return new DomElement(driver, selector);
  }
}
