# Simplelenium

A simple and robust layer on top of WebDriver and PhantomJS.

## Goal

Testing web pages with Selenium/WebDriver can prove difficult. I've seen a lot
of projects with an unstable build because of Selenium. To be fair, it's more
because of the way Selenium is used but experience showed me that using
Selenium properly is harder that one might think.

In fact I think that proper usage of Selenium must be left out of tester hands
and baked into a small, effective library. Simplelenium is my attempt to so and
it served me well.

Simplelenium deals properly and out-of-the-box with timing issues and
`StaleElementReferenceExceptions`. Give it a try and you'll be surprises how
Selenium testing can be fun again (ever?).

## Setup (Maven)

Add Simplelenium as a test dependency to your project and you are all set to go.

```xml
<dependency>
  <groupId>net.code-story</groupId>
  <artifactId>simplelenium</artifactId>
  <version>1.18</version>
  <scope>test</scope>
</dependency>
```

## Build status

[![Build Status](https://api.travis-ci.org/dgageot/simplelenium.png)](https://travis-ci.org/dgageot/simplelenium)

## Quick Start

```java
import net.codestory.simplelenium.SeleniumTest;
import org.junit.Test;

public class QuickStartTest extends SeleniumTest {
  @Test
  public void web_driver_site() {
    goTo("http://docs.seleniumhq.org/projects/webdriver/");

    find("#q").fill("StaleElementReferenceExceptions");
    find("#submit").click();

    find("a.gs-title")
      .should()
      .haveMoreItemsThan(5)
      .contain("Issue 1887 - selenium - Element not found in the cache")
      .not().contain("Selenium Rocks!");
  }
}
```

Notice the fluent api that doesn't rely on static imports. This will make your
life easier.

Lot's of finders, actions and verifications are supported. Notice that no timing
information is provided. The default settings should be ok the vast majority of
times.

## Finders

TODO

## Actions

TODO

## Verifications

TODO

## Advanced topics

### Page Objects

### Running tests in parallel

### Running tests in parallel

## Release

```bash
mvn release:clean release:prepare release:perform
```
