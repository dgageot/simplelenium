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

  Should and();

  Should should();

  // Expectations

  Should contain(String... texts);

  Should match(Pattern regexp);

  Should beEnabled();

  Should beDisplayed();

  Should beSelected();

  Should haveLessItemsThan(int maxCount);

  Should haveSize(int size);

  Should haveMoreItemsThan(int minCount);

  Should beEmpty();

  Should exist();

  Should haveDimension(int width, int height);

  Should beAtLocation(int x, int y);

  Should match(Predicate<WebElement> condition);
}
