@echo off
pushd target
@rem ISSUE using LATEST for archetypeVersion always resolves to latest release
@rem instead of latest snapshot
@rem https://stackoverflow.com/questions/74581510/using-latest-for-archetypeversion-always-resolves-to-latest-release
call mvn archetype:generate ^
  --show-version ^
  --settings ..\development-maven-settings.xml ^
  -Dmaven.wagon.http.ssl.insecure=true ^
  -DarchetypeCatalog=local ^
  -DarchetypeGroupId=dev.aherscu.qa ^
  -DarchetypeArtifactId=qa-testing-archetype ^
  -DarchetypeVersion=LATEST ^
  -DgroupId=com.acme ^
  -DartifactId=testing ^
  -Dversion=0.0.1-SNAPSHOT ^
  -Dpackage=com.acme.testing
popd
