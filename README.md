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

Add Simplelenium as a maventest dependency to your project and you are all
set to go. **Simplelenium requires java 8**.

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

Finding elements start with either a `find("cssSelector")` or a
`find(org.openqa.selenium.By)`. There's no other choice. That's simple. You can
use the full power of cssSelector, which should be enough most of the time, or
use standard Selenium `org.openqa.selenium.By` sub-classes.

Findings can then be narrowed by additional filters, like those:

```java
find("...").withText().contains("text");
find("...").withName().startWith("text");
find("...").withId().equals("text");
find("...").withAttribute("name").matches(Pattern.compile(".*value"));
find("...").withTagName().equals("h1");
find("...").withClass().containsWord("blue");
find("...").withCssValue("color").not().endsWith("grey");
find("...").withText().startsWith("Prefix").endsWith("Suffix");
...
```

Also results can be filtered out this way:

```java
find("...").first();
find("...").second();
find("...").third();
find("...").fourth();
find("...").nth(5);
find("...").limit(10);
find("...").skip(3);
find("...").skip(5).limit(20);
find("...").last();
```

All the searches are not done until a verification is made on the elements.
Simplelenium is both lazy and tolerant to slow pages and ongoing refreshes.
You don't have to worry about it. Just write what the page should look like
and it happens within a sound period of time, the next verification is made.

We'll dig into more details in the last section.

## Verifications

The most simple verification is to check that elements are found:

```java
find(".name").should().exist();
```

Of course more complex verifications are supported:

```java
find(".name").should().contain("a word", "anything");
find(".name").should().match(Pattern.compile("regexp"));
find(".name").should().beEnabled();
find(".name").should().beDisplayed();
find(".name").should().beSelected();
find(".name").should().haveMoreItemsThan(min);
find(".name").should().haveSize(10);
find(".name").should().haveLessItemsThan(max);
find(".name").should().beEmpty();
find(".name").should().haveDimension(width, height);
find(".name").should().beAtLocation(x, y);
```

All verifications can be inverted:

```java
find(".name").should().not().contain("a word");
```

And verifications can be chained:

```java
find(".name")
  .should()
  .contain("a word")
  .contain("anything")
  .beSelected()
  .not().beDisplayed();
```

The way Simplelenium deals with timing issue is simple :

 + It tries to make the search
 + Then the verification
 + If it passes, then we're cool
 + If not, it tries again immediately with a new search to avoid Staled elements
 + It does so for at least 5 seconds

The "magic" comes from:

 + Not searching until you need to check something
 + Searching again if the check fails
 + Doing a lot of retries as quickly as possible
 + Using the fact that you tell what the page should look like and consider all
   the failures as false negatives. That is until the maximum delay is reached.

Default timeout can be set using this syntax:

```java
find(".name").should().within(1, MINUTE).contain("a word");
```

## Actions

Often, you have to interact with the page, not just make verifications.
Simplelenium supports a lot of actions. Here are some of them:

```java
find("...").fill("name");
find("...").submit();
find("...").click();
find("...").pressReturn();
find("...").sendKeys("A", "B", "C");
find("...").clear();
find("...").doubleClick();
find("...").clickAndHold();
find("...").contextClick();
find("...").release();

find("...").select("text");
find("...").deselect();
find("...").deselectByValue("value");
find("...").deselectByVisibleText("text");
find("...").deselectByIndex(index);
find("...").selectByIndex(index);
find("...").selectByValue("value");
```

If that's not enough, thre generic methods give you access to the internal
Selenium Api in a managed fashion:

To have full access to the underlying `WebElement`:

```java
find("...").execute(Consumer<? super WebElement> action);
```

To execute `actions` on the element:

```java
find("...").executeActions(String description, BiConsumer<WebElement, Actions> actionsOnElement);
```

To execute `selections` on the element:

```java
find("...").executeSelect(String description, Consumer<Select> selectOnElement);
```

Those three methods should hopefully not be used very often but it's great to
know that the full power of Selenium is there underneath.

## Advanced topics

### Page Objects

### Running tests in parallel

### Running tests in parallel

## Release

```bash
mvn release:clean release:prepare release:perform
```
