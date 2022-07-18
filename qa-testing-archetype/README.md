# QA Testing Archetype

See [generate-example-project.bat](generate-example-project.bat) for an working demo.

In addition, the following sections must be added to `.m2/settings.xml`:

```xml
<settings>
  ...
  <profiles>
    <profile>
      <id>ossrh</id>
      <repositories>
        <repository>
          <id>archetype</id><!-- id expected by maven-archetype-plugin to avoid fetching from everywhere -->
          <url>https://s01.oss.sonatype.org/content/repositories/snapshots</url>
          <snapshots>
            <enabled>true</enabled>
            <checksumPolicy>warn</checksumPolicy>
          </snapshots>
        </repository>
      </repositories>
    </profile>
  </profiles>
  
  <activeProfiles>
    <activeProfile>ossrh</activeProfile>
  </activeProfiles>
</settings>
```
