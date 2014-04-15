package net.codestory.simplelenium;

import static java.util.stream.Stream.*;

import java.util.*;
import java.util.concurrent.*;
import java.util.function.*;

import org.openqa.selenium.*;

public class Should {
  private final WebDriver driver;
  private final String selector;
  private final Retry retry;

  Should(WebDriver driver, String selector, long duration, TimeUnit timeUnit) {
    this.driver = driver;
    this.selector = selector;
    this.retry = new Retry(duration, timeUnit);
  }

  public void contain(String... texts) {
    verify(
        "contains(" + String.join(";", texts) + ")",
        () -> find(),
        element -> of(texts).allMatch(expected -> element.getText().contains(expected))
    );
  }

  public void notContain(String text) {
    verify(
        "does not contain (" + text + ")",
        () -> find(),
        element -> !element.getText().contains(text)
    );
  }

  public void haveNoMoreItemsThan(int maxCount) {
    verify(
        "has at most " + maxCount + " items",
        () -> findMultiple(),
        elements -> elements.size() <= maxCount
    );
  }

  private <T> void verify(String message, Supplier<T> target, Predicate<T> predicate) {
    String verification = "verify that " + selector + " " + message;

    System.out.println("   -> " + verification);

    if (!retry.verify(target, predicate)) {
      throw new AssertionError("Failed to " + verification);
    }
  }

  private WebElement find() {
    return driver.findElement(By.cssSelector(selector));
  }

  private List<WebElement> findMultiple() {
    return driver.findElements(By.cssSelector(selector));
  }
}
