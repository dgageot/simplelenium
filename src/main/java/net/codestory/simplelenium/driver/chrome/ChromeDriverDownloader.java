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
package net.codestory.simplelenium.driver.chrome;

import net.codestory.simplelenium.driver.Downloader;
import net.codestory.simplelenium.driver.LockFile;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.remote.UnreachableBrowserException;

import java.io.File;

// This install chromedriver
// but this is not the only piece of software we need,
// we also need chrome itself. Maybe we can check if chrome is installed
// by checking if it exists by looking at default installation of chrome :
// https://code.google.com/p/selenium/wiki/ChromeDriver
public class ChromeDriverDownloader extends Downloader {
  public static final String CHROMEDRIVER_URL = "chromedriver.url";
  public static final String CHROMEDRIVER_EXE = "chromedriver.exe";

  public ChromeDriverDownloader() {
    this(DEFAULT_RETRY_DOWNLOAD, DEFAULT_RETRY_CONNECT);
  }

  protected ChromeDriverDownloader(int retryDownload, int retryConnect) {
    super(retryConnect, retryDownload);
  }

  public ChromeDriver createNewDriver() {
    System.out.println("Create a new ChromeDriver");

    File chromeDriverExe = null;
    IllegalStateException downloadError = null;
    for (int i = retryDownload; i >= 0; i--) {
      try {
        chromeDriverExe = downloadAndExtract();
        break;
      } catch (IllegalStateException e) {
        downloadError = e;
        if (i != 0) {
          System.err.println("Unable to download ChromeDriver " + downloadError);
          pause(5);
        }
      }
    }
    if (chromeDriverExe == null) {
      throw new IllegalStateException("Unable to download ChromeDriver", downloadError);
    }

    UnreachableBrowserException connectError = null;
    for (int i = retryConnect; i >= 0; i--) {
      try {
        return createNewChromeDriver(chromeDriverExe);
      } catch (UnreachableBrowserException e) {
        connectError = e;
        if (i != 0) {
          System.err.println("Unable to start ChromeDriver " + connectError);
          pause(5);
        }
      }
    }

    throw new IllegalStateException("Unable to start ChromeDriver", connectError);
  }

  protected ChromeDriver createNewChromeDriver(File chromeDriverExe) {
    // could probably be done with custom ChromeDriverService
    System.setProperty("webdriver.chrome.driver", chromeDriverExe.toString());
    return new ChromeDriver();
  }

  protected synchronized File downloadAndExtract() {
    File installDir = new File(new File(System.getProperty("user.home")), ".chromdrivertest");
    installDir.mkdirs();

    LockFile lock = new LockFile(new File(installDir, "lock"));
    lock.waitLock();
    try {
      String url;
      File chromeDriverExe;
      if (isCustomized()) {
        url = System.getProperty(CHROMEDRIVER_URL);
        chromeDriverExe = new File(installDir, System.getProperty(CHROMEDRIVER_EXE));
      } else if (isWindows()) {
        url = "https://chromedriver.storage.googleapis.com/2.19/chromedriver_win32.zip";
        chromeDriverExe = new File(installDir, "chromedriver.exe");
      } else if (isMac()) {
        url = "https://chromedriver.storage.googleapis.com/2.19/chromedriver_mac32.zip";
        chromeDriverExe = new File(installDir, "chromedriver");
      } else if (isLinux32()) {
        url = "https://chromedriver.storage.googleapis.com/2.19/chromedriver_linux32.zip";
        chromeDriverExe = new File(installDir, "chromedriver");
      } else {
        url = "https://chromedriver.storage.googleapis.com/2.19/chromedriver_linux64.zip";
        chromeDriverExe = new File(installDir, "chromedriver");
      }

      extractExe("chromeDriver", url, installDir, chromeDriverExe);

      return chromeDriverExe;
    } finally {
      lock.release();
    }
  }

  protected boolean isCustomized() {
    return System.getProperty(CHROMEDRIVER_URL) != null && System.getProperty(CHROMEDRIVER_EXE) != null;
  }
}
