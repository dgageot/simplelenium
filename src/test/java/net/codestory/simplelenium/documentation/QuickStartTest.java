package net.codestory.simplelenium.documentation;

import net.codestory.simplelenium.SeleniumTest;
import org.junit.Test;

public class QuickStartTest extends SeleniumTest {
  @Test
  public void web_driver_site() {
    goTo("http://docs.seleniumhq.org/projects/webdriver/");

    find("#q").fill("StaleElementReferenceExceptions");
    find("#submit").click();

    find("a.gs-title")
      .should()
      .haveMoreItemsThan(5)
      .contain("Issue 1887 - selenium - Element not found in the cache")
      .not().contain("Selenium Rocks!");
  }
}
