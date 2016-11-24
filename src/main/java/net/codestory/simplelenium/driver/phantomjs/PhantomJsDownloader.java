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
package net.codestory.simplelenium.driver.phantomjs;

import net.codestory.simplelenium.driver.Configuration;
import net.codestory.simplelenium.driver.Downloader;
import net.codestory.simplelenium.driver.LockFile;
import org.openqa.selenium.Capabilities;
import org.openqa.selenium.net.PortProber;
import org.openqa.selenium.remote.UnreachableBrowserException;

import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;

public class PhantomJsDownloader extends Downloader {
  public PhantomJsDownloader() {
    this(DEFAULT_RETRY_DOWNLOAD, DEFAULT_RETRY_CONNECT);
  }

  protected PhantomJsDownloader(int retryDownload, int retryConnect) {
    super(retryConnect, retryDownload);
  }

  public PhantomJSDriver createNewDriver(Capabilities desiredCapabilities) {
    System.out.println("Create a new PhantomJSDriver");

    File phantomJsExe = null;
    IllegalStateException downloadError = null;
    for (int i = retryDownload; i >= 0; i--) {
      try {
        phantomJsExe = downloadAndExtract();
        break;
      } catch (IllegalStateException e) {
        downloadError = e;
        if (i != 0) {
          System.err.println("Unable to download PhantomJS " + downloadError);
          pause(5);
        }
      }
    }
    if (phantomJsExe == null) {
      throw new IllegalStateException("Unable to download PhantomJS", downloadError);
    }

    UnreachableBrowserException connectError = null;
    for (int i = retryConnect; i >= 0; i--) {
      try {
        return createNewPhantomJsDriver(phantomJsExe, desiredCapabilities);
      } catch (UnreachableBrowserException e) {
        connectError = e;
        if (i != 0) {
          System.err.println("Unable to start PhantomJS " + connectError);
          pause(5);
        }
      }
    }

    throw new IllegalStateException("Unable to start PhantomJS", connectError);
  }

  protected PhantomJSDriver createNewPhantomJsDriver(File phantomJsExe, Capabilities desiredCapabilities) {
    try {
      URL url = new URL("http://localhost:" + PortProber.findFreePort());

      return new PhantomJSDriver(phantomJsExe, url, new File("target/phantomjs.log"), desiredCapabilities);
    } catch (MalformedURLException e) {
      throw new RuntimeException(e);
    }
  }

  protected synchronized File downloadAndExtract() {
    File installDir = new File(Configuration.USER_HOME.get(), ".phantomjstest");
    installDir.mkdirs();

    LockFile lock = new LockFile(new File(installDir, "lock"));
    lock.waitLock();
    try {
      String url;
      File phantomJsExe;
      if (isCustomized()) {
        url = Configuration.PHANTOMJS_URL.get();
        phantomJsExe = new File(installDir, Configuration.PHANTOMJS_EXE.get());
      } else if (Configuration.isWindows()) {
        url = "https://bitbucket.org/ariya/phantomjs/downloads/phantomjs-2.1.1-windows.zip";
        phantomJsExe = new File(installDir, "phantomjs-2.1.1-windows/phantomjs.exe");
      } else if (Configuration.isMac()) {
        url = "https://bitbucket.org/ariya/phantomjs/downloads/phantomjs-2.1.1-macosx.zip";
        phantomJsExe = new File(installDir, "phantomjs-2.1.1-macosx/bin/phantomjs");
      } else if (Configuration.isLinux32()) {
        url = "https://bitbucket.org/ariya/phantomjs/downloads/phantomjs-2.1.1-linux-i686.tar.bz2";
        phantomJsExe = new File(installDir, "phantomjs-2.1.1-linux-i686/bin/phantomjs");
      } else {
        url = "https://bitbucket.org/ariya/phantomjs/downloads/phantomjs-2.1.1-linux-x86_64.tar.bz2";
        phantomJsExe = new File(installDir, "phantomjs-2.1.1-linux-x86_64/bin/phantomjs");
      }

      extractExe("phantomJs", url, installDir, phantomJsExe);

      return phantomJsExe;
    } finally {
      lock.release();
    }
  }

  protected boolean isCustomized() {
    return Configuration.PHANTOMJS_URL.get() != null
        && Configuration.PHANTOMJS_EXE.get() != null;
  }
}
