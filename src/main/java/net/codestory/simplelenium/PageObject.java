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

import org.openqa.selenium.support.ByIdOrName;

import static net.codestory.simplelenium.reflection.ReflectionUtil.*;


@FunctionalInterface
public interface PageObject extends PageObjectSection {
  String url();

  public static <T extends PageObjectSection> T create(Class<T> type) {
    T pageObject = newInstance(type);
    injectMissingElements(pageObject);
    return pageObject;
  }

  public static void injectMissingPageObjects(Object instance) {
    forEachFieldOfType(PageObjectSection.class, instance, field -> {
      setIfNull(field, instance, () -> PageObject.create((Class<? extends PageObjectSection>) field.getType()));
    });
  }

  public static void injectMissingElements(PageObjectSection pageObject) {
    injectMissingPageObjects(pageObject);
    forEachFieldOfType(DomElement.class, pageObject, field -> {
      setIfNull(field, pageObject, () -> new DomElement(new ByIdOrName(field.getName())));
    });
  }
}
