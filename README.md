[![Build Status](https://buildhive.cloudbees.com/job/devopsmercenaries/job/sh4rewithme/badge/icon)](https://buildhive.cloudbees.com/job/devopsmercenaries/job/sh4rewithme/)

How to try it out quickly
========================================
From this repository's root, launch:
######
    mvn tomcat7:run

When the Tomcat server finishes starting, go to http://localhost:9090/sh4rewithme-webapp/
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
