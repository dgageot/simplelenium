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

import org.openqa.selenium.WebDriverException;
import org.openqa.selenium.net.UrlChecker;
import org.openqa.selenium.os.CommandLine;
import org.openqa.selenium.remote.*;

import java.io.File;
import java.io.IOException;
import java.net.URL;

import static java.util.Collections.singletonMap;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.openqa.selenium.net.UrlChecker.TimeoutException;
import static org.openqa.selenium.remote.DriverCommand.NEW_SESSION;
import static org.openqa.selenium.remote.DriverCommand.QUIT;
import static org.openqa.selenium.remote.http.HttpMethod.POST;

public class PhantomJSDriver extends RemoteWebDriver {
  PhantomJSDriver(File phantomJsExe, URL url, File logFile) {
    super(new PhantomJSHttpCommandExecutor(phantomJsExe, url, logFile), new DesiredCapabilities());
    Runtime.getRuntime().addShutdownHook(new Thread(super::quit));
  }

  @Override
  public void quit() {
    // We don't want anybody to quit() our (per thread) driver
  }

  static class PhantomJSHttpCommandExecutor extends HttpCommandExecutor {
    private final URL url;
    private final CommandLine process;

    PhantomJSHttpCommandExecutor(File phantomJsExe, URL url, File logFile) {
      super(singletonMap("executePhantomScript", new CommandInfo("/session/:sessionId/phantom/execute", POST)), url);
      this.url = url;
      this.process = new CommandLine(phantomJsExe.getPath(), "--webdriver=" + url.getPort(), "--webdriver-logfile=" + logFile.getAbsolutePath());
    }

    @Override
    public Response execute(Command command) throws IOException {
      if (NEW_SESSION.equals(command.getName())) {
        start();
      }

      try {
        return super.execute(command);
      } catch (Error | RuntimeException | IOException t) {
        throw t;
      } catch (Throwable t) {
        throw new WebDriverException(t);
      } finally {
        if (QUIT.equals(command.getName())) {
          stop();
        }
      }
    }

    private void start() throws IOException {
      try {
        process.executeAsync();
        new UrlChecker().waitUntilAvailable(20, SECONDS, new URL(url + "/status"));
      } catch (TimeoutException e) {
        process.checkForError();
        throw new WebDriverException("Driver failed to start.", e);
      }
    }

    private void stop() throws IOException {
      try {
        new UrlChecker().waitUntilUnavailable(3, SECONDS, new URL(url + "/shutdown"));
        process.destroy();
      } catch (TimeoutException e) {
        throw new WebDriverException("Driver failed to stop.", e);
      }
    }
  }
}
