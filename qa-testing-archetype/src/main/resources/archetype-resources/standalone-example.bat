java ^
-Dlogback.configurationFile=logback-test.xml ^
-Dpoll.timeout=15 ^
-Dpoll.delay=5 ^
-Dsaucelabs.reporter.url= ^
-Dprovider= ^
-Ddevice.type=windows ^
-Dbuild.label=STANDALONE ^
-Dbuild.tags= ^
-Dapplication.filename=Microsoft.WindowsCalculator_8wekyb3d8bbwe!App ^
-Dapplication.workingdir= ^
-Denvironment.label=default ^
-Djgiven.report.dir=target\jgiven-reports ^
-Ddryrun=true ^
-Dscreenshots=false ^
-DscreenshotDelayMs=500 ^
-jar target\${artifactId}-${version}-test-with-dependencies.jar ^
-d target\test-output ^
testing-self.xml
