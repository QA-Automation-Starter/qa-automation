# HOWTO

## Adding test sets

Additional test sets for performance, stability, etc., should be added as
separate `testing-*.xml` files with associated Maven profiles, like this:

```xml
<profile>
  <id>testing-performance</id>
  <properties>
    <surefire.suiteXmlFiles>testing-performance.xml</surefire.suiteXmlFiles>
  </properties>
</profile>
```

## Configuring for different environments

Test properties can be overridden by adding a new environment profile, like
this:

```xml
<profile>
  <id>environment-other</id>
  <properties>
    <environment>other</environment>
  </properties>
</profile>
```

In this example, we create a subfolder `other` in
`src/test/resources/environments` with a `test.properties` file in it to store
that environment's required properties.
