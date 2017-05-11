#!/usr/bin/env bash

bdate=$1 #2017-05-25
max=$2 #31
for i in `seq 0 $max`
do
   cdate=$(date '+%C%y-%m-%d' -d "$bdate+$i days")
   echo $cdate
   Rscript send_phydummy.R "ip='172.18.161.1'" port=44448 bdate="'$cdate'" range=1:5000 hours=24 &
   Rscript send_phydummy.R "ip='172.18.161.1'" port=44448 bdate="'$cdate'" range=5001:10000 hours=24 &
   Rscript send_phydummy.R "ip='172.18.161.1'" port=44448 bdate="'$cdate'" range=10001:15000 hours=24 &
   Rscript send_phydummy.R "ip='172.18.161.1'" port=44448 bdate="'$cdate'" range=15001:20000 hours=24
done
