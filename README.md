[![Build Status](https://buildhive.cloudbees.com/job/devopsmercenaries/job/sh4rewithme/badge/icon)](https://buildhive.cloudbees.com/job/devopsmercenaries/job/sh4rewithme/)

How to try it out quickly
========================================
From this repository's root, launch:
######
    mvn install tomcat7:run -pl sh4rewithme-webapp

When the Tomcat server finishes starting, go to <a href="http://localhost:9090/sh4rewithme-webapp">http://localhost:9090/sh4rewithme-webapp/</a>
Use "zeus" login with the same as password. 
This default bootstrap uses an embedded ElasticSearch instance (for metadata) and the filesystem (for raw files) as backends.

How to build sh4rewithme Maven artifacts
========================================
From this repository's root, launch:
######
    mvn clean install

How to build sh4rewithme RPM packages
=====================================
At this moment just launch the build.sh script in each folder.
This requires the installation of rpmbuild on a compatible linux platform.

How to launch webtests
=====================================
For the moment:
First start the webapp in a given console.
######
    mvn install tomcat7:run -pl sh4rewithme-webapp

Then run integration tests:
######
    mvn integration-test thucydides:aggregate -f sh4rewithme-webtests/pom.xml

Tests results report will be available in:
######
   sh4rewithme-webtests/target/site/thucydides/index.html

How to launch performance tests
=====================================
For the moment:
First start the webapp in a given console.
######
    mvn install tomcat7:run -pl sh4rewithme-webapp

Then configure a graphite instance somewhere to monitor the test.
Then run performance tests:
######
    mvn install -P qual -f sh4rewithme-perf/pom.xml

Tests results report will be available in target/gatling/results/
