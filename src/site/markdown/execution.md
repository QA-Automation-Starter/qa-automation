# Execution

There are five main configuration sets, each configuration being modelled as a
Maven profile.

A typical execution looks like this:

`mvnw -Ptesting-XX,provider-XX,device-XX,-Penvironment-XX,mode-XX`

`XX` being the specific profile to be applied.

see [QA Testing Parent](qa-testing-parent/index.html) for details

Next: [Reporting](reporting.html)
