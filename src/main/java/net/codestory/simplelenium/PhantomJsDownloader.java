/**
 * Copyright (C) 2013 all@code-story.net
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
package net.codestory.simplelenium;

import static org.openqa.selenium.phantomjs.PhantomJSDriverService.*;

import java.io.*;
import java.lang.reflect.*;
import java.lang.reflect.Proxy;
import java.net.*;
import java.nio.file.*;
import java.util.*;
import java.util.zip.*;

import org.openqa.selenium.*;
import org.openqa.selenium.phantomjs.*;
import org.openqa.selenium.remote.*;

public class PhantomJsDownloader {
  private final boolean isWindows;
  private final boolean isMac;

  private final ThreadLocal<WebDriver> perThreadDriver = new ThreadLocal<WebDriver>() {
    @Override
    protected WebDriver initialValue() {
      return createNewDriver();
    }
  };

  public PhantomJsDownloader() {
    isWindows = System.getProperty("os.name").startsWith("Windows");
    isMac = System.getProperty("os.name").startsWith("Mac OS X");
  }

  public WebDriver getDriverForThread() {
    return perThreadDriver.get();
  }

  private WebDriver createNewDriver() {
    System.out.println("Create a new PhantomJSDriver");

    File phantomJsExe = downloadAndExtract();

    PhantomJSDriverService service = new Builder()
        .usingPhantomJSExecutable(phantomJsExe)
        .withLogFile(new File("target/phantomjs.log"))
        .build();

    PhantomJSDriver driver = new PhantomJSDriver(service, new DesiredCapabilities());

    return disableQuit(driver);
  }

  private WebDriver disableQuit(PhantomJSDriver driver) {
    Runtime.getRuntime().addShutdownHook(new Thread(driver::quit));

    return (WebDriver) Proxy.newProxyInstance(getClass().getClassLoader(), new Class[]{WebDriver.class, TakesScreenshot.class, JavascriptExecutor.class}, (proxy, method, args) -> {
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

  private synchronized File downloadAndExtract() {
    File installDir = new File(new File(System.getProperty("user.home")), ".phantomjstest");

    String url;
    File phantomJsExe;
    if (isWindows) {
      url = "http://cdn.bitbucket.org/ariya/phantomjs/downloads/phantomjs-1.9.7-windows.zip";
      phantomJsExe = new File(installDir, "phantomjs-1.9.7-windows/phantomjs.exe");
    } else if (isMac) {
      url = "http://cdn.bitbucket.org/ariya/phantomjs/downloads/phantomjs-1.9.7-macosx.zip";
      phantomJsExe = new File(installDir, "phantomjs-1.9.7-macosx/bin/phantomjs");
    } else {
      url = "http://cdn.bitbucket.org/ariya/phantomjs/downloads/phantomjs-1.9.7-linux-x86_64.tar.bz2";
      phantomJsExe = new File(installDir, "phantomjs-1.9.7-linux-x86_64/bin/phantomjs");
    }

    extractExe(url, installDir, phantomJsExe);

    phantomJsExe.setExecutable(true);

    return phantomJsExe;
  }

  private void extractExe(String url, File phantomInstallDir, File phantomJsExe) {
    if (phantomJsExe.exists()) {
      return;
    }

    String zipName = url.substring(url.lastIndexOf('/') + 1);
    File targetZip = new File(phantomInstallDir, zipName);
    downloadZip(url, targetZip);

    System.out.println("Extracting phantomjs");
    try {
      if (isWindows || isMac) {
        unzip(targetZip, phantomInstallDir);
      } else {
        executeNative(phantomInstallDir, "tar", "xjvf", zipName);
      }
    } catch (Exception e) {
      throw new IllegalStateException("Unable to unzip phantomjs from " + targetZip.getAbsolutePath(), e);
    }
  }

  private void downloadZip(String url, File targetZip) {
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
      throw new IllegalStateException("Unable to download phantomjs from " + url, e);
    }

    if (!zipTemp.renameTo(targetZip)) {
      throw new IllegalStateException(String.format("Unable to rename %s to %s", zipTemp.getAbsolutePath(), targetZip.getAbsolutePath()));
    }
  }

  private static void unzip(File zip, File toDir) throws IOException {
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

  private static void executeNative(File workingDir, String... commands) throws IOException, InterruptedException {
    new ProcessBuilder().command(commands).directory(workingDir).start().waitFor();
  }
}
