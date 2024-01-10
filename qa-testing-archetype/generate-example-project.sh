pushd target || exit
# ISSUE using LATEST for archetypeVersion always resolves to latest release
# instead of latest snapshot
# https://stackoverflow.com/questions/74581510/using-latest-for-archetypeversion-always-resolves-to-latest-release
mvn archetype:generate \
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
