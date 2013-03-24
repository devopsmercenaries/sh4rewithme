#!/bin/bash

GHOST=graphite.lt.courtanet.net
GPORT=2003
JMX_LOGIN=tomcatMunin
JMX_PWD=muninrocks

SOURCEDIR=$1
TARGETDIR=$2
HOSTS_LIST=$3

echo "Generates JMXTrans configuration files in $SOURCEDIR for $HOSTS_LIST in $TARGETDIR"

mkdir -p $TARGETDIR

#Creates jmxtrans files browsing the templates from $1 dir, using the other parameters for the filename and host/ports
function generate_jmxtrans {
  SOURCE=$1
  NAME=$2
  HOST=$3
  PORT=$4
  for FILE in `ls $SOURCE/*.json`
  do
    NEWFILE=$NAME-`echo $FILE  | xargs basename`;
    cat $FILE | sed -e "s/@JMX_NAME@/$NAME/g" | sed -e "s/@JMX_HOST@/$HOST/g" | sed -e "s/@JMX_PORT@/$PORT/g" | sed -e "s/@JMX_LOGIN@/$JMX_LOGIN/g" | sed -e "s/@JMX_PWD@/$JMX_PWD/g"  | sed -e "s/@GHOST@/$GHOST/g" | sed -e "s/@GPORT@/$GPORT/g" > $TARGETDIR/$NEWFILE;
    echo "$TARGETDIR/$NEWFILE created"
  done
}

#For the hosts listed in parameter, call generate_jmxtrans
for HOSTDEF in `echo $HOSTS_LIST | sed "s/,/ /g"`
do
   NAME=`echo $HOSTDEF | sed -e 's/\(.*\):\(.*\):\(.*\)/\1/'` 
   HOST=`echo $HOSTDEF | sed -e 's/\(.*\):\(.*\):\(.*\)/\2/'` 
   PORT=`echo $HOSTDEF | sed -e 's/\(.*\):\(.*\):\(.*\)/\3/'`
   echo "NAME=$NAME, HOST=$HOST, PORT=$PORT"
   generate_jmxtrans $SOURCEDIR $NAME $HOST $PORT
done
