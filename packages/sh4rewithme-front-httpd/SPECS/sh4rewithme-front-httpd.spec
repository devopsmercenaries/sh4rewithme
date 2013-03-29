%ifos darwin
%define __portsed sed -i "" -e
%else
%define __portsed sed -i
%endif

%define projectname sh4rewithme
%define domain sh4rewith.me

Name: %{projectname}-front-httpd
Version: 1.1.0
Release: 1
Summary: Apache2 Front configuration for %{projectname}
Group: %{projectname}/tools
URL: http://www.sh4rewith.me/
Vendor: %{projectname}
Packager: %{projectname}
License: ASL
BuildArch:  noarch

BuildRoot: %{_tmppath}/build-%{name}-%{version}-%{release}

Requires: apache2-worker
Requires: apache2-mod_jk

Source0: workers.properties
Source1: jk.conf
Source2: default.conf

%description
Apache2 Front configuration for %{projectname}

%prep

%build

%install
rm -rf $RPM_BUILD_ROOT
mkdir -p $RPM_BUILD_ROOT%{_sysconfdir}/apache2/conf.d
mkdir -p $RPM_BUILD_ROOT%{_sysconfdir}/apache2/vhosts.d
mkdir -p $RPM_BUILD_ROOT%{_sysconfdir}/apache2/ssl.crt
mkdir -p $RPM_BUILD_ROOT%{_sysconfdir}/apache2/ssl.key

# install vhost and jk settings
cp %{SOURCE0} $RPM_BUILD_ROOT%{_sysconfdir}/apache2/conf.d
cp %{SOURCE1} $RPM_BUILD_ROOT%{_sysconfdir}/apache2/conf.d
cp %{SOURCE2} $RPM_BUILD_ROOT%{_sysconfdir}/apache2/vhosts.d

%{__portsed} 's|@@PROJECTNAME@@|%{projectname}|g' $RPM_BUILD_ROOT%{_sysconfdir}/apache2/conf.d/workers.properties
%{__portsed} 's|@@PROJECTNAME@@|%{projectname}|g' $RPM_BUILD_ROOT%{_sysconfdir}/apache2/conf.d/jk.conf
%{__portsed} 's|@@PROJECTNAME@@|%{projectname}|g' $RPM_BUILD_ROOT%{_sysconfdir}/apache2/vhosts.d/%{projectname}.conf
%{__portsed} 's|@@PROJECTNAME@@|%{projectname}|g' $RPM_BUILD_ROOT%{_sysconfdir}/apache2/vhosts.d/%{projectname}-ssl.conf
%{__portsed} 's|@@DOMAIN@@|%{domain}|g' $RPM_BUILD_ROOT%{_sysconfdir}/apache2/conf.d/workers.properties
%{__portsed} 's|@@DOMAIN@@|%{domain}|g' $RPM_BUILD_ROOT%{_sysconfdir}/apache2/conf.d/jk.conf
%{__portsed} 's|@@DOMAIN@@|%{domain}|g' $RPM_BUILD_ROOT%{_sysconfdir}/apache2/vhosts.d/%{projectname}.conf
%{__portsed} 's|@@DOMAIN@@|%{domain}|g' $RPM_BUILD_ROOT%{_sysconfdir}/apache2/vhosts.d/%{projectname}-ssl.conf

%clean
rm -rf $RPM_BUILD_ROOT

%pre

%post
if [ "$1" == "1" ]; then

  # Enable Apache module (silently avoid duplicates)
  a2enmod deflate >>/dev/null
  a2enmod jk >>/dev/null
  service apache2 restart

fi


%preun

%postun

%files
%defattr(-,root,root)
%config(noreplace) %{_sysconfdir}/apache2/vhosts.d/
%config(noreplace) %{_sysconfdir}/apache2/conf.d/

%changelog
* Wed Mar 29 2013 henri.gomez@gmail.com 1.1.0-1
- Remove SSL support (and secrets cert/keys)
- VHost is now named default
- Use load-balancing with 2 workers in active/active mode
- Requires apache2-worker (threaded mode) for better performance and lowest memory footprint

* Sun Apr 16 2012 pierre.antoine.gregoire@gmail.com 1.0.0-2
- Add SSL support

* Sun Apr 16 2012 pierre.antoine.gregoire@gmail.com 1.0.0-1
- Refactoring of tinyupload name.

* Sun Apr 1 2012 henri.gomez@gmail.com 1.0.0-0
- Initial RPM
