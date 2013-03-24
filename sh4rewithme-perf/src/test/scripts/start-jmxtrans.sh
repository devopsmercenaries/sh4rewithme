#!/bin/bash
echo Running JMXTrans with following definition files from $1
ls $1/*.json
mv jmxtrans.log jmxtrans.log.old
sh /home/jmxtrans/jmxtrans-250/jmxtrans.sh start $1
echo JMX Trans is running with PID=`jps | grep -i jmxtrans | awk '{ print $1 };'`
