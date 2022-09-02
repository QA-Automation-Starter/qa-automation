QA Tester Utilities
===================

JAX-RS generic providers, authenticators and logging filters, an
auto-returning pool implementation based on Apache Commons Pooling, a
wrapper around Apache Commons Configuration, various assertions for JSON and
XML, a Read-Multi-Write lock, Hamcrest Matchers extensions for streams,
and many other utilities for handling text, streams, lists, maps, etc.
In addition, provides an AWS Cognito SRP filter for JAX-RS and a CLI for it.


Generating an AWS Cognito SRP Authenticator tool
================================================

```
mvn -Pgenerate-aws-cognito-srp-authenticator package
```

This will generate `aws-cognito-srp-authenticator.exe` in target directory. When
run without parameters, prints a help message.
