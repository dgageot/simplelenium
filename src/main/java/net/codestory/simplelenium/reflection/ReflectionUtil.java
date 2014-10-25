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

import java.lang.reflect.Field;
import java.util.function.Consumer;
import java.util.function.Supplier;

import static java.lang.String.format;
import static java.lang.reflect.Modifier.isFinal;

public class ReflectionUtil {
  private ReflectionUtil() {
    // Static class
  }

  public static void forEachFieldOfType(Class<?> type, Object target, Consumer<Field> action) {
    for (Field field : target.getClass().getDeclaredFields()) {
      if (type.isAssignableFrom(field.getType())) {
        action.accept(field);
      }
    }
  }

  public static void setIfNull(Field field, Object target, Supplier<Object> valueSupplier) {
    if (isFinal(field.getModifiers())) {
      return;
    }

    try {
      field.setAccessible(true);
      if (field.get(target) == null) {
        field.set(target, valueSupplier.get());
      }
    } catch (IllegalAccessException e) {
      throw new IllegalStateException(format("Unable to set field [%s] on instance of type [%s]", field.getName(), target.getClass().getName()));
    }
  }
}
