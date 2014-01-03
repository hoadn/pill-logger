Pill Logger [![Build Status](https://travis-ci.org/cntwo/pill-logger.png?branch=master)](https://travis-ci.org/cntwo/pill-logger)
===========

An app for Android that helps you track the pills you take.

Build
-----
To build a signed release version of this app:
-	Copy `/deployment/pilllogger.gradle.example` to `/deployment/pilllogger.gradle`
-	Open `/deployment/pilllogger.gradle` and added the keystore passwords
-	From a terminal on the route of the project, run `./gradlew assembleRelease`
-	The signed APK will be found in the `/app/build/apk/` directory