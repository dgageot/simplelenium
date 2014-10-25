/**
 * Copyright (C) 2013-2014 all@code-story.net
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

import com.google.common.io.Files;
import org.junit.Rule;
import org.junit.rules.RuleChain;
import org.junit.rules.TestWatcher;
import org.junit.runner.Description;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;

import java.io.File;
import java.io.IOException;

import static org.junit.rules.RuleChain.outerRule;
import static org.openqa.selenium.OutputType.BYTES;

public abstract class SeleniumTest implements PageObjectSection {
  private final WebDriver driver = createWebDriver();

  protected WebDriver createWebDriver() {
    WebDriver driver = CurrentWebDriver.get();
    driver.manage().window().setSize(new Dimension(2048, 768));
    return driver;
  }

  public TestWatcher injectMissingPageObjects = new TestWatcher() {
    @Override
    protected void starting(Description desc) {
      PageObject.injectMissingPageObjects(SeleniumTest.this);
      PageObject.injectMissingElements(SeleniumTest.this);
    }
  };

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
        File snapshot = snapshotPath(desc);
        snapshot.getParentFile().mkdirs();
        Files.write(snapshotData, snapshot);
        System.err.println("   !! A snapshot was taken here [" + snapshot.getAbsoluteFile() + "] to help you debug");
      } catch (IOException ioe) {
        throw new RuntimeException("Unable to take snapshot", ioe);
      }
    }
  };

  protected File snapshotPath(Description desc) {
    return new File("snapshots", desc.getTestClass().getSimpleName() + "_" + desc.getMethodName() + ".png");
  }

  @Rule
  public RuleChain ruleChain = outerRule(injectMissingPageObjects).around(printTestName).around(takeSnapshot);

  protected abstract String getDefaultBaseUrl();

  public SeleniumTest goTo(String url) {
    System.out.println("goTo " + url);
    driver.get(getDefaultBaseUrl() + url);
    System.out.println(" - current url " + driver.getCurrentUrl());
    return this;
  }

  public SeleniumTest goTo(PageObject page) {
    goTo(page.url());
    return this;
  }

  public String path() {
    String currentUrl = driver.getCurrentUrl();
    String defaultBaseUrl = getDefaultBaseUrl();

    if (currentUrl.startsWith(defaultBaseUrl)) {
      return currentUrl.substring(defaultBaseUrl.length());
    }
    return currentUrl;
  }
}
