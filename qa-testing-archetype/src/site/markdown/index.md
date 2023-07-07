> [Development Instructions](https://github.com/QA-Automation-Starter/qa-automation/tree/main/qa-testing-archetype#readme)
> 
> (GitHub project README.md)

# Generating a QA Automation Project

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

This will generate a fully configured QA Automation project in `testing`
directory.

The generated project inherits
from [QA Testing Parent](qa-testing-parent/index.html) and contains few
exemplary tests, with all requireddependencies for TestNG, BDD-reporting,
Selenium, Appium, SouceLabs integration, Unitils, DbUnit, and many other utility
libraries which I found useful across a dozen of projects.

All above pieces are already integrated, all you have to do is:

1. derive your automation classes from specific base class
2. define your own configuration and environments
3. optionally, add support modules; currently one of:
    * [QA JGiven RabbitMQ](qa-jgiven-rabbitmq/index.html)
    * [QA JGiven ElasticSearch](qa-jgiven-elasticsearch/index.html)
    * or prepare one of yours :)

See [QA Testing Example](qa-testing-example/index.html), for more examples.
