# Testing Tutorial

[Prerequisites](#prerequisites) |
[Basic Steps](#basic-steps) |
[Advanced Steps](#advanced-steps) |
[Development Workflow](#development-workflow) |
[Branching Diagram](#branching-diagram)

## Prerequisites

1. Working development environment -- see [README](../../../README.md)
2. Knowledge of testing frameworks and libraries, in this order:
    * [TestNG](https://testng.org/doc/documentation-main.html)
    * [Hamcrest](http://hamcrest.org/JavaHamcrest/tutorial)
    * [Selenium WebDriver](https://www.selenium.dev/documentation/en/)
      -- specifically the parts related to WebDriver and RemoteWebDriver clients
        - [Getting started with WebDriver](https://www.selenium.dev/documentation/en/getting_started_with_webdriver/)
        - [WebDriver](https://www.selenium.dev/documentation/en/webdriver/)
        - [Remote WebDriver client](https://www.selenium.dev/documentation/en/remote_webdriver/remote_webdriver_client/)
    * [XPath](https://www.w3.org/TR/1999/REC-xpath-19991116/)
    * [Appium Introduction](http://appium.io/docs/en/about-appium/intro/)
    * [JGiven](http://jgiven.org/userguide/)
    * [JsonPath](https://github.com/json-path/JsonPath)

## Basic Steps

1. [Plain TestNG](../src/test/java/dev/aherscu/qa/testing/example/scenarios/tutorial/_1_PlainTestNg.java)
2. [TestNG with Hamcrest](../src/test/java/dev/aherscu/qa/testing/example/scenarios/tutorial/_2_TestNgWithHamcrest.java)
3. [Testing Web Application](../src/test/java/dev/aherscu/qa/testing/example/scenarios/tutorial/_3_TestingWebApplication.java)
4. [Testing Mobile Application](../src/test/java/dev/aherscu/qa/testing/example/scenarios/tutorial/_4_TestingMobileApplication.java)
5. [Testing Mobile Application on SauceLabs](../src/test/java/dev/aherscu/qa/testing/example/scenarios/tutorial/_5_TestingMobileApplicationOnSauceLabs.java)

## Advanced Steps

1. [Testing and reporting with JGiven in parallel](../src/test/java/dev/aherscu/qa/testing/example/scenarios/tutorial/_6_TestingWithJGiven.java)
2. [Testing Windows Application](../src/test/java/dev/aherscu/qa/testing/example/scenarios/tutorial/_7_TestingWindowsApplication.java)
3. [Testing Windows with JGiven](../src/test/java/dev/aherscu/qa/testing/example/scenarios/tutorial/_8_TestingWindowsWithJGiven.java)

## Development Workflow

Branching, committing, merging and pull requests.

The main milestones are as follows:

1. **Test Initiation** --
    * branch out from `master`

    * create a class to hold the code, naming it accordingly

    * create a method to hold the test (its name is not important)

2. **Test Complete** -- code compiles, runs, fully covering its case

    * the entire class must run at once and generate pass/fail per method

    * must generate a readable JGiven report
      (something that a manual tester could execute)

    * commit and push -- you may do this repeatedly

3. **Test Integrated** -- code compiles and runs with other's people code
   changes

    * merge-back from `master`, it might have impacting changes

    * clean and compile everything, ensure _your_ test still runs

    * fix as needed, commit and push

    * pull request

    * apply code review fixes, commit and push

    * merge-forward into `master`

    * let the branch open for further fixes and/or changes

4. **Maintenance** -- we have nightly test jobs in Jenkins to run all regression
   tests and also a dry run job just to see everything compiles and configures
   correctly

    * if a test fails, it is the owner's responsibility to understand why and
      deal with it accordingly (fix it or report defect)

    * if the test case changed, it is the owner's responsibility to implement
      these changes and re-integrate

    * maintenance will occur on same branch as described in #3 above

## Branching Diagram

The above can be summarized as follows:

```
  master ----------------+-----------+--------------------------------+--------
                         |branch     |                                |merge-forward,
                         |           |                                |check the nightly test
                         |           |merge-back,                     |
                         |           |fix conflicts & test            |
  test-X                 +-----------+--------------------------------+-------> maintenance
                          initiated   completed                       |integrated
                                                                      |pull request
                                                                      |code review fixes
```
