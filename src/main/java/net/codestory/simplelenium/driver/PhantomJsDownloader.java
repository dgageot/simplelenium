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

import org.openqa.selenium.net.PortProber;
import org.openqa.selenium.remote.UnreachableBrowserException;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.nio.file.Files;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import static java.util.concurrent.TimeUnit.SECONDS;

public class PhantomJsDownloader {
  private static final int DEFAULT_RETRY_DOWNLOAD = 4;
  private static final int DEFAULT_RETRY_CONNECT = 4;

  private final int retryDownload;
  private final int retryConnect;

  private final ThreadLocal<PhantomJSDriver> perThreadDriver = new ThreadLocal<PhantomJSDriver>() {
    @Override
    protected PhantomJSDriver initialValue() {
      return createNewDriver();
    }
  };

  public PhantomJsDownloader() {
    this(DEFAULT_RETRY_DOWNLOAD, DEFAULT_RETRY_CONNECT);
  }

  protected PhantomJsDownloader(int retryDownload, int retryConnect) {
    this.retryDownload = retryDownload;
    this.retryConnect = retryConnect;
  }

  public PhantomJSDriver getDriverForThread() {
    return perThreadDriver.get();
  }

  protected PhantomJSDriver createNewDriver() {
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
        return createNewPhantomJsDriver(phantomJsExe);
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

  protected PhantomJSDriver createNewPhantomJsDriver(File phantomJsExe) {
    try {
      URL url = new URL("http://localhost:" + PortProber.findFreePort());

      return new PhantomJSDriver(phantomJsExe, url, new File("target/phantomjs.log"));
    } catch (MalformedURLException e) {
      throw new RuntimeException(e);
    }
  }

  private void pause(long timeout) {
    try {
      SECONDS.sleep(timeout);
    } catch (InterruptedException ie) {
      // Ignore
    }
  }

  protected synchronized File downloadAndExtract() {
    File installDir = new File(new File(System.getProperty("user.home")), ".phantomjstest");
    installDir.mkdirs();

    LockFile lock = new LockFile(new File(installDir, "lock"));
    lock.waitLock();
    try {
      String url;
      File phantomJsExe;
      if (isWindows()) {
        url = "https://bitbucket.org/ariya/phantomjs/downloads/phantomjs-1.9.8-windows.zip";
        phantomJsExe = new File(installDir, "phantomjs-1.9.8-windows/phantomjs.exe");
      } else if (isMac()) {
        url = "https://bitbucket.org/ariya/phantomjs/downloads/phantomjs-1.9.8-macosx.zip";
        phantomJsExe = new File(installDir, "phantomjs-1.9.8-macosx/bin/phantomjs");
      } else if (isLinux32()) {
        url = "https://bitbucket.org/ariya/phantomjs/downloads/phantomjs-1.9.8-linux-i686.tar.bz2";
        phantomJsExe = new File(installDir, "phantomjs-1.9.8-linux-i686/bin/phantomjs");
      } else {
        url = "https://bitbucket.org/ariya/phantomjs/downloads/phantomjs-1.9.8-linux-x86_64.tar.bz2";
        phantomJsExe = new File(installDir, "phantomjs-1.9.8-linux-x86_64/bin/phantomjs");
      }

      extractExe(url, installDir, phantomJsExe);

      return phantomJsExe;
    } finally {
      lock.release();
    }
  }

  protected void extractExe(String url, File phantomInstallDir, File phantomJsExe) {
    if (phantomJsExe.exists()) {
      return;
    }

    String zipName = url.substring(url.lastIndexOf('/') + 1);
    File targetZip = new File(phantomInstallDir, zipName);
    downloadZip(url, targetZip);

    System.out.println("Extracting phantomjs");
    try {
      if (isWindows() || isMac()) {
        unzip(targetZip, phantomInstallDir);
      } else {
        executeNative(phantomInstallDir, "tar", "xjvf", zipName);
      }
    } catch (Exception e) {
      throw new IllegalStateException("Unable to unzip phantomjs from " + targetZip.getAbsolutePath(), e);
    }

    phantomJsExe.setExecutable(true);
  }

  protected void downloadZip(String url, File targetZip) {
    if (targetZip.exists()) {
      if (targetZip.length() == 0) {
        targetZip.delete();
      } else {
        return;
      }
    }

    System.out.printf("Downloading phantomjs from %s...%n", url);

    File zipTemp = new File(targetZip.getAbsolutePath() + ".temp");
    zipTemp.getParentFile().mkdirs();

    try (InputStream input = URI.create(url).toURL().openStream()) {
      Files.copy(input, zipTemp.toPath());
    } catch (IOException e) {
      throw new IllegalStateException("Unable to download phantomjs from " + url + " to " + targetZip, e);
    }

    if (!zipTemp.renameTo(targetZip)) {
      throw new IllegalStateException(String.format("Unable to rename %s to %s", zipTemp.getAbsolutePath(), targetZip.getAbsolutePath()));
    }
  }

  protected void unzip(File zip, File toDir) throws IOException {
    try (ZipFile zipFile = new ZipFile(zip)) {
      Enumeration<? extends ZipEntry> entries = zipFile.entries();
      while (entries.hasMoreElements()) {
        final ZipEntry entry = entries.nextElement();
        if (entry.isDirectory()) {
          continue;
        }

        File to = new File(toDir, entry.getName());

        File parent = to.getParentFile();
        if (!parent.exists()) {
          if (!parent.mkdirs()) {
            throw new IOException("Unable to create folder " + parent);
          }
        }

        try (InputStream input = zipFile.getInputStream(entry)) {
          Files.copy(input, to.toPath());
        }
      }
    }
  }

  protected void executeNative(File workingDir, String... commands) throws IOException, InterruptedException {
    new ProcessBuilder().command(commands).directory(workingDir).start().waitFor();
  }

  protected boolean isWindows() {
    return System.getProperty("os.name").startsWith("Windows");
  }

  protected boolean isMac() {
    return System.getProperty("os.name").startsWith("Mac OS X");
  }

  protected boolean isLinux32() {
    return System.getProperty("os.name").contains("x86");
  }
}
