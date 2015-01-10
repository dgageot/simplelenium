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
package net.codestory.simplelenium.reflection;

import net.codestory.simplelenium.DomElement;
import net.codestory.simplelenium.SectionObject;
import net.codestory.simplelenium.filters.LazyDomElement;
import org.openqa.selenium.support.ByIdOrName;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.function.Function;

import static java.lang.String.format;
import static java.lang.reflect.Modifier.isFinal;

public class ReflectionUtil {
  private ReflectionUtil() {
    // Static class
  }

  public static void injectMissingPageObjects(Object instance) {
    injectNullFieldsOfType(SectionObject.class, instance, field -> {
      SectionObject pageObject = newInstance((Class<? extends SectionObject>) field.getType());
      injectMissingElements(pageObject);
      return pageObject;
    });
  }

  public static void injectMissingElements(Object pageObject) {
    injectMissingPageObjects(pageObject);
    injectNullFieldsOfType(DomElement.class, pageObject, field -> new LazyDomElement(new ByIdOrName(field.getName())));
    injectNullFieldsWithConstructorParameterOfType(DomElement.class, pageObject, field -> new LazyDomElement(new ByIdOrName(field.getName())));
  }

  public static void injectNullFieldsOfType(Class<?> type, Object target, Function<Field, Object> factory) {
    for (Field field : target.getClass().getDeclaredFields()) {
      if (!isFinal(field.getModifiers()) && type.isAssignableFrom(field.getType())) {
        try {
          field.setAccessible(true);
          if (field.get(target) == null) {
            field.set(target, factory.apply(field));
          }
        } catch (IllegalAccessException e) {
          throw new IllegalStateException(format("Unable to set field [%s] on instance of type [%s]", field.getName(), target.getClass().getName()));
        }
      }
    }
  }

  public static void injectNullFieldsWithConstructorParameterOfType(Class<?> type, Object target, Function<Field, Object> factory) {
    for (Field field : target.getClass().getDeclaredFields()) {
      try {
        if (!isFinal(field.getModifiers())) {
          field.setAccessible(true);
          if (field.get(target) == null) {
            Constructor<?> constructor = field.getType().getDeclaredConstructor(type);
            constructor.setAccessible(true);
            field.set(target, constructor.newInstance(factory.apply(field)));
          }
        }
      } catch (IllegalAccessException | InstantiationException | InvocationTargetException e) {
        throw new IllegalStateException(format("Unable to set field [%s] on instance of type [%s]", field.getName(), target.getClass().getName()));
      } catch (NoSuchMethodException e) {
        // Ignore
      }
    }
  }

  public static <T> T newInstance(Class<T> type) {
    Constructor<T> constructor;
    try {
      constructor = type.getDeclaredConstructor();
      constructor.setAccessible(true);
    } catch (NoSuchMethodException e) {
      throw new IllegalArgumentException("Couldn't create Page Object. Missing 0 arg constructor on type " + type, e);
    }

    T instance;
    try {
      instance = constructor.newInstance();
    } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
      throw new IllegalArgumentException("Unable to create instance of type " + type, e);
    }

    return instance;
  }
}
