# Execution

There are five main configuration sets, each configuration being modelled as a
Maven profile.

A typical execution looks like this:

`mvnw -Ptesting-XX,provider-XX,device-XX,-Penvironment-XX,mode-XX`

`XX` being the specific profile to be applied.

see [QA Testing Parent](qa-testing-parent/index.html) for details

The generated QA Automation project will produce two reports:
* JGiven dashboard -- an interactive site
* QA Report -- single HTML document

all under `target/site/jgiven-reports`

Next: [Reporting](reporting.html)
