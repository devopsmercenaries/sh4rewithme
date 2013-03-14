#!/bin/sh

VERSION=31
BUILD=b04
RELEASE=0
BUILD_32=true
BUILD_64=true

if [ $# -ge 1 ]; then
  VERSION=$1
fi

if [ $# -ge 2 ]; then
  BUILD=$2
fi

if [ $# -ge 3 ]; then
  RELEASE=$3
fi

PROC=`uname -p`

#
# Oracle download location for Java 1.6.0
#
# http://download.oracle.com/otn-pub/java/jdk/6u31-b04/jdk-6u31-linux-x64.bin
# http://download.oracle.com/otn-pub/java/jdk/6u31-b04/jdk-6u31-linux-i586.bin
# http://download.oracle.com/otn-pub/java/jdk/6u30-b12/jdk-6u30-linux-x64.bin
# http://download.oracle.com/otn-pub/java/jdk/6u30-b12/jdk-6u30-linux-i586.bin

BASE_URL="http://download.oracle.com/otn-pub/java"

fetch_remote_file()
{
	URL=$1
	DEST=$2

	if [ ! -f $DEST ]; then

		echo "downloading from $URL to $DEST..."
		curl -L $URL -o $DEST

		case $DEST in
			*.tar.gz)
	        	tar tzf $DEST >>/dev/null 2>&1
	        	;;
	    	*.bin)
	        	unzip -t $DEST >>/dev/null 2>&1
	        	;;
	    	*.zip)
	        	unzip -t $DEST >>/dev/null 2>&1
	        	;;
	    	*.jar)
	        	unzip -t $DEST >>/dev/null 2>&1
	        	;;
	    	*.war)
	        	unzip -t $DEST >>/dev/null 2>&1
	        	;;
		esac

		if [ $? != 0 ]; then
			rm -f $DEST
			echo "invalid content for `basename $DEST` downloaded from $URL, discarding content and aborting build."
			exit -1
		fi

	fi
}

# Can't build 64bits VM on 32bits system
if [ $PROC = "i686" ]; then
	BUILD_64=false
fi

# prepare fresh directories
rm -rf BUILD TEMP
mkdir -p BUILD RPMS SRPMS TEMP SOURCES

if $BUILD_64; then

	fetch_remote_file $BASE_URL/jdk/6u${VERSION}-${BUILD}/jdk-6u${VERSION}-linux-x64.bin SOURCES/jdk-6u${VERSION}-linux-x64.bin

	echo "# packaging java 1.6.0 64bits #"
	rpmbuild -bb --define="_topdir $PWD" --define="_tmppath $PWD/TEMP" --define="jvm_version $VERSION" --define="jvm_build $BUILD" --define="jdk_model x86_64" SPECS/sh4rewithme-java-sun-1.6.0.spec
fi

# make sure to install glibc and libgcc 32bits on Fedora 15/16 and CentOS5/6 64bits
# yum install glibc.i686 libgcc.i686

if $BUILD_32; then

	fetch_remote_file $BASE_URL/jdk/6u${VERSION}-${BUILD}/jdk-6u${VERSION}-linux-i586.bin SOURCES/jdk-6u${VERSION}-linux-i586.bin

	echo "# packaging java 1.6.0 32bits #"
	rpmbuild -bb --define="_topdir $PWD" --define="_tmppath $PWD/TEMP" --define="jvm_version $VERSION" --define="jvm_build $BUILD" --define="jdk_model i686" SPECS/sh4rewithme-java-sun-1.6.0.spec
fi
