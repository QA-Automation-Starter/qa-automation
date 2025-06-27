> [**For users**: introduction, instructions, and tutorials](https://java.qa-automation-starter.aherscu.dev/qa-testing-parent/qa-testing-example)
>
> (this page is for developing and maintaining this project)

# Development Instructions

Working with archetype snapshots require following sections to be present
in your `.m2/settings.xml`:

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
