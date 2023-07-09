# Getting Started

## Prerequisites

Following instructions apply to Windows machines:

1. Run PowerShell as Administrator -- required by Chocolatey
2. Install Chocolatey -- <https://chocolatey.org/install#individual>
3. `choco install -y jdk8`
4. `choco install -y maven`
5. `choco install -y intellijidea-community`
   or `choco install eclipse-java-oxygen`

There should be alternative commands for Mac and various Linux distros.

## Generate a QA Automation Project

See [QA Testing Archetype](qa-testing-parent/qa-testing-archetype/index.html)
for generating a skeleton automation project.

## Running Tests

```shell
cd testing
mvnw -Ptesting-self
```

You should see some tests running in console and Chrome openning two times.

## View the Reports

Upon completion, its JGiven report will be available under
`target/site/jgiven-reports/functional-UNDEFINED\your-username@local-execution-timestamp\html\index.html`

This JGiven report should look similar to what is published under
[QA Testing Example](qa-testing-parent/qa-testing-example/index.html).

Next: [IDE Configuration](ide-configuration.html)
