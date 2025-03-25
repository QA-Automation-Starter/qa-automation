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
# ISSUE using LATEST for archetypeVersion always resolves to latest release
# instead of latest snapshot
# https://stackoverflow.com/questions/74581510/using-latest-for-archetypeversion-always-resolves-to-latest-release
../../mvnw archetype:generate \
  --batch-mode \
  --settings ../development-maven-settings.xml \
  -Dmaven.wagon.http.ssl.insecure=true \
  -DarchetypeGroupId=dev.aherscu.qa \
  -DarchetypeArtifactId=qa-testing-archetype \
  -DarchetypeVersion=LATEST \
  -DgroupId=com.acme \
  -DartifactId=testing \
  -Dversion=1.0-SNAPSHOT \
  -Dpackage=com.acme.testing
popd
