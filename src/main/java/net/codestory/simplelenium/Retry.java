package net.codestory.simplelenium;

import java.util.concurrent.*;
import java.util.function.*;

import org.openqa.selenium.*;

class Retry {
  private long timeoutInMs;

  Retry(long duration, TimeUnit timeUnit) {
    this.timeoutInMs = timeUnit.toMillis(duration);
  }

  <T> void execute(Supplier<T> target, Consumer<T> action) {
    WebDriverException lastError = null;

    long start = System.currentTimeMillis();
    while ((System.currentTimeMillis() - start) < timeoutInMs) {
      try {
        action.accept(target.get());
        return;
      } catch (WebDriverException e) {
        lastError = e;
      }
    }

    throw lastError;
  }

  <T> boolean verify(Supplier<T> targetSupplier, Predicate<T> predicate) {
    long start = System.currentTimeMillis();
    while ((System.currentTimeMillis() - start) < timeoutInMs) {
      try {
        if (predicate.test(targetSupplier.get())) {
          return true;
        }
      } catch (WebDriverException e) {
        // Ignore
      }
    }

    return false;
  }
}
