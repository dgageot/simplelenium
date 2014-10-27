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

import org.openqa.selenium.WebElement;

import java.util.concurrent.TimeUnit;
import java.util.function.Predicate;
import java.util.regex.Pattern;

public interface Should extends Navigation {
  // Modifiers

  Should within(long duration, TimeUnit timeUnit);

  Should not();

  // Expectations

  ShouldChain contain(String... texts);

  ShouldChain match(Pattern regexp);

  ShouldChain beEnabled();

  ShouldChain beDisplayed();

  ShouldChain beSelected();

  ShouldChain haveLessItemsThan(int maxCount);

  ShouldChain haveSize(int size);

  ShouldChain haveMoreItemsThan(int minCount);

  ShouldChain beEmpty();

  ShouldChain exist();

  ShouldChain haveDimension(int width, int height);

  ShouldChain beAtLocation(int x, int y);

  ShouldChain match(Predicate<WebElement> condition);
}
