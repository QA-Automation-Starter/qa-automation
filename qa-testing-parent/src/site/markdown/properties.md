# Properties

Description of properties used by QA Automation projects:

* `surefire.suiteXmlFiles` -- defaults to `testng.xml` (which should be left
  empty)
* `environment` -- defaults to `UNDEFINED` name of environment; should match one
  of the subdirectories of `/src/test/resources/environments`;
* `provider` -- defaults to `provider.local.`
* `device.type` -- defaults to empty,
  meaning `src/test/resources/required-capabilities.properties` will be
  consulted
* `build.label` -- will appear on generated report's title
* `build.tags` -- part of directory path of generated reports
* `test.properties.file` -- test properties file to use
* `jgiven.reports` -- directory to store generated JGiven or QA reports
* `poll.timeout` -- how many seconds to wait for an async operation to complete
* `poll.delay` -- how many seconds to wait beween polls on async operation
* `dryrun` -- empty by default, meaning will execute the tests
* `screenshots` -- true by default, meaning screenshots will be
  embedded/attached to reports
* `alm.href` -- http reference to use for linking tests to a requirements or
  test cases specification (defined via `@Reference` annotation)
* `title` -- final title of generated reports

You may override above defaults or add of your own to fit your needs.

Next: [How-to](howto.html)
