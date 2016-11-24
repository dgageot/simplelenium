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
package net.codestory.simplelenium.driver.firefox;

import net.codestory.simplelenium.driver.Configuration;
import net.codestory.simplelenium.driver.Downloader;
import net.codestory.simplelenium.driver.LockFile;
import org.openqa.selenium.Capabilities;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.GeckoDriverService;
import org.openqa.selenium.remote.UnreachableBrowserException;

import java.io.File;

public class FirefoxDownloader  extends Downloader {
  public FirefoxDownloader() {
    this(DEFAULT_RETRY_DOWNLOAD, DEFAULT_RETRY_CONNECT);
  }

  protected FirefoxDownloader(int retryDownload, int retryConnect) {
    super(retryConnect, retryDownload);
  }

  public FirefoxDriver createNewDriver(Capabilities desiredCapabilities) {
    System.out.println("Create a new FirefoxDriver");

    File geckoDriverExe = null;
    IllegalStateException downloadError = null;
    for (int i = retryDownload; i >= 0; i--) {
      try {
        geckoDriverExe = downloadAndExtract();
        break;
      } catch (IllegalStateException e) {
        downloadError = e;
        if (i != 0) {
          System.err.println("Unable to download GeckoDriver " + downloadError);
          pause(5);
        }
      }
    }
    if (geckoDriverExe == null) {
      throw new IllegalStateException("Unable to download GeckoDriver", downloadError);
    }

    UnreachableBrowserException connectError = null;
    for (int i = retryConnect; i >= 0; i--) {
      try {
        return createNewFirefoxDriver(geckoDriverExe, desiredCapabilities);
      } catch (UnreachableBrowserException e) {
        connectError = e;
        if (i != 0) {
          System.err.println("Unable to start GeckoDriver " + connectError);
          pause(5);
        }
      }
    }

    throw new IllegalStateException("Unable to start GeckoDriver", connectError);
  }

  public FirefoxDriver createNewFirefoxDriver(File geckoDriverExe, Capabilities desiredCapabilities) {
    GeckoDriverService geckoDriverService = new GeckoDriverService.Builder()
      .usingDriverExecutable(geckoDriverExe)
      // Use any port free or the one enforced by FIREFOXDRIVER_PORT property
      .usingPort(Configuration.FIREFOXDRIVER_PORT.getInt())
      .build();

    return new FirefoxDriver(geckoDriverService, desiredCapabilities, null);
  }

  protected synchronized File downloadAndExtract() {
    File installDir = new File(Configuration.USER_HOME.get(), ".firefoxdrivertest");
    installDir.mkdirs();

    LockFile lock = new LockFile(new File(installDir, "lock"));
    lock.waitLock();
    try {
      String url;
      File chromeDriverExe;
      if (isCustomized()) {
        url = Configuration.GECKODRIVER_URL.get();
        chromeDriverExe = new File(installDir, Configuration.GECKODRIVER_EXE.get());
      } else if (Configuration.isWindows()) {
        url = "https://github.com/mozilla/geckodriver/releases/download/v0.11.1/geckodriver-v0.11.1-win64.zip";
        chromeDriverExe = new File(installDir, "geckodriver.exe");
      } else if (Configuration.isMac()) {
        url = "https://github.com/mozilla/geckodriver/releases/download/v0.11.1/geckodriver-v0.11.1-macos.tar.gz";
        chromeDriverExe = new File(installDir, "geckodriver");
      } else if (Configuration.isLinux32()) {
        url = "https://github.com/mozilla/geckodriver/releases/download/v0.11.1/geckodriver-v0.11.1-linux32.tar.gz";
        chromeDriverExe = new File(installDir, "geckodriver");
      } else {
        url = "https://github.com/mozilla/geckodriver/releases/download/v0.11.1/geckodriver-v0.11.1-linux64.tar.gz";
        chromeDriverExe = new File(installDir, "geckodriver");
      }

      extractExe("geckodriver", url, installDir, chromeDriverExe);

      return chromeDriverExe;
    } finally {
      lock.release();
    }
  }

  protected boolean isCustomized() {
    return Configuration.GECKODRIVER_URL.get() != null
      && Configuration.GECKODRIVER_EXE.get() != null;
  }
}
