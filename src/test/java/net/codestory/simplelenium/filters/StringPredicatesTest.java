/**
 * Copyright (C) 2013-2015 all@code-story.net
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

import static org.assertj.core.api.Assertions.assertThat;

import java.util.function.Predicate;
import java.util.regex.Pattern;

import org.junit.Test;

public class StringPredicatesTest {
  @Test
  public void match_whole_word() {
    Predicate<String> pattern = StringPredicates.containsWord("word");

    assertThat(pattern.test("word")).isTrue();
    assertThat(pattern.test("before word after")).isTrue();
    assertThat(pattern.test("noword")).isFalse();
  }
  
  @Test
  public void predicate_is_empty() {
    Predicate<String> result = StringPredicates.isEmpty();

    assertThat(result.test(null)).isTrue();
    assertThat(result.test("")).isTrue();
    assertThat(result.test("notEmpty")).isFalse();
  }
  
  @Test
  public void predicate_is_null() {
    Predicate<String> result = StringPredicates.isNull();

    assertThat(result.test(null)).isTrue();
    assertThat(result.test("notNull")).isFalse();
  }
  
  @Test
  public void predicate_equals_to() {
    Predicate<String> result = StringPredicates.equalsTo("something");

    assertThat(result.test("something")).isTrue();
    assertThat(result.test("anything")).isFalse();
  }
  
  @Test
  public void predicate_contains() {
    Predicate<String> result = StringPredicates.contains("contains");

    assertThat(result.test("contains")).isTrue();
    assertThat(result.test("containsAndContainsAgain")).isTrue();
    assertThat(result.test("cake")).isFalse();
  }
  
  @Test
  public void predicate_contains_with_regex() {
    Pattern regex = Pattern.compile("\\w");
    
    Predicate<String> result = StringPredicates.contains(regex);

    assertThat(result.test("abcde")).isTrue();
    assertThat(result.test("!@#$%")).isFalse();
  }
  
  @Test
  public void predicate_starts_with() {
    
    Predicate<String> result = StringPredicates.startsWith("start");

    assertThat(result.test("startWith")).isTrue();
    assertThat(result.test("endWith")).isFalse();
  }
  
  @Test
  public void predicate_ends_with() {
    
    Predicate<String> result = StringPredicates.endsWith("end");

    assertThat(result.test("trend")).isTrue();
    assertThat(result.test("endurence")).isFalse();
  }
  
  @Test
  public void predicate_matches() {
    Pattern regex = Pattern.compile(".*is.*");
    
    Predicate<String> result = StringPredicates.matches(regex);

    assertThat(result.test("it is simple")).isTrue();
    assertThat(result.test("they are complex")).isFalse();
    
  }
}
