> [Development Instructions](https://github.com/QA-Automation-Starter/qa-automation#readme)

> > __How about...__
>
> * ___[starting](#getting-started) an automation project in several minutes___?...
>
> * with __BDD-reporting__, __Selenium__, __REST__, and __database support__
    already baked-in?...
>
> * and support for ___multiple device types___ in different environments?...
>
> * CI/CD ready, without any additional scripting?...
>
> * additional modules for [RabbitMQ](qa-jgiven-rabbitmq) and [Elastic Search](qa-jgiven-elasticsearch)?...
>
> * reporting connector for [TestRail](qa-testrail-reporter)?...

# Getting Started

Assuming JDK 8 and Maven 3.6+ are already installed, on Windows it would be:

```shell
mvn --batch-mode archetype:generate ^
  -Dmaven.wagon.http.ssl.insecure=true ^
  -DarchetypeGroupId=dev.aherscu.qa ^
  -DarchetypeArtifactId=qa-testing-archetype ^
  -DgroupId=com.acme ^
  -DartifactId=testing ^
  -Dversion=0.0.1-SNAPSHOT ^
  -Dpackage=com.acme.testing
```

and building it:

```shell
cd testing
mvn
```

then, coding a test would look like this:

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

After running it, above code will be nicely reflected in
a [JGiven](https://jgiven.org/) BDD report.

See `TestingWindowsWithJGiven`
under [Working Examples](https://qa-automation-starter.aherscu.dev/qa-testing-parent/qa-testing-example).

There is a JGiven Report generated as part of
[QA Testing Example](qa-testing-parent/qa-testing-example/index.html).

# More Details

[QA Testing Archetype](qa-testing-archetype/README.md) generates an automation
project inheriting from [QA Testing Parent](qa-testing-parent/README.md).

The generated project contains few exemplary tests, with all required
dependencies for TestNG, BDD-reporting, Selenium, Appium, SouceLabs integration,
Unitils, DbUnit, and many other utility libraries which I found useful across a
dozen of projects.

All above pieces are already integrated, all you have to do is:

1. derive your automation classes from specific base class
2. define your own configuration and environments
3. optionally, add support modules; currently one of:
    * [QA JGiven RabbitMQ](qa-jgiven-rabbitmq)
    * [QA JGiven ElasticSearch](qa-jgiven-elasticsearch)
    * or prepare one of yours :)

See [QA Testing Example](qa-testing-example/README.md), for more examples.
