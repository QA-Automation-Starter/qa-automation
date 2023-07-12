# How to Use

## Use it as is

Add [QaJGivenPerMethodReporter](xref/dev/aherscu/qa/jgiven/reporter/QaJGivenPerMethodReporter.html#QaJGivenPerMethodReporter),
[QaJGivenPerClassReporter](xref/dev/aherscu/qa/jgiven/reporter/QaJGivenPerClassReporter.html#QaJGivenPerClassReporter),
and/or [QaJGivenReporter](xref/dev/aherscu/qa/jgiven/reporter/QaJGivenReporter.html#QaJGivenReporter)
to your `testng.xml` files as a
[TestNG Listener](https://testng.org/doc/documentation-main.html#listeners-testng-xml).
For example:

```xml

<suite>
  <listeners>
    <listener
      class-name="dev.aherscu.qa.jgiven.reporter.QaJGivenPerMethodReporter"/>
  </listeners>
  ...
</suite>
```

## Customize it

Derive your reporter class from one of the reporter classes above -- see
[TestRailReporter](https://qa-automation-starter.aherscu.dev/qa-testrail-reporter/xref/dev/aherscu/qa/testrail/reporter/TestRailReporter.html#TestRailReporter)
as an example.
