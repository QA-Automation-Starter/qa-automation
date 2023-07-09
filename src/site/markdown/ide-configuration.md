# IDE Configuration

IntelliJ and Eclipse should correctly open, edit and run tests if following
is installed:

## Lombok

In order to compile the test code in an IDE it is required to install the Lombok
plugin:

* [for Eclipse](https://projectlombok.org/setup/eclipse)
* [for IntelliJ](https://projectlombok.org/setup/intellij)

## TestNG

Full TestNG integration is provided via plugins:

* Eclipse -- <http://testng.org/doc/eclipse.html>
* IntelliJ -- bundled, according to <http://testng.org/doc/idea.html>

## Code Formatting

Code formatting is defined in
[code-formatter-rules.xml](https://github.com/QA-Automation-Starter/qa-automation/blob/main/code-formatter-rules.xml)
file.

In IntelliJ, this file should be imported via the
[Eclipse Code Formatter](https://plugins.jetbrains.com/plugin/6546-adapter-for-eclipse-code-formatter)
plugin (which should be installed as prerequisite).

In Eclipse, this file is supported natively.

Next: [Execution](execution.html)
