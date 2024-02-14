# JGiven Commons

Provides generic JGiven-style steps, most importat being the `retry` and
`eventually_assert_that` steps, both steps are controlled by a configurable
policy -- see [Failsafe](https://failsafe.dev/)

Additional features:

* Built-in
  [Apache Commons Configuration (1.0)](https://commons.apache.org/proper/commons-configuration/userguide_v1.10/user_guide.html)
  for loading test properties
* Metrics support via
  [Dropwizard Metrics](https://metrics.dropwizard.io/4.2.0/getting-started.html)
* TestNG DataProvider for CSV files
* Screenshot attachement for annotated JGiven steps

In addition, provides `ConfigurableScenarioTest` as a base class for test
scenarios.
