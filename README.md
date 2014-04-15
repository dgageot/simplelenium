# Simplelenium

A simple and robust layer on top of Selenium and PhantomJS.

## Samples

```java
goTo("/unknown");

find("h1").should().contain("Page not found");
```

```java
find("#level1").click();
find("#errors").should().contain("Errors for level 1");
find("#successes").should().contain("Successes for level 1");

find("#level2").click();
find("#errors").should().contain("Errors for level 2");
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

