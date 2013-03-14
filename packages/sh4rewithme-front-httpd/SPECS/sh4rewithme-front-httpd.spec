%ifos darwin
%define __portsed sed -i "" -e
%else
%define __portsed sed -i
%endif

%define projectname sh4rewithme
%define domain sh4rewith.me

Name: %{projectname}-front-httpd
Version: 1.0.0
Release: 2
Summary: Apache2 Front configuration for %{projectname}
Group: %{projectname}/tools
URL: http://www.sh4rewith.me/
Vendor: %{projectname}
Packager: %{projectname}
License: ASL
BuildArch:  noarch

BuildRoot: %{_tmppath}/build-%{name}-%{version}-%{release}

Requires: apache2-mod_jk

Source0: workers.properties
Source1: jk.conf
Source2: %{projectname}.conf
Source3: %{projectname}-ssl.conf
Source4: %{projectname}.crt
Source5: %{projectname}.key
Source6: GandiStandardSSLCA.pem

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
cp %{SOURCE3} $RPM_BUILD_ROOT%{_sysconfdir}/apache2/vhosts.d

cp %{SOURCE4} $RPM_BUILD_ROOT%{_sysconfdir}/apache2/ssl.crt
cp %{SOURCE5} $RPM_BUILD_ROOT%{_sysconfdir}/apache2/ssl.key
cp %{SOURCE6} $RPM_BUILD_ROOT%{_sysconfdir}/apache2/ssl.crt

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
  a2enmod ssl >>/dev/null
  service apache2 restart

fi


%preun

%postun

%files
%defattr(-,root,root)
%config(noreplace) %{_sysconfdir}/apache2/vhosts.d/
%config(noreplace) %{_sysconfdir}/apache2/conf.d/
%config(noreplace) %{_sysconfdir}/apache2/ssl.crt/
%config(noreplace) %{_sysconfdir}/apache2/ssl.key/

%changelog
* Sun Apr 16 2012 pierre.antoine.gregoire@gmail.com 1.0.0-2
- Add SSL support

* Sun Apr 16 2012 pierre.antoine.gregoire@gmail.com 1.0.0-1
- Refactoring of tinyupload name.

* Sun Apr 1 2012 henri.gomez@gmail.com 1.0.0-0
- Initial RPM