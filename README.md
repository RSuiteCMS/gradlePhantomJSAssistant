# PhantomJS Assistant
Gradle plugin for autoconfiguration of PhantomJS, used in automated testing via Selenium / Selenide.

## What does it do?

When applied, it inserts itself just before the `test` task, quickly checking your configuration,
and fetching the platform-specific binary of PhantomJS from their [download page][1] on BitBucket 
if necessary.  It then adds the `phantomjs.binary.path` system property that the GhostDriver expects.

## How do I use it?

Not sure yet.  Still trying to figure out how to deploy community plugins from Github.

[1]: https://bitbucket.org/ariya/phantomjs/downloads