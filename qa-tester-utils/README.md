QA Tester Utilities
===================

Very low-level and common Java utilities for dealing with:

* configuration
* JSON
* logging
* REST
* YAML
* files, arrays, strings and other primitives

Generating an AWS Cognito SRP Authenticator tool
================================================

```
mvn -Pgenerate-aws-cognito-srp-authenticator package
```

This will generate `aws-cognito-srp-authenticator.exe` in target directory. When
run without parameters, prints a help message.
