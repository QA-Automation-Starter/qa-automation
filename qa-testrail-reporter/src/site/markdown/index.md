# Abstract

Generates JGiven report in TestRail's Markdown format for each test method.
After the test suite is finished, uploads each report to TestRail according
to the `@Reference` annotation specified in each method.

# Getting Started

Add `qa-testrail-reporter` dependency to your `pom.xml`

```xml

<dependency>
  <groupId>dev.aherscu.qa</groupId>
  <artifactId>qa-testrail-reporter</artifactId>
  <version>...</version>
  <scope>test</scope>
</dependency>
```

and as a TestNG listener to your `testng.xml` files, like this:

```xml

<listener
  class-name="dev.aherscu.qa.testrail.reporter.TestRailReporter"/>
```

then set its parameters to suite your environment:

```xml

<parameter name="testRailRunId"
           value="777"/>
<parameter name="testRailUrl"
           value="https://user:password@testrail.host"/>
```

# Customizing the Template

```xml
<parameter name="templateResourceXXX"
           value="your-reporter.testrail"/>
```

where `XXX` is `TestRailReporter` or descendant class. 
