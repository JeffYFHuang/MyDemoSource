#!/bin/bash
name=storm
version=0.9.0-rc2
if [ "$1" == "-h" ]; then
  echo "Usage:"
  echo "$0 [-h] | [version]"
  echo $0  
  echo
  echo "version - the storm version. Default=$version" 
  echo " -h  - prints this message"
  echo 

  exit
else
  if [[ $1 ]] ; then
    version=$1
   
  fi
fi
echo "Building storm version:$version"
url=http://storm-project.net
buildroot=build
fakeroot=storm-${version}
origdir=$(pwd)
description="Storm is a distributed realtime computation system. Similar to how Hadoop provides a set of general primitives for doing batch processing, Storm provides a set of general primitives for doing realtime computation. Storm is simple, can be used with any programming language, is used by many companies, and is a lot of fun to use!"

#_ MAIN _#
rm -rf ${name}*.deb
rm -rf ${name}*.rpm
rm -rf ${fakeroot}
mkdir -p ${fakeroot}
wget https://dl.dropboxusercontent.com/s/p5wf0hsdab5n9kn/storm-${version}.zip
#wget https://dl.dropbox.com/u/133901206/storm-${version}.zip
#wget https://github.com/downloads/nathanmarz/storm/storm-${version}.zip
mv storm-${version}.zip ${fakeroot}
unzip ${fakeroot}/storm-${version}.zip
mv ${fakeroot}/storm-${version}/* ${fakeroot} &>/dev/null
rm -rf ${fakeroot}/storm-${version}.zip
rm -rf ${fakeroot}/logs
rm -rf ${fakeroot}/log4j
rm -rf ${fakeroot}/conf

#_ MAKE DIRECTORIES _#
rm -rf ${buildroot}
mkdir -p ${buildroot}
mkdir -p ${buildroot}/opt/storm
mkdir -p ${buildroot}/etc/default
mkdir -p ${buildroot}/etc/storm/conf.d
mkdir -p ${buildroot}/etc/init
mkdir -p ${buildroot}/var/log/storm
mkdir -p ${buildroot}/var/lib/storm

#_ COPY FILES _#
cp -R ${fakeroot}/* ${buildroot}/opt/storm
cp storm storm-nimbus storm-supervisor storm-ui storm-drpc ${buildroot}/etc/default
cp storm.yaml ${buildroot}/etc/storm
cp storm.log.properties ${buildroot}/etc/storm
cp storm-nimbus.conf storm-supervisor.conf storm-ui.conf storm-drpc.conf ${buildroot}/etc/init

#_ MAKE DEBIAN _#
cd ${buildroot}
fpm -t deb -n $name -v $version --description "$description" --url="$url" -a all --prefix=/ -d "libzmq0 >= 2.1.7" -d "libjzmq >= 2.1.7" -s dir -- .
fpm -t rpm -n $name -v $version --description "$description" --url="$url" -a all --prefix=/ -d "libzmq0 >= 2.1.7" -d "libjzmq >= 2.1.7" -s dir -- .
mv ${origdir}/${buildroot}/*.deb ${origdir}
mv ${origdir}/${buildroot}/*.rpm ${origdir}
cd ${origdir}

