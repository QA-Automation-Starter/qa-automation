@echo off
setlocal
pushd target
for %%F in (*-test-with-dependencies.jar) do (set "TEST_JAR=%%F")
if [%TEST_JAR%]==[] (
    echo ERROR: No test jar found in target directory
    echo INFO: run "mvn package -Pgenerate-standalone" to generate the test jar
    exit /b 1
)
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
-Dapplication.filename=Microsoft.WindowsCalculator_8wekyb3d8bbwe!App ^
-Dapplication.workingdir= ^
-Dtest.properties.file=environments/dev/test.properties ^
-Djgiven.report.dir=jgiven-reports ^
-Ddryrun=false ^
-jar %TEST_JAR% ^
-d test-output ^
-testjar %TEST_JAR% ^
-xmlpathinjar testing-windows.xml
endlocal
popd
