# Getting Started

## Prerequisites

Following instructions apply to Windows machines:

1. Run PowerShell as Administrator -- required by Chocolatey
2. Install Chocolatey -- <https://chocolatey.org/install#individual>
3. `choco install -y temurin11`
4. `choco install -y maven`
5. `choco install -y intellijidea-ultimate` or `choco install eclipse`

There should be alternative commands for Mac and various Linux distros.

## Generate a QA Automation Project

See [QA Testing Archetype](qa-testing-parent/qa-testing-archetype/index.html)
for generating a skeleton automation project.

## Running Tests

```shell
cd testing
mvnw -Ptesting-self
```

You should see some tests running in console and Chrome opening two times.

## Browsing the Reports

By default, two kinds of reports will be generated
under `target/site/jgiven-reports`, looking like these:

* [JGiven Dashboard](qa-testing-parent/qa-testing-example/jgiven-reports/functional-dev/local/html/index.html) --
  an interactive site
* [QA Report](qa-testing-parent/qa-testing-example/jgiven-reports/functional-dev/local/qa-html/qa-jgiven-reporter.html) --
  single HTML document

Next: [IDE Configuration](ide-configuration.html)
