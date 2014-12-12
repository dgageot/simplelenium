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
package net.codestory.simplelenium.filters;

import org.openqa.selenium.*;

import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.function.Supplier;

import static java.util.concurrent.TimeUnit.SECONDS;

class Retry {
  public static final Retry _5_SECONDS = new Retry(5, SECONDS);
  public static final Retry _30_SECONDS = new Retry(30, SECONDS);

  private final long timeoutInMs;

  public Retry(long duration, TimeUnit timeUnit) {
    this.timeoutInMs = timeUnit.toMillis(duration);
  }

  <T> void execute(Supplier<Optional<T>> target, Consumer<T> action) {
    WebDriverException lastError = null;

    boolean retried = false;

    long start = System.currentTimeMillis();
    while ((System.currentTimeMillis() - start) < timeoutInMs) {
      try {
        Optional<T> targetElement = target.get();
        if (targetElement.isPresent()) {
          action.accept(targetElement.get());
          return;
        }
      } catch (StaleElementReferenceException e) {
        // ignore
      } catch (WebDriverException e) {
        lastError = e;
      }

      retried = true;
      System.out.print(".");
    }

    if (retried) {
      System.out.println();
    }

    if (lastError != null) {
      throw lastError;
    }
    throw new NoSuchElementException("Not found");
  }

  <T> boolean verify(Supplier<T> targetSupplier, Predicate<T> predicate) throws NoSuchElementException {
    Error error = Error.KO;

    boolean retried = false;

    long start = System.currentTimeMillis();
    while ((System.currentTimeMillis() - start) < timeoutInMs) {
      try {
        if (predicate.test(targetSupplier.get())) {
          return true;
        }

        error = Error.KO;
      } catch (InvalidElementStateException e) {
        error = Error.KO;
      } catch (NotFoundException e) {
        error = Error.NOT_FOUND;
      } catch (StaleElementReferenceException e) {
        // ignore
      }

      retried = true;
      System.out.print(".");
    }

    if (retried) {
      System.out.println();
    }

    if (error == Error.NOT_FOUND) {
      throw new NoSuchElementException("Not found");
    }
    return false;
  }

  static enum Error {
    NOT_FOUND, KO
  }
}
