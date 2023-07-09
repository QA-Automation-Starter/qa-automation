TODO reorganize

# Architecture

Following the rules below, prevents duplication and keeps configuration simple.

* Test scenario classes inherit from some base Test class
* Test scenarios are written only in business terms
* Technical code, namely loops, conditions, protocol access, is written in step
  methods

Whenever in doubt, look for other tests, even in the `experimental` and `attic`
packages. Do not delete tests just because technology or feature is no longer
relevant, keep them in the `attic`.

## Folder Structure

In order to find where things are located we need to follow these conventions:

- [src/main/java](src/main/java) - contains: states, actions, verifications and
  other utility code
    - `steps` package -- steps used in `scenarios`, see below
    - `model` package (optional) -- may contain data model of the tested system
    - `utils` package (optional) -- may contain utility code
- [src/test/java](src/test/java)
    - `scenarios` package -- contains test flows; these are using the fixtures,
      actions and verifications from [src/main/java](src/main/java)
    - `data` package (optional) -- may contain test data generators
- [src/test/resources](src/test/resources)
    - `scenarios` package -- contains Unitils/DBUnit data-set files
    - `data` package (optional) -- may contain test data files, namely static
      data
    - `environments` folder -- property files for various environments
    - other configuration files for logging, ssh, databases, etc.

## How to add tests

There are three cases:

1. New test cases
2. New test flows
3. New fixtures, actions, and/or verifications

### Adding New Test Cases

There are test flows which are applied with different data sets, each data set
simulating a specific use case. These data sets are applied via
TestNG's `@DataProvider` mechanism.

See the
[TestNG Data Provider](http://testng.org/doc/documentation-main.html#parameters-dataproviders)
documentation for an explanation about this mechanism.

### Adding New Test Flows

The tests should follow the given/when/then pattern:

* in the `given` steps we set up the System Under Test, e.g. hosts, flags,
  initial data in files or databases
* in the `when` steps we perform various actions on the System Under Test
* in the `then` steps we are verifying the previously performed actions yield
  the expected results

### Adding New Fixtures, Actions, and/or Verifications

Sometimes it is necessary to add new steps in order to support a new technology,
a new action, or a new verification.

See the [JGiven](http://jgiven.org/userguide/#_getting_started) documentation.

Sometimes it is required to pass some state between the steps. This should be
done
using [JGiven State Injection](http://jgiven.org/userguide/#_state_injection)
mechanism.

In order to allow tests to be run in parallel, the state should be protected via
[ThreadLocal](https://docs.oracle.com/javase/7/docs/api/java/lang/ThreadLocal.html)
.

## Diagrams

Tests can inherit from one of the `XXXSesionTest` classes:

* `Unmanaged` -- no WebDriver is managed; you have to manage it, if ever needed
* `PerClassWeb` -- one WebDriver instance is opened before any test method runs
  and closed after all finished
* `PerMethodWeb` -- one WebDriver instance opened before each test method and
  closed afterwards

TODO: add class diagrams
