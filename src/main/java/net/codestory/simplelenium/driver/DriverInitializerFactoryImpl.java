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

import net.codestory.simplelenium.driver.initializers.ChromeInitializer;
import net.codestory.simplelenium.driver.initializers.PhantomJsDownloader;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by kag on 07/07/15.
 */
public class DriverInitializerFactoryImpl implements DriverInitializerFactory {

  private static DriverInitializerFactoryImpl instance;

  private Map<Browser, DriverInitializer> initializers = new HashMap<Browser, DriverInitializer>();

  /**
   * Private constructor for singleton
   */
  private DriverInitializerFactoryImpl () {
    super();
  }

  public static DriverInitializerFactoryImpl getInstance () {

    if (instance == null) {
      synchronized (DriverInitializerFactoryImpl.class) {
        if (instance == null) {
          DriverInitializerFactoryImpl tmp = new DriverInitializerFactoryImpl();
          tmp.init();
          instance = tmp;
        }
      }
    }
    return instance;
  }

  /**
   * Instantiate the driver initializers
   */
  private void init() {
    Class<?>[] initializerClasses = getInitializerClasses();
    for (Class<?> cls : initializerClasses) {
      DriverInitializer initializerInstance;
      try {
        initializerInstance = (DriverInitializer)cls.newInstance();
        this.initializers.put(initializerInstance.getBrowser(), initializerInstance);
      }
      catch (InstantiationException e) {
        throw new IllegalStateException(e);
      }
      catch (IllegalAccessException e) {
        throw new IllegalStateException(e);
      }
    }
  }

  /**
   * Get the concrete initializers by using the convention that all
   * initializers are expected to be in the 'initializer' package
   * relative to the package that contains this class.
   */
  private Class<?>[] getInitializerClasses () {
    return new Class[] {
      ChromeInitializer.class,
      PhantomJsDownloader.class
    };
  }

  @Override
  public DriverInitializer getDriverInitializer(Browser browser) {
    return this.initializers.get(browser);
  }
}
