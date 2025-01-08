@echo off
setlocal
pushd target

:: Check for required environment variables
if "%SAUCELABS_USER%"=="" (
    echo ERROR: SAUCELABS_USER environment variable is required but not set.
    echo INFO: Please set this variable before running the script.
    exit /b 1
)

if "%SAUCELABS_PASSWORD%"=="" (
    echo ERROR: SAUCELABS_PASSWORD environment variable is required but not set.
    echo INFO: Please set this variable before running the script.
    exit /b 1
)

for %%F in (*-test-with-dependencies.jar) do (set "TEST_JAR=%%F")
if [%TEST_JAR%]==[] (
    echo ERROR: No test jar found in target directory
    echo INFO: run "mvn package -Pgenerate-standalone" to generate the test jar
    exit /b 1
)
%JAVA_HOME%\bin\java ^
-Dlog.root.level=info ^
-Dlogback.configurationFile=logback-test.xml ^
-Dpoll.timeout=15 ^
-Dpoll.delay=5 ^
-Dsaucelabs.reporter.url=https://%SAUCELABS_USER%:%SAUCELABS_PASSWORD%@ondemand.saucelabs.com:443/wd/hub ^
-Dprovider=provider.saucelabs. ^
-Ddevice.type= ^
-Dbuild.label=STANDALONE ^
-Dbuild.tags= ^
-Dapplication.filename= ^
-Dapplication.workingdir= ^
-Dtest.properties.file=environments/dev/test.properties ^
-Djgiven.report.dir=jgiven-reports ^
-Ddryrun=false ^
-Dscreenshots=true ^
-DscreenshotDelayMs=500 ^
-jar %TEST_JAR% ^
-listener dev.aherscu.qa.jgiven.reporter.QaJGivenPerMethodReporter,dev.aherscu.qa.jgiven.webdriver.utils.SauceLabsReporter ^
-d test-output ^
-testjar %TEST_JAR% ^
-xmlpathinjar testing-tutorials.xml
endlocal
popd
