mkdir demo
chdir demo
mvn archetype:generate ^
  -DarchetypeGroupId=dev.aherscu.qa ^
  -DarchetypeArtifactId=qa-testing-archetype ^
  -DarchetypeVersion=0.0.1-SNAPSHOT ^
  -DgroupId=com.acme ^
  -DartifactId=testing
