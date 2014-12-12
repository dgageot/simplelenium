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
import net.codestory.simplelenium.reflection.ReflectionUtil;
import org.junit.Rule;
import org.junit.rules.RuleChain;
import org.junit.rules.TestName;
import org.junit.rules.TestWatcher;
import org.junit.runner.Description;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;

import java.io.File;
import java.io.IOException;

import static org.junit.rules.RuleChain.outerRule;
import static org.openqa.selenium.OutputType.BYTES;

public abstract class SeleniumTest implements SectionObject {
  protected SeleniumTest() {
    configureWebDriver(driver());
  }

  protected void configureWebDriver(WebDriver driver) {
    driver.manage().window().setSize(new Dimension(2048, 768));
  }

  public TestName testName = new TestName() {
    @Override
    protected void starting(Description d) {
      super.starting(d);

      System.out.println("----------------------------------------------------------------------");
      System.out.println(SeleniumTest.this.getClass().getSimpleName() + "." + getMethodName());
      System.out.println("----------------------------------------------------------------------");
    }
  };

  public TestWatcher injectMissingPageObjects = new TestWatcher() {
    @Override
    protected void starting(Description desc) {
      ReflectionUtil.injectMissingPageObjects(SeleniumTest.this);
      ReflectionUtil.injectMissingElements(SeleniumTest.this);
    }
  };

  public TestWatcher takeSnapshot = new TestWatcher() {
    @Override
    protected void failed(Throwable e, Description desc) {
      takeSnapshot("");
    }
  };

  @Rule
  public RuleChain ruleChain = outerRule(testName).around(injectMissingPageObjects).around(takeSnapshot);

  public SeleniumTest takeSnapshot(String suffix) {
    try {
      byte[] snapshotData = ((TakesScreenshot) driver()).getScreenshotAs(BYTES);
      File snapshot = snapshotPath(suffix);
      snapshot.getParentFile().mkdirs();
      Files.write(snapshotData, snapshot);
      System.err.println("   !! A snapshot was taken here [" + snapshot.getAbsoluteFile() + "] to help you debug");
    } catch (IOException ioe) {
      throw new RuntimeException("Unable to take snapshot", ioe);
    }
    return this;
  }

  protected File snapshotPath(String suffix) {
    return new File("snapshots", getClass().getSimpleName() + "_" + testName.getMethodName() + suffix + ".png");
  }

  // Override to set a base url
  protected String getDefaultBaseUrl() {
    return "";
  }

  public SeleniumTest goTo(String url) {
    Navigation.setBaseUrl(getDefaultBaseUrl());
    SectionObject.super.goTo(url);
    return this;
  }

  public Object executeJavascript(String javascriptCode) {
    WebDriver webDriver = driver();
    if (webDriver instanceof JavascriptExecutor) {
      return ((JavascriptExecutor)webDriver).executeScript(javascriptCode);
    } else {
      throw new RuntimeException("Can't execute javascript");
    }
  }
}
