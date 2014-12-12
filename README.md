# Simplelenium

A simple and robust layer on top of
[Selenium](http://docs.seleniumhq.org/projects/webdriver/) and
[PhantomJS](http://phantomjs.org/).

## Goal

Testing web pages with Selenium can prove difficult. I've seen a lot
of projects with an unstable build because of Selenium. To be fair, it's more
because of the way Selenium is used. Although experience showed me that using
Selenium properly is harder that one might think.

In fact I think that proper usage of Selenium must be left out of tester hands
and baked into a small, effective library. Simplelenium is my attempt to so and
it served me well.

Simplelenium deals properly and out-of-the-box with timing issues and
`StaleElementReferenceExceptions`. It supports running tests in parallel
without you thinking about it. It doesn't open annoying windows since it's
using [PhantomJS](http://phantomjs.org/), a headless browser.

Give it a try and you'll be surprises how Selenium testing can be fun again
(was it ever?).

## Setup (Maven)

Add Simplelenium as a maven test dependency to your project and you are all
set to go. **Simplelenium requires java 8**.

```xml
<dependency>
  <groupId>net.code-story</groupId>
  <artifactId>simplelenium</artifactId>
  <version>1.24</version>
  <scope>test</scope>
</dependency>
```

The first time you run a test, it will download [PhantomJS](http://phantomjs.org/)
automatically for you so that nothing has to be installed on the machine.
`mvn clean install` is all one should need!

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

Finding elements start with either a
[find("cssSelector")](src/main/java/net/codestory/simplelenium/DomElementFinder.java)
or a
[find(org.openqa.selenium.By)](src/main/java/net/codestory/simplelenium/DomElementFinder.java)
. There's no other choice. That's simple. You can use the full power of cssSelector,
which should be enough most of the time, or use standard Selenium
`org.openqa.selenium.By` sub-classes.

Searching is not done until a verification is made on the elements.
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
find(".name").should().beEmpty();
find(".name").should().beEnabled();
find(".name").should().beDisplayed();
find(".name").should().beSelected();
find(".name").should().haveMoreItemsThan(min);
find(".name").should().haveSize(10);
find(".name").should().haveLessItemsThan(max);
find(".name").should().haveDimension(width, height);
find(".name").should().beAtLocation(x, y);
```

Verifications can be inverted:

```java
find(".name").should().not().contain("a word");
```

Verifications can be chained:

```java
find(".name")
  .should()
  .contain("a word")
  .contain("anything")
  .beSelected()
  .not().beDisplayed();
```

The way Simplelenium deals with timing issue is basic, yet efficient:

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

## Narrowing search

Sometime, searching elements is more difficult than using a simple css selector.
Simplelenium supports narrowing searches with additional filters, like those:

```java
find("...").withText().beingEmpty().should()...;
find("...").withText().containing("text").should()...;
find("...").withName().startingWith("text").should()...;
find("...").withId().equalTo("text").should()...;
find("...").withAttribute("name").matching(Pattern.compile(".*value")).should()...;
find("...").withTagName().equalTo("h1").should()...;
find("...").withClass().containingWord("blue").should()...;
find("...").withCssValue("color").not().endingWith("grey").should()...;
find("...").withText().startsWith("Prefix").endingWith("Suffix").should()...;
...
```

Also multiple results can be filtered out this way:

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

## Actions

Often, you have to interact with the page, not just make verifications.
Simplelenium supports a lot of actions. Here are some of them:

```java
find("...").fill("name");
find("...").submit();
find("...").click();
find("...").click(x, y);
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

If that's not enough, three generic methods give you access to the
Selenium Api underneath but in a managed fashion:

To do anything with the underlying `WebElement`:

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

Those three methods should hopefully not be used often but it's great to
know that the full power of Selenium is there underneath.

## Advanced topics

Le't say you are not impressed, what else can Simplelenium do to make writing
tests easier?

### Page Objects and Section Objects

Using Page Objects and Section Objects, one can encapsulate both the extraction
of web elements and the verification, in a more domain oriented fashion. This
also removes a lot of boilerplate code and decreases code duplication.

Let's take a look at a small example:

```java
import net.codestory.simplelenium.DomElement;
import net.codestory.simplelenium.PageObject;
import net.codestory.simplelenium.SeleniumTest;
import org.junit.Test;

public class QuickStartTest extends SeleniumTest {
  Home home;

  @Override
  protected String getDefaultBaseUrl() {
    return "http://localhost:8080/base/";
  }

  @Test
  public void check_page() {
    goTo(home);

    home.shouldDisplayHello();
    home.shouldLinkToOtherPages();
  }

  static class Home implements PageObject {
    DomElement title;
    DomElement greeting;
    DomElement links = find("a.sections");

    @Override
    public String url() {
      return "/home";
    }

    void shouldDisplayHello() {
      title.should().contain("Home page");
      greeting.should().contain("Hello");
    }

    void shouldLinkToOtherPages() {
      links.should().haveSize(5).and().contain("Section1", "Section5");
    }
  }
}
```

How cool is that? All you have to do is implement `PageObject`. Page Objects
are automatically injected into tests. So are `DomElement`s present as fields
into Page Objects. By default elements a searched by name or id but one can
use standard `find(...)` methods to override this behaviour. Same as usual.

If you make the additional effort to return `this` in Page Objects methods,
you than have a nice fluent api.

```java
@Test
public void check_page() {
  home
    .goTo()
    .shouldDisplayHello()
    .shouldLinkToOtherPages();
}

static class Home implements PageObject {
  @Override
  public String url() {
    return "/home";
  }

   Home goTo() {
    goTo(url());
    return this;
  }

   Home shouldDisplayHello() {
    ...
    return this;
  }

   Home shouldLinkToOtherPages() {
    ...
    return this;
  }
}
```

Page Objects represent a Page with a url. For sections of pages, you can
implement `SectionObject` instead. It makes it easy to split a page into
multiple reusable parts that carry their own finders and verifications.

Sections are injected automatically into tests, page objects and other sections.

### Running tests in parallel

Simplelenium is good at running tests in parallel. In fact without you doing
anything on the code side, it should just work.

Simplelenium keeps a distinct PhantomJS WebDriver for each thread. You don't
have to think about it. Let's say you configure surefire to run tests in
parallel at class or method level. Easy! You don't have to copy this
configuration, with a different syntax, into your test framework. It will just
work.

Running tests in parallel with multiple JVMs also works well. We use a
lock on the filesystem when we download PhantomJS.
I told you, you don't have to think about it.

### Tests without JUnit

Sometimes, running the tests with JUnit is not what you want. You'd like to
do your own threading and own test lifecycle. You can then use the `FluentTest`
class:

```java
import org.junit.Test;

import static java.util.stream.IntStream.range;

public class FluentTestTest {
  @Test
  public void parallel() {
    String baseUrl = ...;

    range(0, 20).parallel().forEach(index -> {
      new FluentTest(baseUrl)
        .goTo("/")
        .find("h1").should().contain("Hello World").and().not().contain("Unknown")
        .find("h2").should().contain("SubTitle")
        .find(".age").should().contain("42")
        .goTo("/list")
        .find("li").should().contain("Bob").and().contain("Joe");
    });
  }
}
```
How cool is that?

## What Simplelenium doesn't do

### Support anything else than PhantomJS

It's not difficult to add but that's not done. Pull request anyone?
I rarely face the need to test on multiple browsers.
This need clearly exists though.

### Support alerts, iframes and windows

Pull requests are you best friends.

### Reading properties from web elements

Sometimes, you want to read a property of a web element and use your own
assertions framework to verify if it's ok. That's not how Simplelenium works.
You should be able to expect what the element will look like and tell
Simplelenium to check. Otherwise you might extract a value a bit too soon and
there you are, back into timing hell, with false negative tests. You don't want
that. Trust me.

Here's the Simplelenium way of doing this:

```java
find("...").should().match(element -> /* Test something on every element found /*);
```

## Release

```bash
mvn release:clean release:prepare release:perform
```
