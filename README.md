![Maven Build](https://github.com/adrian-herscu/qa-automation/actions/workflows/maven.yml/badge.svg)
[![Coverage Status](https://coveralls.io/repos/github/adrian-herscu/qa-automation/badge.svg?branch=main)](https://coveralls.io/github/adrian-herscu/qa-automation?branch=main)


# System Testing

Contains common build definitions.

## Application specifics

see [qa-testing-example/README.md](qa-testing-example/README.md)

## Maven Settings on local workstation

For local execution you must have a usable Maven `settings.xml` file in place,
otherwise your Maven build will fail.

Either copy [settings.xml](example-maven-settings.xml) to your `~/.m2`, or run Maven with
`-settings` option to point to this file.

## IntelliJ Configuration

Project settings are shared via `.idea` folder
see <https://www.jetbrains.com/help/idea/creating-and-managing-projects.html#share-project-through-vcs>
and <https://www.jetbrains.com/help/idea/sharing-your-ide-settings.html#settings-repository>

## Java Code Formatting

Formatting rules are stored in
[code-formatter-rules.xml](code-formatter-rules.xml) file.

In IntelliJ, this file should be imported via the Eclipse Code Formatter plugin
(which should be installed as prerequisite).

In Eclipse, this file is supported natively.

Maven builds, by default, format the code, unless launched with
'mode-build-fast' profile. 
