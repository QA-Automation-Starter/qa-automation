[![Apache License 2.0](https://img.shields.io/badge/license-apache2-red.svg?style=flat-square)](http://opensource.org/licenses/Apache-2.0)
[![Maven Central](https://img.shields.io/maven-central/v/dev.aherscu.qa/qa-automation.svg)](https://central.sonatype.com/search?q=dev.aherscu)
[![Maven Build](https://github.com/adrian-herscu/qa-automation/actions/workflows/on-main-push.yml/badge.svg)](https://github.com/QA-Automation-Starter/qa-automation/actions)
[![Sauce Test Status](https://saucelabs.com/buildstatus/oauth-adrian.herscu-d81b6)](https://app.saucelabs.com/u/oauth-adrian.herscu-d81b6)
[![JGiven Report](https://img.shields.io/badge/jgiven-report-example)](https://java.qa-automation-starter.aherscu.dev/qa-testing-parent/qa-testing-example/jgiven-reports/functional-dev/local/html/index.html)
[![Open in Dev Containers](https://img.shields.io/static/v1?label=Dev%20Containers&message=Open&color=blue&logo=visualstudiocode)](https://vscode.dev/redirect?url=vscode://ms-vscode-remote.remote-containers/cloneInVolume?url=git@github.com:QA-Automation-Starter/qa-automation.git)
[![Open in Codespaces](https://img.shields.io/static/v1?label=Codespaces&message=Open&color=blue&logo=github)](https://github.com/codespaces/new?repo=QA-Automation-Starter/qa-automation)

> > **[Usage instructions and Brief introduction](https://java.qa-automation-starter.aherscu.dev)**
>
> (this page is for developing and maintaining this project)

# Development Instructions

## Prerequisites

Following instructions apply to Windows machines:

1. Run PowerShell as Administrator -- required by Chocolatey
2. Install Chocolatey -- <https://chocolatey.org/install#individual>
3. `choco install -y temurin11`
4. `choco install -y git`
5. `choco install -y gpg4win` -- required for signing commits
6. `choco install -y tortoisegit` -- optional
7. `choco install -y intellijidea-ultimate`
   or `choco install eclipse-java-oxygen`

There should be similar commands for Mac and various Linux distros.

## Maven Settings

Either copy [development-maven-settings.xml](development-maven-settings.xml) to
your `~/.m2` as `settings.xml`, or run Maven with
`mvnw -settings development-maven-settings.xml` from this directory.

`JAVA_HOME` -- you must have this environment variable correctly pointing to your JDK installation.

## IDE Configuration

Project settings are shared via `.idea` folder
see <https://www.jetbrains.com/help/idea/creating-and-managing-projects.html#share-project-through-vcs>
and <https://www.jetbrains.com/help/idea/sharing-your-ide-settings.html#settings-repository>

### Lombok and TestNG

see [IDE Configuration](https://java.qa-automation-starter.aherscu.dev/ide-configuration.html)

### AspectJ

Screenshots and tracing logs require AspectJ instrumentation. AspectJ plugin is
required in order to compile, run and debug in an IDE:

* Eclipse -- <https://www.eclipse.org/ajdt/>
* IntelliJ -- bundled (<https://www.jetbrains.com/help/idea/aspectj.html>),
  requires additional configuration as follows:
    1. ajc (AspectJ Compiler) must be used
    2. AspectJ facet must be configured in "post-compile weave mode" for
       following modules: `qa-testing-example`, `qa-testing-utils`,
       `qa-testing-extra` and `qa-jgiven-commons`

## Testing

`mvnw` on the root project will run all tests.

## Deploying

By pushing or merging into main branch.
See the [deploy-site-for-jdk11](.github/workflows/on-main-push.yml).

## Releasing/Versioning

We use [SemVer](http://semver.org/) for versioning.

To initiate a release, run the
[Release](.github/workflows/release.yml) action.

The above has the following effects:

* `SNAPSHOT` suffix will be removed
* sources will be committed and tagged according to current version
* versions will be bumped up forming a new SNAPSHOT
* a new commit will be made on main branch
* after few hours released artifacts will appear on Maven Central
  at https://search.maven.org/search?q=dev.aherscu

## Known Issues

see [docs/KNOWN-ISSUES.md](docs/KNOWN-ISSUES.md)

## Contributing

Please read [CONTRIBUTING.md](.github/CONTRIBUTING.md) for details on our
process for submitting pull requests to us, and please ensure you follow
the [CODE_OF_CONDUCT.md](.github/CODE_OF_CONDUCT.md).

## License

This project is licensed under the Apache License - see
the [LICENSE](LICENSE) file for details.

## My GPG Public Key

For validating published artifacts, use
<https://keys.openpgp.org/search?q=39F1B2495B0260B2D974C634F89B5DBA3AF082E0>

## Importing GPG Private Key

Per OSSRH depolyment rules, all artifacts must be signed using gpg --
https://central.sonatype.org/publish/publish-maven/#gpg-signed-components

`gpg --import 39F1B2495B0260B2D974C634F89B5DBA3AF082E0.gpg`

and ensure your correct Maven Settings as described above.

## Running Web/Mobile tests on SauceLabs

Must add following environment variables before launching Maven:

* `SAUCELABS_USER`
* `SAUCELABS_PASSWORD`

These are available from https://app.saucelabs.com/

## Acknowledgments

[![JetBrains Logo (Main) logo](https://resources.jetbrains.com/storage/products/company/brand/logos/jb_beam.svg)](https://www.jetbrains.com/community/opensource/#support)
[<img src="https://jgiven.org/img/logo.png" height="80" alt="JGiven">](https://jgiven.org)
[![Testing Powered By SauceLabs](https://opensource.saucelabs.com/images/opensauce/powered-by-saucelabs-badge-red.png?sanitize=true "Testing Powered By SauceLabs")](https://saucelabs.com)
