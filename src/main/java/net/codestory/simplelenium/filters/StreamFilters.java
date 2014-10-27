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

import org.openqa.selenium.WebElement;

import java.util.Iterator;
import java.util.function.UnaryOperator;
import java.util.stream.Stream;

class StreamFilters {
  private StreamFilters() {
    // Static class
  }

  public static UnaryOperator<Stream<WebElement>> first() {
    return stream -> stream.limit(1);
  }

  public static UnaryOperator<Stream<WebElement>> second() {
    return stream -> stream.skip(1).limit(1);
  }

  public static UnaryOperator<Stream<WebElement>> third() {
    return stream -> stream.skip(2).limit(1);
  }

  public static UnaryOperator<Stream<WebElement>> nth(int index) {
    return stream -> stream.skip(index - 1).limit(1);
  }

  public static UnaryOperator<Stream<WebElement>> limit(int max) {
    return stream -> stream.limit(max);
  }

  public static UnaryOperator<Stream<WebElement>> skip(int count) {
    return stream -> stream.skip(count);
  }

  public static UnaryOperator<Stream<WebElement>> last() {
    return stream -> {
      Iterator<WebElement> iterator = stream.iterator();
      WebElement last = null;
      while (iterator.hasNext()) {
        last = iterator.next();
      }
      return (last == null) ? Stream.empty() : Stream.of(last);
    };
  }
}
