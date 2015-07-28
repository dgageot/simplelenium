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
package net.codestory.simplelenium.driver;

import org.openqa.selenium.remote.RemoteWebDriver;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Proxy;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;

class ThreadSafeDriver {
  private ThreadSafeDriver() {
    // Static class
  }

  static SeleniumDriver makeThreadSafe(RemoteWebDriver driver) {
    Runtime.getRuntime().addShutdownHook(new Thread(driver::quit));

    return (SeleniumDriver) Proxy.newProxyInstance(
      Thread.currentThread().getContextClassLoader(),
      findInterfaces(driver),
      (proxy, method, args) -> {
        if (method.getName().equals("quit")) {
          return null; // We don't want anybody to quit() our (per thread) driver
        }

        try {
          return method.invoke(driver, args);
        } catch (InvocationTargetException e) {
          throw e.getCause();
        }
      });
  }

  private static Class[] findInterfaces(Object driver) {
    Set<Class<?>> interfaces = new LinkedHashSet<>();

    interfaces.add(SeleniumDriver.class);

    for (Class<?> parent = driver.getClass(); parent != null; ) {
      Collections.addAll(interfaces, parent.getInterfaces());
      parent = parent.getSuperclass();
    }

    return interfaces.toArray(new Class[interfaces.size()]);
  }
}