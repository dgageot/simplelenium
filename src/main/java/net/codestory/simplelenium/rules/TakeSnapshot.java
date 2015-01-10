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
package net.codestory.simplelenium.rules;

import com.google.common.io.Files;
import net.codestory.simplelenium.driver.CurrentWebDriver;
import org.junit.rules.TestWatcher;
import org.junit.runner.Description;

import java.io.File;
import java.io.IOException;

import static org.openqa.selenium.OutputType.BYTES;

public class TakeSnapshot extends TestWatcher {
  private final String suffix;

  private Class<?> testClass;
  private String methodName;

  public TakeSnapshot() {
    this("");
  }

  public TakeSnapshot(String suffix) {
    this.suffix = suffix;
  }

  @Override
  protected void starting(Description description) {
    this.testClass = description.getTestClass();
    this.methodName = description.getMethodName();
  }

  @Override
  protected void failed(Throwable e, Description description) {
    takeSnapshot();
  }

  public void takeSnapshot() {
    try {
      byte[] snapshotData = CurrentWebDriver.get().getScreenshotAs(BYTES);
      File snapshot = snapshotPath(testClass, methodName);
      snapshot.getParentFile().mkdirs();
      Files.write(snapshotData, snapshot);
      System.err.println("   !! A snapshot was taken here [" + snapshot.getAbsoluteFile() + "] to help you debug");
    } catch (IOException ioe) {
      throw new RuntimeException("Unable to take snapshot", ioe);
    }
  }

  public File snapshotPath(Class<?> testClass, String methodName) {
    return new File("snapshots", testClass.getSimpleName() + "_" + methodName + suffix + ".png");
  }
}
