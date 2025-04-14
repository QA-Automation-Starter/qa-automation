> [Development Instructions](https://github.com/QA-Automation-Starter/qa-automation/tree/main/qa-testing-archetype#readme)
>
> (GitHub project README.md)

# Generating a QA Automation Project

Assuming JDK 11 and Maven 3.6+ are already installed, on Windows it would be:

```shell
mvn --batch-mode archetype:generate ^
  -Dmaven.wagon.http.ssl.insecure=true ^
  -DarchetypeGroupId=dev.aherscu.qa ^
  -DarchetypeArtifactId=qa-testing-archetype ^
  -DarchetypeVersion=LATEST ^
  -DgroupId=com.acme ^
  -DartifactId=testing ^
  -Dversion=0.0.1-SNAPSHOT ^
  -Dpackage=com.acme.testing
```

This will generate a fully configured QA Automation project in `testing`
directory.

> NOTE: if you need to use one of the snapshot versions, then add a `settings.xml` file to your `~\.m2`
> Maven directory with proper snapshot repository section; see [development-maven-settings.xml](../development-maven-settings.xml)

The generated project inherits from [QA Testing Parent](../index.html) and
contains few exemplary tests, with all required dependencies for
[TestNG](https://testng.org/doc/documentation-main.html),
[JGiven BDD-reporting](https://jgiven.org/),
[Selenium](https://www.selenium.dev/documentation/webdriver/),
[Appium](http://appium.io/docs/en/2.0/),
[SouceLabs](https://saucelabs.com/) integration,
and many other utility libraries which I
found useful across a dozen of projects.

All above pieces are already integrated, all you have to do is:

1. derive your automation classes from specific base class
2. define your own configuration and environments
3. optionally, add support modules; currently one of:
    * [QA JGiven REST](../../qa-jgiven-rest/index.html)
    * [QA JGiven SSH](../../qa-jgiven-ssh/index.html)
    * [QA JGiven JDBC](../../qa-jgiven-jdbc/index.html)
    * [QA JGiven ElasticSearch](../../qa-jgiven-elasticsearch/index.html)
    * [QA JGiven RabbitMQ](../../qa-jgiven-rabbitmq/index.html)
    * or prepare one of yours :)

See [QA Testing Example](../qa-testing-example/index.html), for more examples.
