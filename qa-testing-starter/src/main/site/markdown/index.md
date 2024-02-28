QA Testing Starter
===================

Can be included as a dependency in your `pom.xml` in case that it already has
a parent. Relevant profiles and plugin configurations will need to be added
manually, since each Maven `pom.xml` may have its own peculiaries.

```xml
    <dependencyManagement>
      <dependencies>
        <dependency>
            <groupId>dev.aherscu.qa</groupId>
            <artifactId>qa-testing-parent</artifactId>
            <version>${qa.automation.version}</version>
            <type>pom</type>
            <scope>import</scope>
        </dependency>
      </dependencies>
    </dependencyManagement>

    <dependencies>
        <dependency>
          <groupId>dev.aherscu.qa</groupId>
          <artifactId>qa-testing-starter</artifactId>
          <version>${qa.automation.version}</version>
        </dependency>
        <!-- per need -->
        <dependency>
          <groupId>dev.aherscu.qa</groupId>
          <artifactId>qa-jgiven-*</artifactId>
        </dependency>
    </dependencies>
```