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

Next: [Selenium Tests](selenium-tests.html)
