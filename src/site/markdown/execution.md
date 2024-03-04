# Execution

There are five main configuration sets, each configuration being modelled as a
Maven profile.

A typical execution looks like this:

`mvnw -Ptesting-XX,provider-XX,device-XX,environment-XX,mode-XX`

`XX` being the specific profile to be applied.

## Test Specific Profiles

Detailed in [QA Testing Parent](qa-testing-parent/profiles.html)

## Generic Modding Profiles

* `mode-logs-*`: sets the logging level (`debug|error|trace`); if not specified
  defaults to `info`
* `mode-aspectj-skip`: for debugging, it is easier to see code without AspectJ
  weaved code
* `mode-build-full`: full build including unit tests, static code analysis, etc.
* `mode-build-verbose`: more diagnostics
* `mode-build-nosign`: skips gpg signatures
* `mode-site-fast`: diables SureFire and Project Info reports, useful for
  testing documentation
* `mode-eclipse`: autoselected when the project is opened in Eclipse

Next: [Reporting](reporting.html)
