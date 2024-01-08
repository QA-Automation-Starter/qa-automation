pushd target || exit 1

TEST_JAR=$(find . -name "*-test-with-dependencies.jar")
if [ -z "$TEST_JAR" ]
then
    echo ERROR: No test jar found in target directory
    echo INFO: run "./mvnw package -Pgenerate-standalone" to generate the test jar
    exit 1
fi

$JAVA_HOME/bin/java \
-Dlog.root.level=info \
-Dlogback.configurationFile=logback-test.xml \
-Dpoll.timeout=15 \
-Dpoll.delay=5 \
-Dsaucelabs.reporter.url= \
-Dprovider=provider.local. \
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
-jar $TEST_JAR \
-listener dev.aherscu.qa.jgiven.reporter.QaJGivenPerMethodReporter \
-d test-output \
-testjar $TEST_JAR \
-xmlpathinjar testing-tutorials.xml

STATUS=$?

popd

exit $STATUS
