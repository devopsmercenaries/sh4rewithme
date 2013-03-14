PROCESSOR=`uname -p`

case "$PROCESSOR" in

	x86_64)
		export JAVA_HOME=/opt/@@PROJECTNAME@@/jvm/java-sun-1.6.0-x64
		export PATH=$JAVA_HOME/bin:$JAVA_HOME/jre/bin:$PATH
		;;

	i?86)
		export JAVA_HOME=/opt/@@PROJECTNAME@@/jvm/java-sun-1.6.0-i586
		export PATH=$JAVA_HOME/bin:$JAVA_HOME/jre/bin:$PATH
		;;

	*)
		echo "Architecture unknown, can't set JVM location"
		;;

esac
