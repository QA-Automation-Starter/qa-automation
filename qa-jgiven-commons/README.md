# JGiven Commons

Provides JGiven-style actions and verifications around various
types of WebDrivers, JAX-RS, and SSH protocol. All actions and
verifications, reuse generic ones, which use the FailFast retry mechanism to
ensure failures are never due to application latency. In addition,
adapts Apache Commons Configuration to be used for loading test
properties and DBUtils for managing database-driven tests.
