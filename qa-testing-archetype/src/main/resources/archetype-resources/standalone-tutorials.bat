java ^
-Dlog.root.level=debug ^
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
-Ddryrun= ^
-jar target/${artifactId}-${version}-test-with-dependencies.jar ^
-d target/test-output ^
testing-tutorials.xml
@echo Exit Code is %errorlevel%