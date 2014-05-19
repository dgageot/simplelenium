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

  <T> Verification verify(Supplier<T> targetSupplier, Check<T> check) {
    Verification result = Verification.ko();

    long start = System.currentTimeMillis();
    while ((System.currentTimeMillis() - start) < timeoutInMs) {
      try {
        Check.Result checkResult = check.execute(targetSupplier.get());
        if (checkResult.isOk()) {
          return Verification.ok();
        }

        result = Verification.ko(checkResult.message);
      } catch (NotFoundException e) {
        result = Verification.notFound();
      }
    }

    return result;
  }

  <T> Verification verify(Supplier<T> targetSupplier, Predicate<T> predicate) {
    Verification result = Verification.ko();

    long start = System.currentTimeMillis();
    while ((System.currentTimeMillis() - start) < timeoutInMs) {
      try {
        T target = targetSupplier.get();
        if (predicate.test(target)) {
          return Verification.ok();
        }

        result = Verification.ko();
      } catch (NotFoundException e) {
        result = Verification.notFound();
      }
    }

    return result;
  }
}
