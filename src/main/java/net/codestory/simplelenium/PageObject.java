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

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

@FunctionalInterface
public interface PageObject extends DomElementFactory {
  String url();

  public static <T extends PageObject> T create(Class<T> type) {
    try {
      T pageObject = type.newInstance();

      injectMissingElements(pageObject);

      return pageObject;
    } catch (InstantiationException | IllegalAccessException e) {
      throw new IllegalArgumentException("Unable to create Page Object of type " + type, e);
    }
  }

  public static void injectMissingPageObjects(Object instance) {
    try {
      for (Field field : instance.getClass().getDeclaredFields()) {
        if (PageObject.class.isAssignableFrom(field.getType())) {
          if (!Modifier.isFinal(field.getModifiers()) && field.get(instance) == null) {
            field.set(instance, PageObject.create((Class<? extends PageObject>) field.getType()));
          }
        }
      }
    } catch (IllegalAccessException e) {
      throw new IllegalArgumentException("Unable inject missing Page Objects in object of type " + instance.getClass(), e);
    }
  }

  public static void injectMissingElements(PageObject pageObject) {
    try {
      for (Field field : pageObject.getClass().getDeclaredFields()) {
        if (DomElement.class.isAssignableFrom(field.getType())) {
          if (!Modifier.isFinal(field.getModifiers()) && field.get(pageObject) == null) {
            field.set(pageObject, new DomElement(new ByIdOrName(field.getName())));
          }
        }
      }
    } catch (IllegalAccessException e) {
      throw new IllegalArgumentException("Unable inject missing elements in Page Object of type " + pageObject.getClass(), e);
    }
  }
}
