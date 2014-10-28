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

import java.util.function.Predicate;
import java.util.regex.Pattern;

class StringPredicates {
  private StringPredicates() {
    // Static class
  }

  public static Predicate<String> empty() {
    return value -> (value == null) || "".equals(value);
  }

  public static Predicate<String> equalsTo(String text) {
    return value -> value.equals(text);
  }

  public static Predicate<String> contains(String text) {
    return value -> value.contains(text);
  }

  public static Predicate<String> contains(Pattern regex) {
    return value -> regex.matcher(value).find();
  }

  public static Predicate<String> containsWord(String word) {
    Pattern pattern = Pattern.compile("\\b(" + word + ")\\b");
    return value -> pattern.matcher(value).find();
  }

  public static Predicate<String> startsWith(String text) {
    return value -> value.startsWith(text);
  }

  public static Predicate<String> endsWith(String text) {
    return value -> value.endsWith(text);
  }

  public static Predicate<String> matches(Pattern regex) {
    return value -> regex.matcher(value).matches();
  }
}
