# Introduction

Provides default configuration for QA Automation projects.
Also provides Maven profiles for managing logs, devices, providers
and test suites. There is a special profile for generating a standalone test
executable to used in constrained environments.

If your project already has a parent,
see [QA Testing Starter](../qa-testing-starter/README.md).
This module should be used as a Maven parent for QA Automation projects.

It provides the following facilities:

* default properties
* necessary dependencies
* necessary build plugins
* some useful profiles

In order to suit your needs, you may need additional properties, dependencies,
build plugins and profiles. Just add them to your project's `pom.xml`. If you
have used the
[QA Testing Archetype](https://java.qa-automation-starter.aherscu.dev/qa-testing-parent/qa-testing-archetype)
to generate your project, then its `pom.xml` already has the relevant sections
in it.

Next: [Profiles](profiles.html)
