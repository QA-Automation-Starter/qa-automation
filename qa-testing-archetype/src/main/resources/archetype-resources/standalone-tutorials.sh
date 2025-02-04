pushd target || exit 1

# Check for required environment variables
if [ -z "$SAUCELABS_USER" ] || [ -z "$SAUCELABS_PASSWORD" ]; then
    echo "ERROR: SAUCELABS_USER and SAUCELABS_PASSWORD environment variables are required but not set."
    echo "INFO: Please export these variables before running the script."
    exit 1
fi

TEST_JAR=$(find . -name "*-test-with-dependencies.jar")
if [ -z "$TEST_JAR" ]
then
    echo ERROR: No test jar found in target directory
    echo INFO: run "./mvnw package -Pgenerate-standalone" to generate the test jar
    exit 1
fi

$JAVA_HOME/bin/java \
-Dlog.root.level=debug \
-Dlogback.configurationFile=logback-test.xml \
-Dpoll.timeout=15 \
-Dpoll.delay=5 \
-Dsaucelabs.reporter.url=https://$SAUCELABS_USER:$SAUCELABS_PASSWORD@ondemand.saucelabs.com:443/wd/hub \
-Dprovider=provider.saucelabs. \
-Ddevice.type= \
-Dbuild.label=STANDALONE \
-Dbuild.tags= \
-Dapplication.filename= \
-Dapplication.workingdir= \
-Dtest.properties.file=environments/dev/test.properties \
-Djgiven.report.dir=jgiven-reports \
-Ddryrun=false \
-Dscreenshots=true \
-DscreenshotDelayMs=500 \
-Djava.awt.headless=true \
-jar $TEST_JAR \
-listener dev.aherscu.qa.jgiven.reporter.QaJGivenPerMethodReporter,dev.aherscu.qa.jgiven.webdriver.utils.SauceLabsReporter \
-d test-output \
-testjar $TEST_JAR \
-xmlpathinjar testing-tutorials.xml

STATUS=$?

popd

exit $STATUS
