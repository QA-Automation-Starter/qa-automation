[![Maven Central](https://maven-badges.herokuapp.com/maven-central/dev.aherscu.qa/qa-automation/badge.svg)](https://search.maven.org/search?q=dev.aherscu)
[![Apache License 2.0](https://img.shields.io/badge/license-apache2-red.svg?style=flat-square)](http://opensource.org/licenses/Apache-2.0)
![Maven Build](https://github.com/adrian-herscu/qa-automation/actions/workflows/maven.yml/badge.svg)
[![Open Source Helpers](https://www.codetriage.com/adrian-herscu/qa-automation/badges/users.svg)](https://www.codetriage.com/adrian-herscu/qa-automation)

# Abstract

How about having an automation project running in several minutes?

How about having BDD, Selenium, and database support already baked-in?

How about having support for different configurations, environments,
and multiple device types, ready to run on your Jenkins?

Then, assuming JDK 8 and Maven 3.6+ are already installed,
on Windows it would be:

```shell
mvn --batch-mode archetype:generate ^
  -Dmaven.wagon.http.ssl.insecure=true ^
  -DarchetypeGroupId=dev.aherscu.qa ^
  -DarchetypeArtifactId=qa-testing-archetype ^
  -DarchetypeVersion=0.0.1-SNAPSHOT ^
  -DgroupId=com.acme ^
  -DartifactId=testing ^
  -Dversion=1.0-SNAPSHOT ^
  -Dpackage=com.acme.testing
```

and building it:

```shell
cd testing
mvn
```

and coding a test would look like this:

```java
public class ATest extends CalculatorTest {
    @Test(dataProvider = INTERNAL_DATA_PROVIDER)
    public void shouldCalculate(final Calculation calculation) {
        given().a_calculator(webDriver.get());

        when().typing(calculation.expression + "=");

        then().the_result(is(stringContainsInOrder("Display is", calculation.result)));
    }
}
```

After running it, above code will be nicely reflected in a BDD report.

see [Working Examples](qa-testing-example/README.md)

# More details

[QA Testing Archetype](qa-testing-archetype/README.md) generates an automation
project, with all required dependencies for TestNG, BDD-reporting, Selenium,
Appium, SouceLabs integration, Unitils, DbUnit, and many other utility libraries
which I found useful across a dozen of projects.

All above pieces are already integrated, all you have to do is:

1. derive your automation classes from specific base class
2. define your own configuration and environments

The generated project contains example tests.

see [QA Testing Example](qa-testing-example/README.md)

# How to use snapshot builds

see [QA Testing Archetype](qa-testing-archetype/README.md)

# Development Instructions

## Maven Settings

Either copy [development-maven-settings.xml](development-maven-settings.xml) to
your `~/.m2` as `settings.xml`, or run Maven with
`mvn -settings development-maven-settings.xml` from this directory.

## IntelliJ Configuration

Project settings are shared via `.idea` folder
see <https://www.jetbrains.com/help/idea/creating-and-managing-projects.html#share-project-through-vcs>
and <https://www.jetbrains.com/help/idea/sharing-your-ide-settings.html#settings-repository>

## Java Code Formatting

Formatting rules are stored in
[code-formatter-rules.xml](code-formatter-rules.xml) file.

In IntelliJ, this file should be imported via the Eclipse Code Formatter plugin
(which should be installed as prerequisite).

In Eclipse, this file is supported natively.

Maven builds, by default, format the code, unless launched with
'mode-build-fast' profile.

## GPG Public Key

<https://keys.openpgp.org/search?q=39F1B2495B0260B2D974C634F89B5DBA3AF082E0>

## Sonatype Repo

<https://s01.oss.sonatype.org/>
