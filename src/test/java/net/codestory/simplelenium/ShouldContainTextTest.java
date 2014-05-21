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

import static org.assertj.core.api.Assertions.assertThat;
import static org.simpleframework.http.Status.*;

import java.io.*;
import java.util.concurrent.TimeUnit;

import net.codestory.simplelenium.misc.*;

import org.junit.*;

public class ShouldContainTextTest extends SeleniumTest {
  private static final TimeUnit testFriendlyTime = TimeUnit.MILLISECONDS;

  private WebServer webServer;

  private void openPageWithContent(String body) throws IOException {
    stopWebServerIfStarted();

    webServer = new WebServer((req, resp) -> {
      try {
        resp.setStatus(OK);
        resp.getPrintStream().print(body);
        resp.close();
      } catch (IOException e) {
        throw new RuntimeException(e);
      }
    }).startOnRandomPort();

    goTo("/");
  }

  @After
  public void stopWebServerIfStarted() throws IOException {
    if (webServer != null) {
      webServer.stop();
    }
  }

  @Override
  public String getDefaultBaseUrl() {
    return "http://localhost:" + webServer.port();
  }

  @Test
  public void element_does_not_exist_when_it_was_expected_to_contain_expected_text() throws Exception {
    // given
    openPageWithContent("<p>Some paragraph.</p>");

    // when
    try {
      find("section").shouldWithin(1, testFriendlyTime).contain("blah");
    }
    // then
    catch (AssertionError e) {
      assertThat(e).hasMessage("Element not found. Failed to verify that section contains(blah)");
    }
  }

  @Test
  public void element_does_not_exist_when_it_was_expected_not_to_contain_unwanted_text() throws Exception {
    // given
    openPageWithContent("<p>Some long text.</p>");

    // when
    try {
      find("section").shouldWithin(1, testFriendlyTime).not().contain("long");
    }
    // then
    catch (AssertionError e) {
      assertThat(e).hasMessage("Element not found. Failed to verify that section does not contain(long)");
    }
  }

  @Test
  public void element_exists_and_contains_expected_text() throws Exception {
    // given
    openPageWithContent("<p>Some short text.</p>");

    // when
    find("p").shouldWithin(1, testFriendlyTime).contain("short");

    // then: no AssertionError is thrown
  }

  @Test
  public void element_exists_but_does_not_contain_expected_text() throws Exception {
    // given
    openPageWithContent("<p>Some short text.</p>");

    // when
    try {
      find("p").shouldWithin(1, testFriendlyTime).contain("long");
    }
    // then
    catch (AssertionError e) {
      assertThat(e).hasMessage("Failed to verify that p contains(long). p hasText(Some short text.)");
    }
  }

  @Test
  public void element_exists_and_does_not_contain_unwanted_text() throws Exception {
    // given
    openPageWithContent("<p>Some short text.</p>");

    // when
    find("p").shouldWithin(1, testFriendlyTime).not().contain("long");

    // then: no AssertionError is thrown
  }

  @Test
  public void element_exists_but_does_contain_unwanted_text() throws Exception {
    // given
    openPageWithContent("<p>Some long text.</p>");

    // when
    try {
      find("p").shouldWithin(1, testFriendlyTime).not().contain("long");
    }
    // then
    catch (AssertionError e) {
      assertThat(e).hasMessage("Failed to verify that p does not contain(long). p hasText(Some long text.)");
    }
  }
}
