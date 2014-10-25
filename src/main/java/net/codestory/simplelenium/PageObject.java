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

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

import static net.codestory.simplelenium.reflection.ReflectionUtil.forEachFieldOfType;
import static net.codestory.simplelenium.reflection.ReflectionUtil.setIfNull;

@FunctionalInterface
public interface PageObject extends DomElementFactory {
  String url();

  public static <T extends PageObject> T create(Class<T> type) {
    Constructor<T> constructor;
    try {
      constructor = type.getDeclaredConstructor();
      constructor.setAccessible(true);
    } catch (NoSuchMethodException e) {
      throw new IllegalArgumentException("Couldn't create Page Object. Missing 0 arg constructor on type " + type, e);
    }

    T pageObject;
    try {
      pageObject = constructor.newInstance();
    } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
      throw new IllegalArgumentException("Unable to create Page Object of type " + type, e);
    }

    injectMissingElements(pageObject);
    return pageObject;
  }

  public static void injectMissingPageObjects(Object instance) {
    forEachFieldOfType(PageObject.class, instance, field -> {
      setIfNull(field, instance, () -> PageObject.create((Class<? extends PageObject>) field.getType()));
    });
  }

  public static void injectMissingElements(PageObject pageObject) {
    forEachFieldOfType(DomElement.class, pageObject, field -> {
      setIfNull(field, pageObject, () -> new DomElement(new ByIdOrName(field.getName())));
    });
  }
}
