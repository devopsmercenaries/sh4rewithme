%ifos darwin
%define __portsed sed -i "" -e
%else
%define __portsed sed -i
%endif

%define projectname           sh4rewithme


Name: %{projectname}-users
Version: 1.0.0
Release: 1
Summary: %{projectname} users
Group: %{projectname}/tools
URL: http://www.sh4rewith.me/
Vendor: %{projectname}
Packager: %{projectname}
License: ASL
BuildArch:  noarch

Source0: users.id
Source1: former-users.id
Source2: %{projectname}.sh
Source3: %{projectname}.csh
Source4: ops-aheritier.pub
Source5: dbaeli.pub
Source6: adm-hgomez.pub
Source7: hikage.pub
Source8: zepag.pub
Source9: jenkinsa.pub

BuildRoot: %{_tmppath}/build-%{name}-%{version}-%{release}

Requires: sudo

Requires(post):   %{_sbindir}/groupadd
Requires(post):   %{_sbindir}/useradd
Requires(postun): %{_sbindir}/userdel


%description
Setup %{projectname} users.

%prep

%build

%install
# Prep the install location.
rm -rf ${RPM_BUILD_ROOT}
mkdir -p ${RPM_BUILD_ROOT}%{_datadir}/%{projectname}/%{name}
mkdir -p ${RPM_BUILD_ROOT}%{_sysconfdir}/profile.d

# Copy users.id and former-users.id
cp %{SOURCE0} ${RPM_BUILD_ROOT}%{_datadir}/%{projectname}/%{name}
cp %{SOURCE1} ${RPM_BUILD_ROOT}%{_datadir}/%{projectname}/%{name}

# Copy sh4rewithme.sh/sh4rewithme.csh
cp %{SOURCE2} ${RPM_BUILD_ROOT}%{_sysconfdir}/profile.d
cp %{SOURCE3} ${RPM_BUILD_ROOT}%{_sysconfdir}/profile.d
%{__portsed} 's|@@PROJECTNAME@@|%{projectname}|g' ${RPM_BUILD_ROOT}%{_sysconfdir}/profile.d/%{projectname}.csh
%{__portsed} 's|@@PROJECTNAME@@|%{projectname}|g' ${RPM_BUILD_ROOT}%{_sysconfdir}/profile.d/%{projectname}.sh

# Copy public keys
cp %{SOURCE4} ${RPM_BUILD_ROOT}%{_datadir}/%{projectname}/%{name}
cp %{SOURCE5} ${RPM_BUILD_ROOT}%{_datadir}/%{projectname}/%{name}
cp %{SOURCE6} ${RPM_BUILD_ROOT}%{_datadir}/%{projectname}/%{name}
cp %{SOURCE7} ${RPM_BUILD_ROOT}%{_datadir}/%{projectname}/%{name}
cp %{SOURCE8} ${RPM_BUILD_ROOT}%{_datadir}/%{projectname}/%{name}
cp %{SOURCE9} ${RPM_BUILD_ROOT}%{_datadir}/%{projectname}/%{name}

%clean
rm -rf $RPM_BUILD_ROOT

#
# Post-installation phase (rpm -Uvh)
# post is just after rpm datas has been installed (ie: /usr/share/%{projectname}/myusers/ files)
#
%post

SH4REWITHME_GROUP_ID=1965

%if 0%{?suse_version}
%{_sbindir}/groupadd -r -g ${SH4REWITHME_GROUP_ID} %{projectname} 2>/dev/null || :
%else
%{_sbindir}/groupadd -f %{projectname} -g ${SH4REWITHME_GROUP_ID}
%endif

# Ensure /etc/sudoers.d exists
if [ ! -d %{_sysconfdir}/sudoers.d ]; then
  mkdir %{_sysconfdir}/sudoers.d
fi

# set admins sudoers in /etc/sudoers.d/%{projectname}-adm
if [ ! -f %{_sysconfdir}/sudoers.d/%{projectname}-adm ]; then
  touch %{_sysconfdir}/sudoers.d/%{projectname}-adm
fi

# set operators sudoers in /etc/sudoers.d/%{projectname}-ops
if [ ! -f %{_sysconfdir}/sudoers.d/%{projectname}-ops ]; then
  touch %{_sysconfdir}/sudoers.d/%{projectname}-ops
fi

# ensure sudoers.d exist on /etc/sudoers
grep "#includedir /etc/sudoers.d" /etc/sudoers >>/dev/null 2>&1 || :
if [ $? != "0" ]; then
  echo "#includedir /etc/sudoers.d" >> /etc/sudoers
  mkdir -p %{_sysconfdir}/sudoers.d
fi

# add real sh4rewithme user and group
for AUTH_KEY_FILE in %{_datadir}/%{projectname}/%{name}/*.pub
do
	SH4REWITHME_USERKEY_FILE=`basename ${AUTH_KEY_FILE}`
    SH4REWITHME_USER=`echo ${SH4REWITHME_USERKEY_FILE} | sed "s|^.*-||" | sed "s|.pub||"`
    SH4REWITHME_USER_ID=`cat %{_datadir}/%{projectname}/%{name}/users.id | grep ^${SH4REWITHME_USER} | sed "s|^${SH4REWITHME_USER}:||" | sed "s|:.*||"`
	SH4REWITHME_USER_EMAIL=`cat %{_datadir}/%{projectname}/%{name}/users.id | grep ^${SH4REWITHME_USER} | sed "s|^${SH4REWITHME_USER}:||" | sed "s|^.*:||"`

    if [ "x${SH4REWITHME_USER_ID}" != "x" ]; then
        %{_sbindir}/useradd -c "${SH4REWITHME_USER}" -g ${SH4REWITHME_GROUP_ID} -r -m -d /home/${SH4REWITHME_USER} -s "/bin/bash" -u ${SH4REWITHME_USER_ID} ${SH4REWITHME_USER} >>/dev/null 2>&1

        # install ssh stuff
        mkdir -p /home/${SH4REWITHME_USER}/.ssh
        cp -f ${AUTH_KEY_FILE} /home/${SH4REWITHME_USER}/.ssh/authorized_keys
        chmod 700 /home/${SH4REWITHME_USER}/.ssh
        chmod 600 /home/${SH4REWITHME_USER}/.ssh/authorized_keys

        chown -R ${SH4REWITHME_USER}:${SH4REWITHME_GROUP_ID} /home/${SH4REWITHME_USER}

        # fix SELinux problem
        if [ -x /sbin/restorecon -o -x %{_sbindir}/restorecon ]; then
          restorecon -R -v /home/${SH4REWITHME_USER}/.ssh
        fi

        # redirect mail
        echo "${SH4REWITHME_USER_EMAIL}" > /home/${SH4REWITHME_USER}/.forward
        chown -R ${SH4REWITHME_USER}:${SH4REWITHME_GROUP_ID} /home/${SH4REWITHME_USER}/.forward

    else
        echo "missing user id for ${SH4REWITHME_USER}, skipping this one"
    fi

	# Dispatch users between admins, operators and regulars users
	case "${SH4REWITHME_USERKEY_FILE}" in

		adm-*)
		    cat %{_sysconfdir}/sudoers.d/%{projectname}-adm | grep -v ${SH4REWITHME_USER} > %{_sysconfdir}/sudoers.d/%{projectname}-adm.wip
		    echo "${SH4REWITHME_USER}          ALL=(ALL) ALL" >> %{_sysconfdir}/sudoers.d/%{projectname}-adm.wip
		    cat %{_sysconfdir}/sudoers.d/%{projectname}-adm.wip | sort | uniq > %{_sysconfdir}/sudoers.d/%{projectname}-adm

			if [ ! -d /root/.ssh ]; then
			  mkdir -p /root/.ssh
		      chmod 700 /root/.ssh
			fi

			if [ ! -f /root/.ssh/authorized_keys ]; then
			  touch /root/.ssh/authorized_keys
			fi

			cat /root/.ssh/authorized_keys | grep -v ${SH4REWITHME_USER} > /root/.ssh/authorized_keys.wip
			cat ${AUTH_KEY_FILE} >>/root/.ssh/authorized_keys.wip
			cp -f /root/.ssh/authorized_keys.wip /root/.ssh/authorized_keys
		;;

		ops-*)
		    cat %{_sysconfdir}/sudoers.d/%{projectname}-ops | grep -v ${SH4REWITHME_USER} > %{_sysconfdir}/sudoers.d/%{projectname}-ops.wip

%if 0%{?suse_version}
		    echo "${SH4REWITHME_USER}  ALL = NOPASSWD: /usr/bin/zypper, /sbin/service, /sbin/reboot" >> %{_sysconfdir}/sudoers.d/%{projectname}-ops.wip
%endif

%if 0%{?fedora} || 0%{?rhel} || 0%{?centos}
		    echo "${SH4REWITHME_USER}  ALL = NOPASSWD: /usr/bin/yum, /sbin/service, /sbin/reboot" >> %{_sysconfdir}/sudoers.d/%{projectname}-ops.wip
%endif

		    cat %{_sysconfdir}/sudoers.d/%{projectname}-ops.wip | sort | uniq > %{_sysconfdir}/sudoers.d/%{projectname}-ops
		;;

	esac

done

rm -f /root/.ssh/authorized_keys.wip
chmod 600 /root/.ssh/authorized_keys

# fix CentOS / SELinux problem (http://bugs.centos.org/print_bug_page.php?bug_id=4959)
if [ -x /sbin/restorecon -o -x %{_sbindir}/restorecon ]; then
	restorecon -R -v /root/.ssh
fi

chmod 0440 %{_sysconfdir}/sudoers.d/%{projectname}-adm
chmod 0440 %{_sysconfdir}/sudoers.d/%{projectname}-ops

#
# Remove former users (account and ssh auth keys). remove it from sh4rewithme and sh4rewithme-ops if existing
#
for SH4REWITHME_USER in `cat %{_datadir}/%{projectname}/%{name}/former-users.id | cut -d : -f1`
do
  if [ -d /home/${SH4REWITHME_USER}/.ssh ]; then

      echo "removing former user ${SH4REWITHME_USER}"
      %{_sbindir}/userdel ${SH4REWITHME_USER} >>/dev/null 2>&1
      rm -rf /home/${SH4REWITHME_USER}/.ssh

      cat %{_sysconfdir}/sudoers.d/%{projectname}-adm | grep -v ${SH4REWITHME_USER} > %{_sysconfdir}/sudoers.d/%{projectname}-adm.wip
      mv %{_sysconfdir}/sudoers.d/%{projectname}-adm.wip %{_sysconfdir}/sudoers.d/%{projectname}-adm

      cat %{_sysconfdir}/sudoers.d/%{projectname}-ops | grep -v ${SH4REWITHME_USER} > %{_sysconfdir}/sudoers.d/%{projectname}-ops.wip
      mv %{_sysconfdir}/sudoers.d/%{projectname}-ops.wip %{_sysconfdir}/sudoers.d/%{projectname}-ops
  fi

done

# Ensure Happy exit even if shutdown script didn't exit cleanly.
exit 0

#
# Uninstallation phase (rpm -e)
# preun is just before rpm datas is removed (ie: /usr/share/sh4rewithme/ files)
#
%preun
if [ "$1" == "0" ]; then
   for AUTH_KEY_FILE in %{_datadir}/%{projectname}/%{name}/*.pub
   do
		SH4REWITHME_USERKEY_FILE=`basename ${AUTH_KEY_FILE}`
    	SH4REWITHME_USER=`echo ${SH4REWITHME_USERKEY_FILE} | sed "s|^.*-||" | sed "s|.pub||"`
        %{_sbindir}/userdel ${SH4REWITHME_USER} >>/dev/null 2>&1
        rm -rf /home/${SH4REWITHME_USER}/.ssh

		if [ -d /root/.ssh ]; then
		  cat /root/.ssh/authorized_keys | grep -v ${SH4REWITHME_USER} > /root/.ssh/authorized_keys.wip
		  cp -f /root/.ssh/authorized_keys.wip /root/.ssh/authorized_keys
		fi

    done

	rm -f /root/.ssh/authorized_keys.wip
    chmod 600 /root/.ssh/authorized_keys

	# fix CentOS / SELinux problem (http://bugs.centos.org/print_bug_page.php?bug_id=4959)
	if [ -x /sbin/restorecon -o -x %{_sbindir}/restorecon ]; then
		restorecon -R -v /root/.ssh
	fi

	# remove admins and operators from sudoers
    rm -f %{_sysconfdir}/sudoers.d/%{projectname}-adm
    rm -f %{_sysconfdir}/sudoers.d/%{projectname}-ops

fi

# Ensure Happy exit even if shutdown script didn't exit cleanly.
exit 0


%files
%defattr(-,root,root)
%attr(0700,root,root) %dir %{_datadir}/%{projectname}/%{name}
%attr(0400,root,root) %{_datadir}/%{projectname}/%{name}
%config(noreplace) %{_sysconfdir}/profile.d/%{projectname}.sh
%config(noreplace) %{_sysconfdir}/profile.d/%{projectname}.csh

%changelog
* Thu Feb 16 2012 pierre.antoine.gregoire@gmail.com 1.0.0-1
- Refactored for coherence with other packages (sharewithme) and to ease future refactorings ;)
* Thu Feb 16 2012 hgomez@gmail.com 1.0.0-0
- Initial package