# Simplelenium

A simple and robust layer on top of WebDriver and PhantomJS.

## Goal

Testing web pages with Selenium/WebDriver can prove difficult. I've seen a lot of projects with an unstable build
because of Selenium. To be fair, it's more because of the way Selenium is used but experience showed me that using
Selenium properly is harder that one might think.

In fact I think that proper usage of Selenium must be left out of tester hands and baked into a small, effective
library. Simplelenium is my attempt to so and it served me well.

Simplelenium deals properly and out-of-the-box with timing issues and StaleElementReferenceExceptions. Give it a try
and you'll be surprises how Selenium testing can be fun again (ever?).

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

## Samples

```java
goTo("/unknown");

find("#name").fill("Name");
find("#age").clear();
find("#form").submit();

find("h1").should().contain("Page not found");
```

```java
find("#level1").click();
find("#errors").should().contain("Errors for level 1");
find("#successes").should().contain("Successes for level 1");

find("#level2").click();
find("#errors").should().contain("Errors for level 2");
find("#errors").should().not().contain("Errors for level 3");
find("#successes").should().contain("Successes for level 2");
```

```java
goTo("/auth/signout");

find("#login").fill("john");
find("#password").fill("pwd");
find("#signin").submit();

goTo(url);
```

## Release

```bash
mvn release:clean release:prepare release:perform
```
