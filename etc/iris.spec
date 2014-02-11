#
# Fedora RPM Spec file for IRIS
# Written by Michael Darter, December 2008
#     and Douglas Lau
#
# IRIS -- Intelligent Roadway Information System
# Copyright (C) 2009-2014  Minnesota Department of Transportation
#
# This program is free software; you can redistribute it and/or modify
# it under the terms of the GNU General Public License as published by
# the Free Software Foundation; either version 2 of the License, or
# (at your option) any later version.
#
# This program is distributed in the hope that it will be useful,
# but WITHOUT ANY WARRANTY; without even the implied warranty of
# MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
# GNU General Public License for more details.

%define name		@@NAME@@
%define version		@@VERSION@@
%define _topdir		@@BUILD.RPM@@
%define _installdir	%{_topdir}/BUILDROOT
%define _serverlink	/usr/share/java/iris-server
%define _serverdir	%{_serverlink}-%{version}
%define _clientlink	/var/www/html/iris-client
%define _clientdir	%{_clientlink}-%{version}

Name:		%{name}
Summary:	The IRIS advanced traffic management system (ATMS).
Version:	%{version}
Release:	@@RPM.RELEASE@@
License:	GPL
Group:		Applications/Engineering
Provides:	%{name}
Source:		%{name}-%{version}.tar.gz
URL:		@@RPM.URL@@
BuildArch:	noarch
Buildroot:	%{buildroot}
Vendor:		@@RPM.PACKAGER@@
Packager:	@@RPM.PACKAGER@@
Requires:	java-1.7.0-openjdk, postgresql-server, postgresql-jdbc, httpd

%Description
@@RPM.DESCRIPTION@@

# prepare sources
%prep
%setup -q 

# build from source
%build
ant

# install the built files
%install
ant -Dinstall.dir=%{_installdir} install

# prepare to install RPM
%pre
if [ $1 == 1 ]; then
	useradd -r -M tms
	if [ "$?" == "9" ]
	then
		exit 0
	fi
fi

# All files included in RPM are listed here.
%files

# /etc/iris
%dir %attr(0750,tms,tms) /etc/iris
%defattr(0640,tms,tms)
%config(noreplace) /etc/iris/iris-client.properties
%config(noreplace) /etc/iris/iris-server.properties

# /usr/bin
%defattr(0755,root,root)
/usr/bin/iris_ctl

# %{_unitdir}
%defattr(0644,root,root)
%{_unitdir}/iris.service

# /etc/httpd/conf.d
%defattr(0644,root,root)
/etc/httpd/conf.d/iris.conf

# /usr/share/java/iris-server-x.x.x
%dir %attr(0755,root,root) %{_serverdir}
%defattr(0644,root,root)
%{_serverdir}/iris-server-%{version}.jar
%{_serverdir}/iris-common-%{version}.jar
%{_serverdir}/mail.jar
%{_serverdir}/geokit-@@GEOKIT.VERSION@@.jar
%{_serverdir}/scheduler-@@SCHEDULER.VERSION@@.jar
%{_serverdir}/sonar-server-@@SONAR.VERSION@@.jar

# /var/lib/iris
%dir %attr(3775,tms,tms) /var/lib/iris
%attr(0444,tms,tms) /var/lib/iris/sql/
%dir %attr(0775,tms,tms) /var/lib/iris/sql
%dir %attr(0775,tms,tms) /var/lib/iris/sql/fonts
%dir %attr(0775,tms,tms) /var/lib/iris/meter
%dir %attr(0775,tms,tms) /var/lib/iris/traffic

# /var/log/iris
%dir %attr(3775,tms,tms) /var/log/iris

# /var/www/html/iris_xml
%dir %attr(3775,tms,tms) /var/www/html/iris_xml

# client: /var/www/html/iris-client-x.x.x
%dir %attr(0755,apache,apache) %{_clientdir}
%dir %attr(0755,apache,apache) %{_clientdir}/images
%dir %attr(0755,apache,apache) %{_clientdir}/lib
%defattr(0444,apache,apache)
%{_clientdir}/index.html
%{_clientdir}/mail.jnlp
%{_clientdir}/iris-client.jnlp
%{_clientdir}/images/iris.png
%{_clientdir}/images/iris_icon.png
%{_clientdir}/lib/mail.jar
%{_clientdir}/lib/iris-client-%{version}.jar
%{_clientdir}/lib/iris-common-%{version}.jar
%{_clientdir}/lib/mapbean-@@MAPBEAN.VERSION@@.jar
%{_clientdir}/lib/scheduler-@@SCHEDULER.VERSION@@.jar
%{_clientdir}/lib/sonar-client-@@SONAR.VERSION@@.jar
%{_clientdir}/lib/geokit-@@GEOKIT.VERSION@@.jar
%attr(0644,tms,apache) %{_clientdir}/session_ids
