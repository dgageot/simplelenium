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
package net.codestory.simplelenium.rules;

import net.codestory.simplelenium.driver.Browser;
import org.junit.rules.TestWatcher;
import org.junit.runner.Description;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.concurrent.atomic.AtomicLong;

import static org.openqa.selenium.OutputType.BYTES;

public class TakeSnapshot extends TestWatcher {
  private static final AtomicLong NEXT_ID = new AtomicLong();

  private Class<?> testClass;
  private String methodName;

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
      byte[] image = Browser.getCurrentDriver().getScreenshotAs(BYTES);
      File file = snapshotPath(testClass, methodName);
      write(image, file);
      System.err.println("   !! A snapshot was taken here [" + file.getAbsolutePath() + "] to help you debug");
    } catch (IOException ioe) {
      throw new RuntimeException("Unable to take snapshot", ioe);
    }
  }

  protected void write(byte[] snapshotData, File to) throws IOException {
    to.getParentFile().mkdirs();
    Files.write(to.toPath(), snapshotData);
  }

  protected File snapshotPath(Class<?> testClass, String methodName) {
    return new File("snapshots", filename(testClass, methodName));
  }

  protected String filename(Class<?> testClass, String methodName) {
    if (testClass != null) {
      return testClass.getSimpleName() + "_" + methodName + ".png";
    }

    return String.format("snapshot%03d.png", NEXT_ID.incrementAndGet());
  }
}
