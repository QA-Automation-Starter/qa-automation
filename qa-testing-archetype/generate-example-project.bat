mvn --batch-mode archetype:generate ^
  -Dmaven.wagon.http.ssl.insecure=true ^
  -DarchetypeGroupId=dev.aherscu.qa ^
  -DarchetypeArtifactId=qa-testing-archetype ^
  -DarchetypeVersion=0.0.1-SNAPSHOT ^
  -DgroupId=com.acme ^
  -DartifactId=testing ^
  -Dversion=1.0-SNAPSHOT ^
  -Dpackage=com.acme.testing
