# Adding Tests

First, we should determine what are the external interfaces of the system to be
tested. For the sake of simplicity, let's say it is a REST only system.

Let's, add [qa-jgiven-rest](qa-jgiven-rest/dependency-info.html) to our project.

```xml

<dependencies>
  ...
  <dependency>
    <groupId>dev.aherscu.qa</groupId>
    <artifactId>qa-jgiven-rest</artifactId>
    <version>version</version>
    <scope>test</scope>
  </dependency>
  ...
</dependencies>
```

## Environments

Now we should determine in which environments it should run. These may be called
Development, QA, Staging... whatever. These are just groups of computers
providing various services.

Under `test/resources/environments` let's create a folder for each and put an
empty `test.properties` file in each one. For each environment, we should ensure
there is Maven profile to allow its activation, like this:

```xml

<profiles>
  ...
  <profile>
    <id>environment-qa</id>
    <properties>
      <environment>qa</environment>
    </properties>
  </profile>
  ...
</profiles>
```

(a profile for Development environment is already defined
in [QA Testing Parent](qa-testing-parent/index.html))

Later we should add properties to hold URLs, and credentials needed to access
this system. These may look like this:

```properties
system.url        =https://username:password@system-dev.host
token.url         =https://oauth-dev.host
token.clientId    =clientId
token.clientSecret=clientSecret
```

We should now have `test/resources/environments/dev/test.properties`, and so on
for each environment.

## Configuration

Somehow we need to access these properties at run time. Let's create
a `TestConfiguration` class in the main package, like this:

```java
public final class TestConfiguration extends BaseConfiguration {

    // NOTE: this is imperative
    public TestConfiguration(final Configuration... configurations) {
        super(configurations);
    }

    public String systemUrl() {
        return getString("system.url");
    }

    public SystemOAuthRequestFilter systemOAuthRequestFilter() {
        return SystemOAuthRequestFilter.builder()
                .refreshTokenUri(getString("token.url"))
                .authorization(CosOAuthRequestFilter.Authorization.builder()
                        .clientId(getString("token.clientId"))
                        .clientSecret(getString("token.clientSecret"))
                        .build())
                .build();
    }
}
```

Because there are many OAuth variations we need to specify exactly how to
retrieve a token. It can be like this:

```java

@SuperBuilder
@EqualsAndHashCode(callSuper = false)
@ToString
@Slf4j
public class SystemOAuthRequestFilter extends OAuthRequestFilter {

    @Jacksonized
    @Builder
    @ToString
    public static final class Authorization {
        @JsonProperty("client_id")
        public final String clientId;

        @JsonProperty("client_secret")
        public final String clientSecret;

        @JsonProperty("grant_type")
        public final String grantType = "client_credentials";
    }

    private final Authorization authorization;

    @Override
    protected TokenBlock retrieveTokenBlockFor(final ClientRequestContext context) {
        log.debug("retrieving token block for {}", this);
        try (val response = context
                .getClient()
                .target(refreshTokenUri)
                .request()
                .buildPost(json(authorization))
                .invoke()) {
            return response.readEntity(TokenBlock.class);
        }
    }
}
```

## Common Scenario Stuff

In a REST scenario we need to initialize a client before and close it after, so
instead of repeating ourselves in each scenario we can hold this in a common
base class:

```java
public abstract class SystemRestTest extends ConfigurableScenarioTest<TestConfiguration, RestScenarioType, SystemRestFixtures<?>, SystemRestActions<?>, SystemRestVerifications<?>> {

    protected Client client; // we should manage the REST client

    protected SystemRestTest() { // mandatory
        super(TestConfiguration.class);
    }

    // so this one...
    protected SystemRestTest(final Class<TestConfiguration> configurationType) {
        super(configurationType);
    }

    // we may have sections that repeat themselves -- see below
    public Sections sections() {
        return new Sections();
    }

    public Sections sections(final String title) {
        section(title);
        return sections();
    }

    @AfterClass
    protected void afterClassCloseRestClient() {
        client.close();
    }

    @BeforeClass
    protected void beforeClassOpenRestClient() {
        client = LoggingClientBuilder.newClient();
    }

    public final class Sections {
        public Sections creatingObject(final SomeObject object, final Name name) {
            section("creating object with name");
            when()
                    .creating(object)
                    .and().setting(name);

            then()
                    .the_objects(adaptedStream(object -> object.name,
                            hasItemsMatching(equalTo(name))));

            return this;
        }
    }
}
```

This will not compile because of missing:
* `SystemRestFixtures`, `SystemRestActions`, and `SystemRestVerifications` -- these are the steps we should define
* `SomeObject` -- this is a data model we should define too

## One or More Scenarios

TBD

## Steps

TBD

Next: [Selenium Tests](selenium-tests.html)
