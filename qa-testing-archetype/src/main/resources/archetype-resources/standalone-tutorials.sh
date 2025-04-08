#!/bin/bash
#
# Copyright 2025 Adrian Herscu
#
# Licensed under the Apache License, Version 2.0 (the "License");
# you may not use this file except in compliance with the License.
# You may obtain a copy of the License at
#
#     http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and
# limitations under the License.
#
set -e

pushd target || { echo "Directory target not found"; exit 1; }

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
