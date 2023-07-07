# Getting Started

[Generate a QA Automation project](qa-testing-parent/qa-testing-archetype/index.html),
then run it like this:

```shell
cd testing
mvnw -Ptesting-self
```

You should see some tests running in console and Chrome openning two times.

Upon completion, its JGiven report will be available under 
`target/site/jgiven-reports/functional-UNDEFINED\your-username@local-execution-timestamp\html\index.html`

This JGiven report should look similar to what is published under
[QA Testing Example](qa-testing-parent/qa-testing-example/index.html).

Next: [IDE Configuration](ide-configuration.html)
