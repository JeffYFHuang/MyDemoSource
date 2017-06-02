#!/usr/bin/env bash

bdate=$1 #2017-05-25
max=$2 #31
for i in `seq 0 $max`
do
   cdate=$(date '+%C%y-%m-%d' -d "$bdate+$i days")
   echo $cdate
   Rscript send_phydummy.R "ip='172.18.161.1'" port=44448 bdate="'$cdate'" range=1:1200 hours=24 &
   Rscript send_phydummy.R "ip='172.18.161.1'" port=44448 bdate="'$cdate'" range=1201:2400 hours=24 &
   Rscript send_phydummy.R "ip='172.18.161.1'" port=44448 bdate="'$cdate'" range=2401:3600 hours=24 &
   Rscript send_phydummy.R "ip='172.18.161.1'" port=44448 bdate="'$cdate'" range=3601:5000 hours=24
done
