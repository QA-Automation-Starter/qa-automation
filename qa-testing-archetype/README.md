# QA Testing Archetype

Generates a template Maven project with the QA Automation built-in.

See [generate-example-project.bat](generate-example-project.bat) for an working
demo.

Snapshots require following sections to present in `.m2/settings.xml`:

```xml

<settings>
  ...
  <profiles>
    <profile>
      <id>ossrh</id>
      <repositories>
        <repository>
          <!-- id expected by maven-archetype-plugin to avoid fetching from everywhere -->
          <id>archetype</id>
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
