%define projectname sh4rewithme
#
# Sun/Oracle Java 1.6.0 packaging (32/64bits)
#

# disable debug info
%define debug_package %{nil}

%if %{jdk_model} == i686
%define jdk_arch	i586
%endif

%if %{jdk_model} == x86_64
%define jdk_arch	x64
%endif

#
# JDK Definition
#
%define origin          sun
%define javaver         1.6.0

Summary: %{origin} JDK %{javaver} Environment

#
# jvm_version and jvm_build provided via --define externally
#
Version: %{javaver}.%{jvm_version}.%{jvm_build}
Release: 0%{?dist}

#
# Force _jvmdir to be into /opt/sharewithme/jvms where all jvms will be stored
#
%define _jvmdir	/opt/%{projectname}/jvm

# Name contain Java + Origin + Version + Architecture (32/64) (ie: java-sun-1.6.0-x64)
Name:    %{projectname}-java-%{origin}-%{javaver}-%{jdk_arch}
%define jdkdir  %{_jvmdir}/java-%{origin}-%{javaver}-%{jdk_arch}

# java-1.5.0-ibm from jpackage.org set Epoch to 1 for unknown reasons,
# and this change was brought into RHEL-4.  java-1.5.0-ibm packages
# also included the epoch in their virtual provides.  This created a
# situation where in-the-wild java-1.5.0-ibm packages provided "java =
# 1:1.5.0".  In RPM terms, "1.6.0 < 1:1.5.0" since 1.6.0 is
# interpreted as 0:1.6.0.  So the "java >= 1.6.0" requirement would be
# satisfied by the 1:1.5.0 packages.  Thus we need to set the epoch in
# JDK package >= 1.6.0 to 1, and packages referring to JDK virtual
# provides >= 1.6.0 must specify the epoch, "java >= 1:1.6.0".
Epoch:   1

Group:   %{projectname}/tools
Packager: %{projectname}

# Standard JPackage base provides.
Provides: jre-%{javaver}-%{origin} = %{epoch}:%{version}-%{release}
Provides: jre-%{origin} = %{epoch}:%{version}-%{release}
Provides: jre-%{javaver} = %{epoch}:%{version}-%{release}
Provides: java-%{javaver} = %{epoch}:%{version}-%{release}
Provides: jre = %{javaver}
Provides: java-%{origin} = %{epoch}:%{version}-%{release}
Provides: java = %{epoch}:%{javaver}
# Standard JPackage extensions provides.
Provides: jndi = %{epoch}:%{version}
Provides: jndi-ldap = %{epoch}:%{version}
Provides: jndi-cos = %{epoch}:%{version}
Provides: jndi-rmi = %{epoch}:%{version}
Provides: jndi-dns = %{epoch}:%{version}
Provides: jaas = %{epoch}:%{version}
Provides: jsse = %{epoch}:%{version}
Provides: jce = %{epoch}:%{version}
Provides: jdbc-stdext = 3.0
Provides: java-sasl = %{epoch}:%{version}
Provides: java-fonts = %{epoch}:%{version}

License:  Proprietary
URL:      http://www.oracle.com/technetwork/java

Source0: jdk-6u%{jvm_version}-linux-%{jdk_arch}.bin
BuildRoot: %{_tmppath}/build-%{name}-%{version}-%{release}

%if 0%{?fedora} || 0%{?rhel} || 0%{?centos}

# 32bits JVM on 64bits OS requires 32bits libs
%ifarch x86_64
%if %{jdk_model} == i686
Requires: alsa-lib.i686
Requires: dbus-glib.i686
Requires: glibc.i686
Requires: libXext.i686
Requires: libXi.i686
Requires: libXt.i686
Requires: libXtst.i686
%endif
%endif

Requires: alsa-lib
Requires: dbus-glib
Requires: glibc
Requires: libXext
Requires: libXi
Requires: libXt
Requires: libXtst

%endif

%description
This package contains the JDK Environment for %{origin}

%package db
Summary:        JavaDB files for %{name}
Group:          Development/Languages
Requires:       %{name} = %{epoch}:%{version}-%{release}

%description db
This package contains JavaDB files for %{origin}

%package demo
Summary:        Demonstration files for %{name}
Group:          Development/Languages
Requires:       %{name} = %{epoch}:%{version}-%{release}

%description demo
This package contains demonstration files for %{origin}

%package src
Summary:        Source Bundle for %{name}
Group:          Development/Languages
Requires:       %{name} = %{epoch}:%{version}-%{release}

%description src
This package contains Source Bundle files for %{origin}

%package visualvm
Summary:        VisualVM files for %{name}
Group:          Development/Languages
Requires:       %{name} = %{epoch}:%{version}-%{release}

%description visualvm
This package contains VisualVM files for %{origin}

%prep

%build

%install
# Prep the install location.
rm -rf ${RPM_BUILD_ROOT}
mkdir -p ${RPM_BUILD_ROOT}%{jdkdir}
#echo "y" | sh %{SOURCE0} >>/dev/null 2>&1
echo | sh %{SOURCE0} --accept-license --unpack >>/dev/null 2>&1

mv jdk1.6.0_%{jvm_version}/* ${RPM_BUILD_ROOT}%{jdkdir}

# exclude db, demo, sample and visualvm related files from main contents
find $RPM_BUILD_ROOT%{jdkdir} -type d \
  | grep -v %{jdkdir}/db \
  | grep -v %{jdkdir}/bin/jdb \
  | grep -v libJdbcOdbc.so \
  | grep -v %{jdkdir}/demo \
  | grep -v %{jdkdir}/sample \
  | grep -v visualvm \
  | sed 's|'$RPM_BUILD_ROOT'|%dir |' \
  > %{name}.files

find $RPM_BUILD_ROOT%{jdkdir} -type f -o -type l \
  | grep -v %{jdkdir}/db \
  | grep -v jdb \
  | grep -v libJdbcOdbc.so \
  | grep -v %{jdkdir}/demo \
  | grep -v %{jdkdir}/sample \
  | grep -v visualvm \
  | grep -v src.zip \
  | sed 's|'$RPM_BUILD_ROOT'| |' \
  >> %{name}.files

mkdir -p $RPM_BUILD_ROOT/etc/sysconfig
cat > $RPM_BUILD_ROOT/etc/sysconfig/%{name} << EOF1
JAVA_HOME=%{jdkdir}
EOF1
echo "/etc/sysconfig/%{name}" >> %{name}.files

%clean
rm -rf $RPM_BUILD_ROOT

%files -f %{name}.files
%defattr(-,root,root)

%files db
%defattr(-,root,root)
%{jdkdir}/db
%{jdkdir}/bin/jdb
%{jdkdir}/jre/lib/*/libJdbcOdbc.so
%{jdkdir}/man/ja_JP.eucJP/man1/jdb.1
%{jdkdir}/man/man1/jdb.1

%files demo
%defattr(-,root,root)
%{jdkdir}/demo
%{jdkdir}/sample

%files src
%defattr(-,root,root)
%{jdkdir}/src.zip

%files visualvm
%defattr(-,root,root)
%{jdkdir}/bin/jvisualvm
%{jdkdir}/lib/visualvm
%{jdkdir}/man/ja_JP.eucJP/man1/jvisualvm.1
%{jdkdir}/man/man1/jvisualvm.1

%changelog
* Mon Apr 1 2012 henri.gomez@gmail.com
- Initial package