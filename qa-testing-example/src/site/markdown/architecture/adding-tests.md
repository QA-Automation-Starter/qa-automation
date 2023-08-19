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
[ThreadLocal](https://docs.oracle.com/javase/7/docs/api/java/lang/ThreadLocal.html).

Next: [Development Workflow](development-workflow.html)
