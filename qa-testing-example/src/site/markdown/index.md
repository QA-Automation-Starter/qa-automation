> BDD Test Reports for this project are available on right menu:
> * The original **JGiven Report** suitable for dashboards
> * One page **QA Report** suitable for printing

# QA Testing (e2e) Overview

[Prerequisites](#prerequisites) |
[Execution](#execution) |
[HOWTO](#howto) |
[Architecture](#architecture)

> For generating a working project
> see [qa-testing-archetype](../qa-testing-archetype).

> First, for how it works -- a brief [Tutorial](src/site/markdown/TUTORIAL.md)

> Then, for what should be done -- TBD

> and, for how it should be done -- start with
> the [Prerequisites](#prerequisites)

# Prerequisites

Following instructions apply to Windows machines. There should be alternative
commands for Mac and various Linux distros.

## Basic - for all types of machines

1. Run PowerShell as Administrator -- required by Chocolatey
2. Install Chocolatey -- <https://chocolatey.org/install#individual>
3. `choco install -y jdk8`
4. `choco install -y git`

## Development Machine

1. `choco install -y tortoisegit`
2. `choco install -y intellijidea-community`

## Scheduler Machine (with Jenkins)

1. `choco install -y jenkins`
   > Now, Jenkins should be available at <http://localhost:8080>
2. install required plugins, beyond the default installation:
    * Active Directory Plugin -- allow log-in with AD/SSO credentials
    * Maven Plugin -- support for building Maven projects
3. Configure Global Security
    * Security Realm -- Active Directory
4. Global Tool Configuration
    * JDK installations -- something
      like, `%ProgramFiles%\OpenJDK\jdk-8.0.292.10-hotspot`
    * Git installations -- usually, `%ProgramFiles%\Git\cmd\git.exe`
5. Credentials
    * add Global Credentials Store (Unrestricted)
    * add SSH private key to this GitHub repo

There might be better job schedulers out there.

## Selenium

For a quick standalone Selenium Grid with several Nodes, refer
to [Selenium Hub Docker](selenium-hub-docker.yml). The Grid UI will be
at <http://localhost:4444/ui>.

### Selenium Hub

1. `choco install -y selenium --params "'/role:hub /service /autostart'"`
   > Now, Selenium Grid should be available
   at <http://localhost:4444/grid/console>
   > Additional reading <https://github.com/dhoer/choco-selenium#hub>

### Selenium Node

TBD

## Appium Node

1. `choco install -y nodejs` -- and restart the console to refresh the env vars
2. `npm --proxy http(s)://<host>:<port> install -g appium`
   > Now, **Appium** should be at `%APPDATA%\npm`.
3. Configure Appium
    1. add `nodeconfig.json` to Appium's installation directory
       ```json
        {
          "capabilities": [
            {
              "browserName": "",
              "version": "",
              "maxInstances": 1,
              "platform": "WINDOWS"
            }
          ],
          "configuration": {
            "cleanUpCycle": 2000,
            "timeout": 30000,
            "proxy": "org.openqa.grid.selenium.proxy.DefaultRemoteProxy",
            "url": "http://<machine-hostname>:4723/wd/hub",
            "host": "<machine-hostname>",
            "port": 4723,
            "maxSession": 1,
            "register": true,
            "registerCycle": 5000,
            "hubPort": 4444,
            "hubHost": "<grid-hostname>",
            "hubProtocol": "http"
          }
        }
       ```
    2. add `appium-startup.cmd` to Appium's installation directory
       ```shell
       appium.cmd --nodeconfig %APPDATA%\npm\nodeconfig.json ^
       --log %APPDATA%\npm\appium.log ^
       --log-timestamp ^
       --log-level error:debug ^
       --log-no-colors
       ```
4. add Appium to Windows' Start-up Tasks:
    1. open `%APPDATA%\Microsoft\Windows\Start Menu\Programs\Startup`
    2. create shortcut to `appium-startup.cmd`
    3. run it
   > Open the Selenium Grid Console to ensure proper registration.

## Mobile Testing

### Android Emulator

> see [Known Issues](docs/KNOWN-ISSUES.md)
> see also <https://gist.github.com/mrk-han/66ac1a724456cadf1c93f4218c6060ae>

1. `choco install -y android-sdk`
2. `cd %ANDROID_HOME%`
3. `.\tools\bin\sdkmanager.bat --no_https --proxy=http --proxy_host=<host> --proxy_port=<port>
   --install "system-images;android-30;google_apis_playstore;x86"`
4. `.\tools\bin\sdkmanager.bat --no_https --proxy=http --proxy_host=<host> --proxy_port=<port>
   --install "platform-tools"`

> `.\platform-tools\adb.exe devices` -- should list your devices either real or
> emulated

Check Android setup by running `adb devices` -- it should list your device
either real or emulated.

### iOS Simulator

> Install XCode to get the iOS Simulator.

For hybrid applications, check DOM Inspector connects to application
via <chrome://inspect/#devices>

## Windows Applications Testing

1. Enable Windows Developer Mode
2. `choco install -y winappdriver`
   > Now, **WinAppDriver** should be
   at `%ProgramFiles(x86)%\Windows Application Driver`.
3. for GUI element discovery --
   <https://github.com/microsoft/WinAppDriver/releases/tag/UIR-v1.1> or similar
   tool

If remote file access is required, then OpenSSH, or similar, is required:

`Add-WindowsCapability -Online -Name OpenSSH.Server~~~~0.0.1.0`

## Running GUI Tests on a Remote Windows Machine

For running Android Emulators, Web-Browsers, or Windows applications
on remote machines there must be an open desktop session:

1. `choco install -y autologon`
2. `autologon %USERNAME% $USERDOMAIN% <user-password>`

# Execution

## Typical Maven Usage

`mvn -Penvironment-XX,provider-saucelabs-appium,mode-XX`

The above will configure the tests to run on `XX` environment, via SauceLabs'
emulators.

SauceLabs runs will be visible at <https://app.saucelabs.com/dashboard/builds>.

## On Local Workstation

Maven `settings.xml` should contain proper proxy configuration; see
[Maven Settings on Local Workstation](./../development-maven-settings.xml)
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

TBD

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