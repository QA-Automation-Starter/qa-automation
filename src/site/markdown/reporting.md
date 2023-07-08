# Reporting

There can be mutliple reporting requirements:

* generate a dashboard and publish it on Jenkins
* generate a dashboard and publish it on some other site
* generate printable/custom document(s)
* update an ALM system

# Dashboard on Jenkins

JGiven Maven plugin generates a dashboard, you only need to add a link from
Jenkins job.

# Dashboard somewhere else...

Same JGiven dashboard can be uploaded to a remote site. You can solve this by
finding various Maven plugins to do that and add them to your `build` section.

One of the wierdiest requirements I got was to upload it to S3;
see [S3 Publisher Maven Plugin](qa-s3-publisher-maven-plugin/index.html).

# Printable/Custom Document(s)

The requirement can vary widely, from HTML, and PDF, to some wierd Markdown
format.

See [QA JGiven Reporter Maven Plugin](qa-jgiven-reporter-maven-plugin).

# Update ALM

Application Lifecycle Management, ALM in short... There are many of these, most
common today being Jira and AzureDevops. But there are others like TestRail and
Orcanos.

There are two possible approaches:

* [QA JGiven Reporter Maven Plugin](qa-jgiven-reporter-maven-plugin/index.html)
  to generate custom reports, further uploading them in `post-integration` phase
  of Maven
* [QA TestRail Reporter](qa-testrail-reporter/index.html) to generate the
  reports and upload them after each test

See [QA Orcanos Publisher Maven Plugin](qa-orcanos-publisher-maven-plugin/index.html).

Next: [CI/CD](ci-cd.html)
