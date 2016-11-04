# Class: storm::install
#
# This module manages storm installation
#
# Parameters: None
#
# Actions: None
#
# Requires:
#
# Sample Usage: include storm::install
#
class storm::install {

  $BASE_URL="https://bitbucket.org/qanderson/storm-deb-packaging/downloads/"
  $ZMQ_FILE="libzmq0_2.1.7_amd64.deb"
  $JZMQ_FILE="libjzmq_2.1.7_amd64.deb"
  $STORM_FILE="storm_0.8.1_all.deb"

  package { "wget": ensure => latest }
  
  # call fetch for each file
  exec { "wget_storm": 
  	command => "/usr/bin/wget ${BASE_URL}${STORM_FILE}" }
  exec {"wget_zmq": 
  	command => "/usr/bin/wget ${BASE_URL}${ZMQ_FILE}" }
  exec { "wget_jzmq": 
  	command => "/usr/bin/wget ${BASE_URL}${JZMQ_FILE}" }
  
  #call package for each file
  package { "libzmq0":
    provider => dpkg,
    ensure => installed,
    source => "${ZMQ_FILE}",
    require => Exec['wget_zmq']
  }
  
  #call package for each file
  package { "libjzmq":
    provider => dpkg,
    ensure => installed,
    source => "${JZMQ_FILE}",
    require => [Exec['wget_jzmq'],Package['libzmq0']]
  }
  
  #call package for each file
  package { "storm":
    provider => dpkg,
    ensure => installed,
    source => "${STORM_FILE}",
    require => [Exec['wget_storm'], Package['libjzmq']]
  }
  
  

}
