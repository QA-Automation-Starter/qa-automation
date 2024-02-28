QA Testing Extra
===================

An auto-returning pool implementation based on Apache Commons Pooling,
a Read-Multi-Write lock, object tree/stream scanner, and support for YML, CSV,
Mustache Templates, I16n.
In addition, provides an AWS Cognito SRP filter for JAX-RS and a CLI for it.

Generating an AWS Cognito SRP Authenticator tool
================================================

```
mvn -Pgenerate-aws-cognito-srp-authenticator package
```

This will generate `aws-cognito-srp-authenticator.exe` in target directory. When
run without parameters, prints a help message.
