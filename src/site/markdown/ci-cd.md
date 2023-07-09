# Continuous Integration / Continuous Deployment (CI/CD)

There are many CICD systems, few examples
being [Jenkins](https://www.jenkins.io/)
and [GitHub Actions](https://docs.github.com/en/actions).

This project is currently hosted
on [GitHub](https://github.com/QA-Automation-Starter/qa-automation), and uses
its [Actions](https://github.com/QA-Automation-Starter/qa-automation/actions)
mechenism.

At the end of the day, all CI/CD system are capable of checking out source code
from a source control system, and run Maven on that code. Just checkout your
automation project and run Maven on it as described
in [Execution](execution.html) section.

For Jenkins, you should specify [mode-jenkins](qa-testing-parent/profiles.html)
profile.

## GitHub Actions

See [workflows](https://github.com/QA-Automation-Starter/qa-automation/tree/main/.github/workflows)
for examples.

## Jenkins

Following instructions apply to Windows machines:

1. `choco install -y jenkins` -- should be available at <http://localhost:8080>
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

There should be alternative commands for Mac and various Linux distros.

Next: [Environment Setup](environment-setup.html)
