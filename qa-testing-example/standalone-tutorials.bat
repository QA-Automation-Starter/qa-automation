java ^
-Dlog.root.level=info ^
-Dlogback.configurationFile=logback-test.xml ^
-Dpoll.timeout=15 ^
-Dpoll.delay=5 ^
-Dsaucelabs.reporter.url= ^
-Dprovider=provider.local. ^
-Ddevice.type= ^
-Dbuild.label=STANDALONE ^
-Dbuild.tags= ^
-Dapplication.filename= ^
-Dapplication.workingdir= ^
-Dtest.properties.file=environments/default/test.properties ^
-Djgiven.report.dir=target/jgiven-reports ^
-Ddryrun=false ^
-Dscreenshots=true ^
-DscreenshotDelayMs=500 ^
-jar target/qa-testing-example-0.0.9-SNAPSHOT-test-with-dependencies.jar ^
-listener dev.aherscu.qa.jgiven.reporter.QaJGivenPerMethodReporter ^
-d target/test-output ^
testing-tutorials.xml
@echo Exit Code is %errorlevel%
