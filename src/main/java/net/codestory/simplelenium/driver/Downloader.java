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

import org.apache.commons.compress.archivers.ArchiveEntry;
import org.apache.commons.compress.archivers.tar.TarArchiveInputStream;
import org.apache.commons.compress.compressors.bzip2.BZip2CompressorInputStream;

import java.io.*;
import java.net.URI;
import java.nio.file.Files;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import static java.nio.file.StandardCopyOption.REPLACE_EXISTING;
import static java.util.concurrent.TimeUnit.SECONDS;

public abstract class Downloader {
    protected static final int DEFAULT_RETRY_DOWNLOAD = 4;
    protected static final int DEFAULT_RETRY_CONNECT = 4;
    protected final int retryDownload;
    protected final int retryConnect;

    public Downloader(int retryConnect, int retryDownload) {
        this.retryConnect = retryConnect;
        this.retryDownload = retryDownload;
    }

    protected void pause(long timeout) {
      try {
        SECONDS.sleep(timeout);
      } catch (InterruptedException ie) {
        // Ignore
      }
    }

    protected void extractExe(String driverName, String url, File installDirectory, File executable) {
      if (executable.exists()) {
        return;
      }

      String zipName = url.substring(url.lastIndexOf('/') + 1);
      File targetZip = new File(installDirectory, zipName);
      downloadZip(driverName, url, targetZip);

      System.out.println("Extracting " + driverName);
      try {
        if (url.endsWith(".zip")) {
          unzip(targetZip, installDirectory);
        } else {
          untarbz2(targetZip, installDirectory);
        }
      } catch (Exception e) {
        throw new IllegalStateException("Unable to uncompress " + driverName + " from " + targetZip.getAbsolutePath(), e);
      }

      executable.setExecutable(true);
    }

    protected void downloadZip(String driverName, String url, File targetZip) {
      if (targetZip.exists()) {
        if (targetZip.length() > 0) {
          return;
        }
        targetZip.delete();
      }

      System.out.printf("Downloading %s from %s...%n", driverName, url);

      File zipTemp = new File(targetZip.getAbsolutePath() + ".temp");
      zipTemp.getParentFile().mkdirs();

      try (InputStream input = URI.create(url).toURL().openStream()) {
        Files.copy(input, zipTemp.toPath());
      } catch (IOException e) {
        throw new IllegalStateException("Unable to download "+ driverName + " from " + url + " to " + targetZip, e);
      }

      if (!zipTemp.renameTo(targetZip)) {
        throw new IllegalStateException(String.format("Unable to rename %s to %s", zipTemp.getAbsolutePath(), targetZip.getAbsolutePath()));
      }
    }

    protected void untarbz2(File zip, File toDir) throws IOException {
      File tar = new File(zip.getAbsolutePath().replace(".tar.bz2", ".tar"));

      try (FileInputStream fin = new FileInputStream(zip);
           BufferedInputStream bin = new BufferedInputStream(fin);
           BZip2CompressorInputStream bzIn = new BZip2CompressorInputStream(bin)
      ) {
        Files.copy(bzIn, tar.toPath(), REPLACE_EXISTING);
      }

      untar(tar, toDir);
    }

    protected void unzip(File zip, File toDir) throws IOException {
      try (ZipFile zipFile = new ZipFile(zip)) {
        Enumeration<? extends ZipEntry> entries = zipFile.entries();
        while (entries.hasMoreElements()) {
          ZipEntry entry = entries.nextElement();
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
            Files.copy(input, to.toPath(), REPLACE_EXISTING);
          }
        }
      }
    }

    protected void untar(File tar, File toDir) throws IOException {
      try (FileInputStream fin = new FileInputStream(tar);
           BufferedInputStream bin = new BufferedInputStream(fin);
           TarArchiveInputStream tarInput = new TarArchiveInputStream(bin)
      ) {
        ArchiveEntry entry;
        while (null != (entry = tarInput.getNextTarEntry())) {
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

          Files.copy(tarInput, to.toPath(), REPLACE_EXISTING);
        }
      }
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
