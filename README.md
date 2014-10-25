# Simplelenium

A simple and robust layer on top of Selenium and PhantomJS.

# Build status

[![Build Status](https://api.travis-ci.org/dgageot/simplelenium.png)](https://travis-ci.org/dgageot/simplelenium)

## Maven

```xml
<dependency>
  <groupId>net.code-story</groupId>
  <artifactId>simplelenium</artifactId>
  <version>1.17</version>
</dependency>
```

## Release

```bash
mvn release:clean release:prepare release:perform
```

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

## TODO

+ find.withName()
+ find.withClass()
+ find.withId()
+ find.withAttribute() ?
+ with.containsWord(word)
+ with.not().
+ find.first()
+ find.second()
+ find.third()
+ find.fourth()
+ find.last()
+ find.limit(n)
+ find.nth(index)
+ should haveDimension(w,h)
+ fillWith(String...)
+ fillWith(Bean)
+ inject MyDomElement with constructor(Domelement)
+ inject MyDomElement with field(Domelement)
+ takeScreenShot()
+ takeScreenShot(pathAndfileName)
+ new IsolatedTest()
+ iframes()
+ alerts()
+ windows()
+ hamcrest matchers
