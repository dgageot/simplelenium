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
package net.codestory.simplelenium.driver;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileLock;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import static java.util.Objects.requireNonNull;

class LockFile {
  private static final List<LockFile> LOCKS_TAKEN = new CopyOnWriteArrayList<>();

  private final File lockFile;
  private FileLock lock;

  LockFile(File lockFile) {
    this.lockFile = lockFile;
  }

  @SuppressWarnings("resource")
  public void waitLock() {
    while (true) {
      try {
        lock = new FileOutputStream(lockFile).getChannel().tryLock();
        if (lock != null) {
          LOCKS_TAKEN.add(this); // This way the lock cannot be reclaimed by the GC
          return;
        }
      } catch (Exception e) {
        // Ignore
      }

      waitBeforeRetry();
    }
  }

  public void release() {
    requireNonNull(lock, "Lock before unlock");

    try {
      LOCKS_TAKEN.remove(this);
      lock.release();
    } catch (IOException e) {
      throw new IllegalStateException("Unable to release lock");
    }
  }

  private static void waitBeforeRetry() {
    try {
      Thread.sleep(500);
    } catch (InterruptedException e) {
      // Ignore
    }
  }
}
