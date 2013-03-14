#!/bin/sh

APP_VERSION=1.0.0-SNAPSHOT
TOMCAT_VERSION=7.0.27

if [ $# -gt 1 ]; then
  APP_VERSION=$1
  shift
fi

if [ $# -gt 1 ]; then
  TOMCAT_VERSION=$1
  shift
fi

ARTIFACT_GROUP=me.sh4rewith
ARTIFACT_ID=sh4rewithme-assembly
ARTIFACT_VERSION=$APP_VERSION
ARTIFACT_TYPE=tar.gz
ARTIFACT_CLASSIFIER=-bin
ARTIFACT_RELEASE_REPOSITORY=http://repository.sh4rewith.me/content/repositories/releases
ARTIFACT_SNAPSHOT_REPOSITORY=http://repository.sh4rewith.me/content/repositories/snapshots

fetch_maven() {

        local ART_GROUP=`echo $1 | sed "s#\.#/#g"`
        local ART_ID=$2
        local ART_VERSION=$3
        local ART_TYPE=$4
        local ART_CLASSIFIER=$5
        local ART_RELEASE_REPOSITORY=$6
        local ART_SNAPSHOT_REPOSITORY=$7

        case "$ART_VERSION" in

            *SNAPSHOT* )

                    # SNAPSHOT mode, need to fetch metadata.xml to get latest artifact id
                        ART_URL=$ART_SNAPSHOT_REPOSITORY/$ART_GROUP/$ART_ID

                        rm -f maven-metadata.xml
                        echo "fetching maven-metadata.xml from $ART_URL/$ART_VERSION/maven-metadata.xml"
                        curl -s -L $ART_URL/$ART_VERSION/maven-metadata.xml -o maven-metadata.xml

                        #
                        # Single or Multi SNAPSHOT ?
                        #
                        XPATH_QUERY="/metadata/versioning/snapshot/timestamp/text()"
                        ART_TIME_STAMP=`xpath maven-metadata.xml $XPATH_QUERY 2>/dev/null`

                        XPATH_QUERY="/metadata/versioning/lastUpdated/text()"
                        ART_LATEST_UPDATED=`xpath maven-metadata.xml $XPATH_QUERY 2>/dev/null`

                        XPATH_QUERY="/metadata/versioning/snapshot/buildNumber/text()"
                        ART_BUILD_NUMBER=`xpath maven-metadata.xml $XPATH_QUERY 2>/dev/null`

                        echo "ART_TIME_STAMP=$ART_TIME_STAMP ART_LATEST_UPDATED=$ART_LATEST_UPDATED ART_BUILD_NUMBER=$ART_BUILD_NUMBER"
                        rm -f maven-metadata.xml

                    if [ -z "$ART_LATEST_UPDATED" ]; then
                                echo "problem downloading metadata, aborting build"
                        exit -1
                        fi

                    if [ -z "$ART_TIME_STAMP" ]; then
                                echo "Single SNAPSHOT repository"
                                # 1.0.0-SNAPSHOT-20120106100703
                                ART_UNIQ_VERSION=$ART_VERSION-$ART_LATEST_UPDATED
                                # basic-perf-webapp-1.0.0-SNAPSHOT.war
                                ARTIFACT_REPO_FILE_NAME=$ART_ID-$ART_VERSION$ART_CLASSIFIER.$ART_TYPE
                        else
                        echo "Multi SNAPSHOT repository"
                        # 1.0.0-20120106.100703-18
                                ART_UNIQ_VERSION=$ART_VERSION-$ART_BUILD_NUMBER
                                # basic-perf-webapp-1.0.0-20120106.100703-18.war
                                ARTIFACT_REPO_FILE_NAME=`echo $ART_ID-$ART_VERSION-$ART_TIME_STAMP-$ART_BUILD_NUMBER$ART_CLASSIFIER.$ART_TYPE | sed "s#-SNAPSHOT##"`
                        fi

                        ARTIFACT_DOWNLOAD_URL=$ART_SNAPSHOT_REPOSITORY/$ART_GROUP/$ART_ID/$ART_VERSION
                        ARTIFACT_RPM_FILE_NAME=$ART_ID-$ART_UNIQ_VERSION$ART_CLASSIFIER.$ART_TYPE
                        ARTIFACT_RPM_VERSION=`echo $ART_VERSION | sed "s#-SNAPSHOT##"`
                        ARTIFACT_RPM_RELEASE=0.$ART_LATEST_UPDATED
                ;;

            * )
                        ARTIFACT_DOWNLOAD_URL=$ART_RELEASE_REPOSITORY/$ART_GROUP/$ART_ID/$ART_VERSION
                        ARTIFACT_REPO_FILE_NAME=$ART_ID-$ART_VERSION$ART_CLASSIFIER.$ART_TYPE
                        ARTIFACT_RPM_FILE_NAME=$ARTIFACT_REPO_FILE_NAME
                        ARTIFACT_RPM_VERSION=$ART_VERSION
                        ARTIFACT_RPM_RELEASE=1
           ;;

        esac
}

fetch_maven $ARTIFACT_GROUP $ARTIFACT_ID $ARTIFACT_VERSION $ARTIFACT_TYPE $ARTIFACT_CLASSIFIER $ARTIFACT_RELEASE_REPOSITORY $ARTIFACT_SNAPSHOT_REPOSITORY

echo "Artifact repo name is $ARTIFACT_REPO_FILE_NAME, RPM source file is $ARTIFACT_RPM_FILE_NAME"

if [ ! -s SOURCES/$ARTIFACT_RPM_FILE_NAME ]; then
 rm -f SOURCES/$ARTIFACT_ID-*$ARTIFACT_VERSION*
 echo "downloading ${ARTIFACT_DOWNLOAD_URL}/$ARTIFACT_REPO_FILE_NAME to SOURCES/$ARTIFACT_RPM_FILE_NAME"
 curl -s -L ${ARTIFACT_DOWNLOAD_URL}/$ARTIFACT_REPO_FILE_NAME -o SOURCES/$ARTIFACT_RPM_FILE_NAME
fi

TOMCAT_URL=http://mir2.ovh.net/ftp.apache.org/dist/tomcat/tomcat-7/v${TOMCAT_VERSION}/bin/apache-tomcat-${TOMCAT_VERSION}.tar.gz
CATALINA_JMX_REMOTE_URL=http://mir2.ovh.net/ftp.apache.org/dist/tomcat/tomcat-7/v${TOMCAT_VERSION}/bin/extras/catalina-jmx-remote.jar

if [ ! -f SOURCES/apache-tomcat-${TOMCAT_VERSION}.tar.gz ]; then
  echo "downloading apache-tomcat-${TOMCAT_VERSION}.tar.gz from $TOMCAT_URL"
  curl -s -L $TOMCAT_URL -o SOURCES/apache-tomcat-${TOMCAT_VERSION}.tar.gz
fi

if [ ! -f SOURCES/catalina-jmx-remote-${TOMCAT_VERSION}.jar ]; then
  echo "downloading catalina-jmx-remote-${TOMCAT_VERSION}.jar from $CATALINA_JMX_REMOTE_URL"
  curl -s -L $CATALINA_JMX_REMOTE_URL -o SOURCES/catalina-jmx-remote-${TOMCAT_VERSION}.jar
fi

echo "Version to package $APP_VERSION is powered by Apache Tomcat $TOMCAT_VERSION"

# prepare fresh directories
rm -rf BUILD RPMS SRPMS TEMP
mkdir -p BUILD RPMS SRPMS TEMP

# Build using rpmbuild (use double-quote for define to have shell resolv vars !)
rpmbuild -bb --define="_topdir $PWD" --define="_tmppath $PWD/TEMP" --define="TOMCAT_REL $TOMCAT_VERSION" --define="APP_VERSION $ARTIFACT_RPM_VERSION"  --define="APP_REL $ARTIFACT_RPM_RELEASE" --define "APP_FILE $ARTIFACT_RPM_FILE_NAME"  SPECS/sh4rewithme-webapp-tc7.spec
