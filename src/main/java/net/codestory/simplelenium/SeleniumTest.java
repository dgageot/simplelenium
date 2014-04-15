package net.codestory.simplelenium;

import java.io.*;

import org.junit.*;
import org.junit.rules.*;
import org.junit.runner.*;
import org.openqa.selenium.*;

import com.google.common.io.*;

public abstract class SeleniumTest {
  private static final PhantomJsDownloader phantomJsDownloader = new PhantomJsDownloader();

  private WebDriver driver;

  public TestWatcher initWebDriver = new TestWatcher() {
    @Override
    protected void starting(Description desc) {
      driver = phantomJsDownloader.getDriverForThread();
      driver.manage().window().setSize(new Dimension(2048, 768));
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
        byte[] snapshotData = ((TakesScreenshot) driver).getScreenshotAs(OutputType.BYTES);
        File snapshot = new File("snapshots", desc.getTestClass().getSimpleName() + "_" + desc.getMethodName() + ".png");
        snapshot.getParentFile().mkdirs();
        Files.write(snapshotData, snapshot);
      } catch (IOException ioe) {
        throw new RuntimeException("Unable to take snapshot", ioe);
      }
    }
  };

  @Rule
  public RuleChain ruleChain = RuleChain.outerRule(initWebDriver).around(printTestName).around(takeSnapshot);

  public abstract String getDefaultBaseUrl();

  public void goTo(String url) {
    System.out.println("goTo " + url);
    driver.get(getDefaultBaseUrl() + url);
    System.out.println(" - current url " + driver.getCurrentUrl());
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
    return new DomElement(driver, selector);
  }
}
