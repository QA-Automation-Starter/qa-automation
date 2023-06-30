# About the QA Testing Parent Module

This module should be used as a Maven parent for QA Automation projects.

It provides the following facilities:

* default properties
* necessary dependencies
* necessary build plugins
* and profiles

In order to suit your needs, you may need additional properties, dependencies,
build plugins and profiles. Just add them to your project's `pom.xml`. If you
have used the
[QA Testing Archetype](https://qa-automation-starter.aherscu.dev/qa-testing-parent/qa-testing-archetype)
to generate your project, then its `pom.xml` already has the relevant sections
in it.

# Properties

Description of properties used by QA Automation projects:

* `surefire.suiteXmlFiles` -- `testng.xml` descriptor to run;
  [testing-*](#mandatory-profiles) profiles alter this value, so you may define
  different sets of tests for load, stability, functional, etc.
* `environment` -- name of environment; should match one of the subdirectories
  of `/src/test/resources/environments`; by default, `UNDEFINED`. You may need
  to define other environments to suite your needs. Activating one of
  `environment-*`, see [Mandatory Profiles](#mandatory-profiles) below, will
  change this value
* `provider` -- Selenium provider for GUI tests, `provider.local.` by default,
  meaning that GUI tests will access WebDriver directly, namely not via Selenium
  Hub; activating any of [provider-*](#provider-profiles-mutually-exclusive)
  profiles will change this value
* `device.type` -- type of device to use for GUI tests, empty by default;
  meaning that actual device type will be selected by consulting the
  `required-capabilities.properties` file in your project
* `build.label` -- will appear on generated report's title
* `build.tags` -- part of directory path of generated reports
* `test.properties.file` -- test properties file to use
* `jgiven.reports` -- directory to store generated JGiven or QA reports
* `poll.timeout` -- how many seconds to wait for an async operation to complete
* `poll.delay` -- how many seconds to wait beween polls on async operation
* `dryrun` -- whether to run dry reports, namely just generates the reports
  without actually activating or polling the system under test; empty by
  default, meaning will execute the tests
* `screenshots` -- true by default, meaning screenshots will be
  embedded/attached to reports
* `alm.href` -- http reference to use for linking tests to a requirements or
  test cases specification (defined via `@Reference` annotation)
* `title` -- final title of generated reports

You may override above defaults or add of your own to fit your needs.

# Profiles

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

* `provider-selenium`: will use hosted Selenium Grid
* `provider-saucelabs-*`: will run GUI tests on selected SauceLabs provider
  type; if not specified will try to run on local Appium Server.

If no provider is specified, will try to run with local WebDriver per
`provider.local.web`.

#### Provider profiles, for application uploading:

* `provider-saucelabs-application-upload`: will upload application files for
  emulator and simulator to SauceLabs;
* `provider-saucelabs-application-real-setting`: will set the uploaded
  application identifiers for real devices on SauceLabs;
* `provider-saucelabs-application-real-upload`: will upload application files
  for real device to SauceLabs;

if the application files are already up-to-date then these profiles can be
skipped to save time and start the tests faster.

### Modding Profiles

* `mode-logs-*`: sets the logging level (`debug|error|trace`); if not specified
  defaults to `info`
* `mode-aspectj-skip`: for debugging, it is easier to see code without AspectJ
  weaved code
* `mode-build-fast`: disables self unit tests, static analysis and source code
  formatting
* `mode-build-quiet`: silent output for several build plugins (e.g. aspectj)
* `mode-jenkins`: sets the logging level to `info` and the build label to match
  job name and build number in Jenkins
* `mode-noscreenshots`: disables screenshot gathering during GUI tests
* `mode-dryrun`: generates full test report without executing the tests
* `mode-proxy`: will add proxy definitions to testing process

### Other Profiles

* `generate-standalone` -- sometimes it is required to run the automation from
  a standalone JAR, for example, if there is no access to Maven Central

Of course, you may add your own to fit your needs.

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
