# Simplelenium

A simple and robust layer on top of Selenium and PhantomJS.

## Maven

```xml
<dependency>
  <groupId>net.code-story</groupId>
  <artifactId>simplelenium</artifactId>
  <version>1.2</version>
</dependency>
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

+ Clean up code
+ Clean up dependencies
+ Support more actions
+ Support more assertions

