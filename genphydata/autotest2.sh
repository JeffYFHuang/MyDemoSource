#!/usr/bin/env bash

bdate=$1
days=$2
ip=$3 #flume server ip

. ./autosend.sh $bdate $days $ip
. ./autocal.sh $bdate $days
