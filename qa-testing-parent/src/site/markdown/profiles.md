# Profiles

## Mandatory Profiles

To run various test suites in various environments, following profiles must be
selected:

* `testing-*`: mutually-exclusive, will run `testing-*.xml` specification

If not specified, will run the `testng.xml` which should be left empty, namely
no test will run.

## Per-need Profiles

### Environment, mutually-exclusive

* `environment-*`: mutually-exclusive, will run with one of properties files
  found under `src/test/resources/environments`

If no environment is specifed, will use the `UNDEFINED` environment.

### Device, mutually-exclusive

* `device-*`: will run GUI tests on specified device type; if not specified,
  will pick devices from `src/test/resources/required-capabilities.properties`,
  providers and device type combinations being specified in
  `src/test/resources/webdriver.properties`

### Provider, mutually-exclusive

* `provider-selenium`: will use hosted Selenium Grid
* `provider-saucelabs-*`: will use SauceLabs

If no provider is specified, will try to run with local WebDriver per
`provider.local.*`.

### Additional provider profiles, for application uploading

* `provider-saucelabs-application-upload`: will upload application files for
  emulator and simulator to SauceLabs;
* `provider-saucelabs-application-real-setting`: will set the uploaded
  application identifiers for real devices on SauceLabs;
* `provider-saucelabs-application-real-upload`: will upload application files
  for real device to SauceLabs;

If the application files are already up-to-date then these profiles can be
skipped to save time and start the tests faster.

### Modding Profiles

* `mode-logs-*`: sets the logging level (`debug|error|trace`); if not specified
  defaults to `info`
* `mode-aspectj-skip`: for debugging, it is easier to see code without AspectJ
  weaved code
* `mode-build-fast`: disables self unit tests, static analysis and source code
  formatting
* `mode-build-quiet`: silent output for several build plugins (e.g. aspectj)
* `mode-jenkins`: sets the logging level to `info` and the build label to match
  job name and build number in Jenkins
* `mode-noscreenshots`: disables screenshot gathering during GUI tests
* `mode-dryrun`: generates full test report without executing the tests
* `mode-proxy`: will add proxy definitions to testing process

### Other Profiles

* `generate-standalone` -- sometimes it is required to run the automation from
  a standalone JAR, for example, if there is no access to Maven Central

Of course, you may add your own to fit your needs.

Next: [Properties](properties.html)
