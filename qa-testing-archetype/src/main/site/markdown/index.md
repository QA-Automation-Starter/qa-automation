# QA Testing Archetype

Generates a template Maven project with the QA Automation dependencies built-in.

Assuming JDK 8 and Maven 3.6+ are already installed, on Windows it would be:

```shell
mvn --batch-mode archetype:generate ^
  -Dmaven.wagon.http.ssl.insecure=true ^
  -DarchetypeGroupId=dev.aherscu.qa ^
  -DarchetypeArtifactId=qa-testing-archetype ^
  -DgroupId=com.acme ^
  -DartifactId=testing ^
  -Dversion=0.0.1-SNAPSHOT ^
  -Dpackage=com.acme.testing
```

Change `groupId`, `artifactId`, `version`, and `package` to whatever you want.

See [generate-example-project.bat](generate-example-project.bat) for a working
demo.
