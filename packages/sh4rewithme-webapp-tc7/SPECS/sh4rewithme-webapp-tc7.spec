%ifos darwin
%define __portsed sed -i "" -e
%else
%define __portsed sed -i
%endif

%if %{?TOMCAT_REL:1}
%define tomcat_rel        %{TOMCAT_REL}
%else
%define tomcat_rel        7.0.27
%endif

%if %{?APP_VERSION:1}
%define app_ver    %{APP_VERSION}
%else
%define app_ver    1.0.0
%endif

%if %{?APP_REL:1}
%define app_rel    %{APP_REL}
%else
%define app_rel    1
%endif

%define projectname           sh4rewithme
%define app             %{projectname}-webapp

Name: sh4rewithme-webapp-tc7
Version: %{app_ver}
Release: %{app_rel}
Summary: %{app} %{app_ver}-%{app_rel} powered by Apache Tomcat %{tomcat_rel}
Group: Applications/Communications
Group: %{projectname}/applications
URL: https://www.sh4rewith.me/
Vendor: %{projectname}
Packager: %{projectname}
License: ASL
BuildArch:  noarch


%define appusername     sh4rewm
%define appuserid       2100
%define appgroupname    %{appusername}
%define appgroupid      %{appuserid}

%define appdir          /opt/%{projectname}/%{app}
%define appdatadir      %{_var}/lib/%{projectname}/%{app}
%define applogdir       %{_var}/log/%{projectname}/%{app}
%define apprepodir      %{appdir}/apps-repo
%define appexec         %{appdir}/bin/catalina.sh
%define appconfdir      %{appdir}/conf
%define appconflocaldir %{appdir}/conf/Catalina/localhost
%define appetcdir       %{appdir}/etc
%define applibdir       %{appdir}/lib
%define appwebappdir    %{appdir}/webapps
%define apptempdir      /tmp/%{projectname}/%{app}
%define appworkdir      %{_var}/%{projectname}/%{app}

%define _systemdir      /lib/systemd/system
%define _initrddir      %{_sysconfdir}/init.d

BuildRoot: %{_tmppath}/build-%{name}-%{version}-%{release}

%if 0%{?suse_version} > 1140
BuildRequires: systemd
%{?systemd_requires}
%endif

%if 0%{suse_version} <= 1140
%define systemd_requires %{nil}
%endif

Requires:           java = 1:1.6.0

Requires(pre):      %{_sbindir}/groupadd
Requires(pre):      %{_sbindir}/useradd
Requires(postun):   %{_sbindir}/groupdel
Requires(postun):   %{_sbindir}/userdel

Source0: apache-tomcat-%{tomcat_rel}.tar.gz
Source1: %{APP_FILE}
Source2: initd.skel
Source3: sysconfig.skel
Source4: jmxremote.access.skel
Source5: jmxremote.password.skel
Source6: setenv.sh.skel
Source7: logrotate.skel
Source8: server.xml.skel
Source9: limits.conf.skel
Source10: systemd.skel
Source11: catalina-jmx-remote-%{tomcat_rel}.jar
Source12: catalina.properties

%description
%{app} %{app_ver}-%{app_rel} powered by Apache Tomcat %{tomcat_rel}

%prep
%setup -q -c

%build

%install
# Prep the install location.
rm -rf $RPM_BUILD_ROOT

mkdir -p $RPM_BUILD_ROOT%{_initrddir}
mkdir -p $RPM_BUILD_ROOT%{_sysconfdir}/sysconfig
mkdir -p $RPM_BUILD_ROOT%{_sysconfdir}/logrotate.d
mkdir -p $RPM_BUILD_ROOT%{_sysconfdir}/security/limits.d
mkdir -p $RPM_BUILD_ROOT%{_systemdir}

mkdir -p $RPM_BUILD_ROOT%{appdir}
mkdir -p $RPM_BUILD_ROOT%{apprepodir}
mkdir -p $RPM_BUILD_ROOT%{appdatadir}
mkdir -p $RPM_BUILD_ROOT%{appetcdir}
mkdir -p $RPM_BUILD_ROOT%{applogdir}
mkdir -p $RPM_BUILD_ROOT%{apptempdir}
mkdir -p $RPM_BUILD_ROOT%{appworkdir}
mkdir -p $RPM_BUILD_ROOT%{appwebappdir}

# Copy tomcat
mv apache-tomcat-%{tomcat_rel}/* $RPM_BUILD_ROOT%{appdir}

# Create conf/Catalina/localhost
mkdir -p $RPM_BUILD_ROOT%{appconflocaldir}

# remove default webapps
rm -rf $RPM_BUILD_ROOT%{appdir}/webapps/*

# patches to have logs under /var/log/app
%{__portsed} 's|\${catalina.base}/logs|%{applogdir}|g' $RPM_BUILD_ROOT%{appdir}/conf/logging.properties

# extract content from assembly
mkdir assembly
pushd assembly
tar xvzf %{SOURCE1}

# app webapp is ROOT context (will respond to /)
mv apps-repo/%{app}-%{app_ver}*.war $RPM_BUILD_ROOT%{apprepodir}/%{app}.war
mv conf/Catalina/localhost/%{app}-*.xml $RPM_BUILD_ROOT%{appconflocaldir}/ROOT.xml.skel
# patch webapp-x.y.z.war or webapp-x.y.z-SNAPSHOT.war to webapp.war
%{__portsed} 's|webapp-.*.war|webapp.war|g' $RPM_BUILD_ROOT%{appconflocaldir}/ROOT.xml.skel
mv etc/mongodb-prod.properties.skel $RPM_BUILD_ROOT%{appetcdir}/mongodb-prod.properties.skel

# additional libraries go to lib folder
mv lib/*.jar $RPM_BUILD_ROOT%{applibdir}
popd
rm -rf assembly

# init.d
cp  %{SOURCE2} $RPM_BUILD_ROOT%{_initrddir}/%{app}
%{__portsed} 's|@@APP_APP@@|%{app}|g' $RPM_BUILD_ROOT%{_initrddir}/%{app}
%{__portsed} 's|@@APP_USER@@|%{appusername}|g' $RPM_BUILD_ROOT%{_initrddir}/%{app}
%{__portsed} 's|@@APP_VERSION@@|version %{version} release %{release}|g' $RPM_BUILD_ROOT%{_initrddir}/%{app}
%{__portsed} 's|@@APP_EXEC@@|%{appexec}|g' $RPM_BUILD_ROOT%{_initrddir}/%{app}

# sysconfig
cp  %{SOURCE3}  $RPM_BUILD_ROOT%{_sysconfdir}/sysconfig/%{app}
%{__portsed} 's|@@APP_APP@@|%{app}|g' $RPM_BUILD_ROOT%{_sysconfdir}/sysconfig/%{app}
%{__portsed} 's|@@PROJECT_NAME@@|%{projectname}|g' $RPM_BUILD_ROOT%{_sysconfdir}/sysconfig/%{app}
%{__portsed} 's|@@APP_APPDIR@@|%{appdir}|g' $RPM_BUILD_ROOT%{_sysconfdir}/sysconfig/%{app}
%{__portsed} 's|@@APP_DATADIR@@|%{appdatadir}|g' $RPM_BUILD_ROOT%{_sysconfdir}/sysconfig/%{app}
%{__portsed} 's|@@APP_REPODIR@@|%{apprepodir}|g' $RPM_BUILD_ROOT%{_sysconfdir}/sysconfig/%{app}
%{__portsed} 's|@@APP_ETCDIR@@|%{appetcdir}|g' $RPM_BUILD_ROOT%{_sysconfdir}/sysconfig/%{app}
%{__portsed} 's|@@APP_LOGDIR@@|%{applogdir}|g' $RPM_BUILD_ROOT%{_sysconfdir}/sysconfig/%{app}
%{__portsed} 's|@@APP_USER@@|%{appusername}|g' $RPM_BUILD_ROOT%{_sysconfdir}/sysconfig/%{app}
%{__portsed} 's|@@APP_CONFDIR@@|%{appconfdir}|g' $RPM_BUILD_ROOT%{_sysconfdir}/sysconfig/%{app}

# JMX (including JMX Remote)
cp %{SOURCE11} $RPM_BUILD_ROOT%{appdir}/lib
cp %{SOURCE4}  $RPM_BUILD_ROOT%{appconfdir}/jmxremote.access.skel
cp %{SOURCE5}  $RPM_BUILD_ROOT%{appconfdir}/jmxremote.password.skel

# Our custom setenv.sh to get back env variables
cp  %{SOURCE6} $RPM_BUILD_ROOT%{appdir}/bin/setenv.sh
%{__portsed} 's|@@APP_APP@@|%{app}|g' $RPM_BUILD_ROOT%{appdir}/bin/setenv.sh

# Install logrotate
cp %{SOURCE7} $RPM_BUILD_ROOT%{_sysconfdir}/logrotate.d/%{app}
%{__portsed} 's|@@APP_LOGDIR@@|%{applogdir}|g' $RPM_BUILD_ROOT%{_sysconfdir}/logrotate.d/%{app}

# Install server.xml.skel
cp %{SOURCE8} $RPM_BUILD_ROOT%{appconfdir}/server.xml.skel

# Install catalina.properties
cp %{SOURCE12} $RPM_BUILD_ROOT%{appconfdir}/catalina.properties

# Setup user limits
cp %{SOURCE9} $RPM_BUILD_ROOT%{_sysconfdir}/security/limits.d/%{app}.conf
%{__portsed} 's|@@APP_USER@@|%{appusername}|g' $RPM_BUILD_ROOT%{_sysconfdir}/security/limits.d/%{app}.conf

# Setup Systemd
cp %{SOURCE10} $RPM_BUILD_ROOT%{_systemdir}/%{app}.service
%{__portsed} 's|@@APP_APP@@|%{app}|g' $RPM_BUILD_ROOT%{_systemdir}/%{app}.service
%{__portsed} 's|@@APP_EXEC@@|%{appexec}|g' $RPM_BUILD_ROOT%{_systemdir}/%{app}.service

# remove uneeded file in RPM
rm -f $RPM_BUILD_ROOT%{appdir}/*.sh
rm -f $RPM_BUILD_ROOT%{appdir}/*.bat
rm -f $RPM_BUILD_ROOT%{appdir}/bin/*.bat
rm -rf $RPM_BUILD_ROOT%{appdir}/logs
rm -rf $RPM_BUILD_ROOT%{appdir}/temp
rm -rf $RPM_BUILD_ROOT%{appdir}/work

# ensure shell scripts are executable
chmod 755 $RPM_BUILD_ROOT%{appdir}/bin/*.sh

%clean
rm -rf $RPM_BUILD_ROOT

%pre
%if 0%{?suse_version} > 1140
%service_add_pre %{app}.service
%endif
# First install time, add user and group
if [ "$1" == "1" ]; then
  %{_sbindir}/groupadd -r -g %{appgroupid} %{appgroupname} 2>/dev/null || :
  %{_sbindir}/useradd -s /sbin/nologin -c "%{app} user" -g %{appgroupname} -r -d %{appdatadir} -u %{appuserid} %{appusername} 2>/dev/null || :
else
# Update time, stop service if running
  if [ "$1" == "2" ]; then
    if [ -f %{_var}/run/%{app}.pid ]; then
      %{_initrddir}/%{app} stop
      touch %{applogdir}/rpm-update-stop
    fi
    # clean up deployed webapp
    rm -rf %{appconflocaldir}/ROOT.xml
  fi
fi

%post
%if 0%{?suse_version} > 1140
%service_add_post %{app}.service
%endif
# First install time, register service, generate random passwords and start application
if [ "$1" == "1" ]; then
  # register app as service
  systemctl enable %{app}.service >/dev/null 2>&1

  # Generated random password for RO and RW accounts
  RANDOMVAL=`echo $RANDOM | md5sum | sed "s| -||g" | tr -d " "`
  sed -i "s|@@APP_RO_PWD@@|$RANDOMVAL|g" %{_sysconfdir}/sysconfig/%{app}
  RANDOMVAL=`echo $RANDOM | md5sum | sed "s| -||g" | tr -d " "`
  sed -i "s|@@APP_RW_PWD@@|$RANDOMVAL|g" %{_sysconfdir}/sysconfig/%{app}

  pushd %{appdir} >/dev/null
  ln -s %{applogdir}  logs
  ln -s %{apptempdir} temp
  ln -s %{appworkdir} work
  popd >/dev/null

  # start application at first install (uncomment next line this behaviour not expected)
  # %{_initrddir}/%{name} start
else
  # Update time, restart application if it was running
  if [ "$1" == "2" ]; then
    if [ -f %{applogdir}/rpm-update-stop ]; then
      # restart application after update (comment next line this behaviour not expected)
      # %{_initrddir}/%{name} start
      rm -f %{applogdir}/rpm-update-stop
    fi
  fi
fi

%preun
%if 0%{?suse_version} > 1140
%service_del_preun %{app}.service
%endif
if [ "$1" == "0" ]; then
  # Uninstall time, stop service and cleanup

  # stop service
  %{_initrddir}/%{app} stop

  # unregister app from services
  systemctl disable %{app}.service >/dev/null 2>&1

  # finalize housekeeping
  rm -rf %{appdir}
  rm -rf %{applogdir}
  rm -rf %{apptempdir}
  rm -rf %{appworkdir}

fi

%postun
%if 0%{?suse_version} > 1140
%service_del_postun %{app}.service
%endif
if [ "$1" == "0" ]; then
%{_sbindir}/userdel %{appusername} >/dev/null 2>&1
%{_sbindir}/groupdel %{appgroupname} >/dev/null 2>&1
fi

# Specific actions in relations with others packages
#%triggerin -- otherapp
# Do something if otherapp is installed

#%triggerun -- otherapp
# Do something if otherapp is uninstalled


%files
%defattr(-,root,root)
%attr(0755,%{appusername},%{appgroupname}) %dir %{applogdir}
%attr(0755, root,root) %{_initrddir}/%{app}
%attr(0644,root,root) %{_systemdir}/%{app}.service
%config(noreplace) %{_sysconfdir}/sysconfig/%{app}
%config %{_sysconfdir}/logrotate.d/%{app}
%config %{_sysconfdir}/security/limits.d/%{app}.conf
%{appdir}/bin
%{appdir}/conf
%{appdir}/lib
%attr(-,%{appusername}, %{appgroupname}) %{appwebappdir}
%attr(-,%{appusername}, %{appgroupname}) %{apprepodir}
%attr(-,%{appusername}, %{appgroupname}) %{appetcdir}
%attr(0755,%{appusername},%{appgroupname}) %{appconflocaldir}
%attr(0755,%{appusername},%{appgroupname}) %dir %{appdatadir}
%attr(0755,%{appusername},%{appgroupname}) %dir %{apptempdir}
%attr(0755,%{appusername},%{appgroupname}) %dir %{appworkdir}
%doc %{appdir}/NOTICE
%doc %{appdir}/RUNNING.txt
%doc %{appdir}/LICENSE
%doc %{appdir}/RELEASE-NOTES

%changelog
* Sun Apr 15 2012 henri.gomez@gmail.com 1.0.0-1
- Initial RPM