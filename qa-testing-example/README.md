# QA Testing (e2e) Overview

[Prerequisites](#prerequisites) |
[Execution](#execution) |
[HOWTO](#howto) |
[Architecture](#architecture)

> First, for how it works -- a brief [Tutorial](docs/TUTORIAL.md)

> Then, for what should be done -- TBD

> and, for how it should be done -- start with the [Prerequisites](#prerequisites)

> Last, there are things which are not perfect -- [Known Issues](docs/KNOWN-ISSUES.md)

> One more thing... [Old Stuff](docs/OLD-STUFF.md) which may come back to us...

# Prerequisites

## Basic

* git 2.x -- <https://git-scm.com/download/win>; clone this repo via SSH
  only <https://docs.github.com/en/github/authenticating-to-github/connecting-to-github-with-ssh>

  IMPORTANT: for IntelliJ settings synchronization to work, generate an RSA key
  using `ssh-keygen -t rsa -b 4096 -C "your_email@example.com"`

* Java 8 SDK (Java 11+ support is experimental)
  -- <https://www.oracle.com/technetwork/java/javase/downloads/jdk8-downloads-2133151.html>
* Maven 3.6.x -- <https://maven.apache.org/download.cgi>

## Optional

* TortoiseGIT -- <https://tortoisegit.org/download/>
* IntelliJ Ultimate -- <https://www.jetbrains.com/idea/download>

## Advanced

For on-desktop mobile testing and development:

* WINDOWS
    - Appium 1.x --  <https://github.com/appium/appium-desktop/releases>
    - for Android SDK Platform, must use API Level 26 or higher otherwise Appium
      won't work
    - Android SDK Tools latest
      -- <https://developer.android.com/studio/#command-tools>
    - or, attach a real Android device via USB
      **and** install appropriate USB drivers for it
    - the Android device/emulator may need Internet access

* MAC
    - install XCode to get the iOS Simulator

NOTE: currently installing Android emulator is done via IntelliJ IDE -- see:
https://developer.android.com/studio/run/emulator

After setting up all the above your environment variables should include:

| VAR_NAME         | VAR_VALUE   |
| -----------------|-------------|
| ANDROID_HOME     | ANDROID installation (usually: %USERPROFILE%\AppData\Local\Android\Sdk)|
| ANDROID_PLATFORM | ANDROID platform (usually: %ANDROID_HOME%\platform-tools)|
| ANDROID_SDK_ROOT | ANDROID installation (usually: %ANDROID_HOME%)|
| JAVA_HOME        | JDK installation (usually: %ProgramFiles%\Java\jdk1.8.x)|
| MAVEN_HOME       | Maven installation (usually: %ProgramFiles%\Java\apache-maven-3.x)|   
| OPEN_SSH         | OPEN_SSH installation (usually: %ProgramFiles%\OpenSSH\bin)|

Your PATH variable should
include: `%ANDROID_PLATFORM%;%JAVA_HOME%\bin;%MAVEN_HOME%\bin;%OPEN_SSH%\bin`.

Check Appium by starting its server.

Check Android setup by running `adb devices` -- it should list your device
either real or emulated.

Check DOM Inspector connects to application via <chrome://inspect/#devices>

# Execution

## Typical Maven Usage

`mvn -Penvironment-XX,provider-saucelabs-appium,mode-XX`

The above will configure the tests to run on `XX` environment, via SauceLabs'
emulators.

SauceLabs runs will be visible at <https://app.saucelabs.com/dashboard/builds>.

## On Jenkins

Jobs with different profiles are available at <TBD>.

## On Local Workstation

Maven `settings.xml` should contain proper proxy configuration; see
[Maven Settings on Local Workstation](./../README.md#maven-settings-on-local-workstation)
.

Assuming everything is setup correctly, and this repository cloned, then
running `mvn` on parent project should compile everything.

Some services may require special root CA installed. While
on `$JAVA_HOME/jre/lib/security` run:

```
keytool -keystore cacerts -import -alias root_ca.pem
```

The default password is `changeit`.

## Execution Details

### Mandatory Profiles

To run various test suites in various environments, following profiles must be
selected:

* `environment-XXX`: mutually-exclusive, will run with one of properties files
  found under [environments](src/test/resources/environments)
* `testing-XXX`: mutually-exclusive, will run `testing-XXX.xml` specification,
  see [qa-testing](.)

### Per-need Profiles

#### Device profiles:

* `device-*`: will run GUI tests on specified device type; if not specified,
  will pick devices from
  [required-capabilities.properties](
  src/test/resources/required-capabilities.properties. Device types are
  specified in
  [webdriver.properties](src/test/resources/webdriver.properties).

#### Provider profiles, mutually-exclusive:

* `provider-saucelabs-*`: will run GUI tests on selected SauceLabs provider
  type; if not specified will try to run on local Appium Server.

#### Provider profiles, for application uploading:

* `provider-saucelabs-application-upload`: will upload application files for
  emulator and simulator to SauceLabs;
* `provider-saucelabs-application-real-setting`: will set the uploaded
  application identifiers for real devices on SauceLabs;
* `provider-saucelabs-application-real-upload`: will upload application files
  for real device to SauceLabs;

if the application files are already up-to-date then these profiles can be
skipped to save time and start the tests faster.

Troubleshooting --

1. Check SauceLabs access <https://app.saucelabs.com>


2. Uploading to SauceLabs now is done via Maven profiles, but this might be
   helpful for understanding how this mechanism works. Binaries under [bin](bin)
   must be uploaded to SauceLabs, see
   <https://wiki.saucelabs.com/display/DOCS/Uploading+the+App+and+Test+Files+to+Storage+Before+Execution>
   namely, run this or similar before running the tests:

```
curl -u TBD:TBD -X POST -H "Content-Type: application/octet-stream" https://saucelabs.com/rest/v1/storage/TBD/TBD.apk?overwrite=true --data-binary @TBD.apk
```

**IMPORTANT**: currently the Maven plugin used for uploading does not support
working via a proxy server --
see <https://github.com/cjnygard/rest-maven-plugin/issues/12>

### Modding Profiles

* `mode-logs-*`: sets the logging level (`debug|error|trace`); if not specified
  defaults to `info`
* `mode-es-logs-verification-skip`: skips backend logs verification
* `mode-build-fast`: disables self unit tests, static analysis and source code
  formatting
* `mode-build-quiet`: silent output for several build plugins (e.g. aspectj)
* `mode-jenkins`: sets the logging level to `info` and the build label to match
  job name and build number in Jenkins
* `mode-noscreenshots`: disables screenshot gathering during GUI tests
* `mode-dryrun`: generates full test report without executing the tests
* `mode-proxy`: will add proxy definitions to testing process

# HOWTO

## Configuring for different environments

Test properties can be overridden by adding a new profile, like this:

    <profile>
      <id>other</id>
      <properties>
        <test.properties.file>other-test.properties</test.properties.file>
      </properties>
    </profile>

In this example, we create a profile named `other` that sets the
`test.properties.file` parameter to `other-test.properties`. This parameter may
be also temporarily provided via `-D`
command line argument to Maven.

## Grouping tests

The test suites are specified via `testing-*.xml` files.

See the TestNG descriptor documentation at
<http://testng.org/doc/documentation-main.html#testng-xml>.

## Supporting other Selenium/Appium Drivers

TBD

## Running & Debugging tests in IDE

### Lombok installation

In order to compile the test code in an IDE it is required to install the Lombok
plugin:

* Eclipse -- requires running the Lombok JAR
* IntelliJ -- requires installation of a plugin

Detailed instructions here: <https://projectlombok.org/download.html>

### AspectJ installation

Screenshots and tracing logs require AspectJ instrumentation. AspectJ plugin is
required in order to compile, run and debug in an IDE:

* Eclipse -- <https://www.eclipse.org/ajdt/>
* IntelliJ -- bundled (<https://www.jetbrains.com/help/idea/aspectj.html>),
  requires additional configuration as follows:
    1. ajc (AspectJ Compiler) must be used
    2. AspectJ facet must be configured in "post-compile weave mode" for
       following modules: `qa-testing`
       , `qa-jgiven-utils` and `qa-jgiven-commons`

### TestNG installation

Full TestNG integration is provided via plugins:

* Eclipse -- <http://testng.org/doc/eclipse.html>
* IntelliJ -- bundled, according to <http://testng.org/doc/idea.html>

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
