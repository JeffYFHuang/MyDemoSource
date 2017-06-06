#!/usr/bin/env bash

bdate=$1 #2017-06-1
max=$2 #31
ip=$3

echo $ip
isProcessRunning() { if [ $1 > /dev/null ]; then retval=true; else retval=false; fi; echo $retval; }
flume_pid=`/bin/ps -fu $USER| grep "flume" | grep -v "grep" | awk '{print $2}'`
if ! `isProcessRunning $flume_pid`; then
    flume-ng agent --conf-file hdfs_phydummy.conf --name a1 -Dflume.root.logger=INFO,console &
fi

isProcessRunning $!
if `isProcessRunning $!`; then
   sleep 5
   for i in `seq 0 $max`
   do
      cdate=$(date '+%C%y-%m-%d' -d "$bdate+$i days")
      echo $cdate
      Rscript send_phydummy.R "ip='$ip'" port=44448 bdate="'$cdate'" range=1:250 hours=24 &
      Rscript send_phydummy.R "ip='$ip'" port=44448 bdate="'$cdate'" range=251:500 hours=24 &
      Rscript send_phydummy.R "ip='$ip'" port=44448 bdate="'$cdate'" range=501:750 hours=24 &
      Rscript send_phydummy.R "ip='$ip'" port=44448 bdate="'$cdate'" range=751:1000 hours=24
   done
fi

      COUNTER=0
      while true; do
         echo R.pids: $(pidof R)
         if [ -z "$(pidof R)" ]; then
            flume_pid=`/bin/ps -fu $USER| grep "flume" | grep -v "grep" | awk '{print $2}'`
            echo flume pid: $flume_pid
            if `isProcessRunning $flume_pid`; then
              kill -9 $flume_pid
              echo "kill flume!"
              break
            else
              break
            fi
         fi
         let COUNTER=COUNTER+1
         if [ $COUNTER -eq 10 ]; then
            flume_pid=`/bin/ps -fu $USER| grep "flume" | grep -v "grep" | awk '{print $2}'`
            echo flume pid: $flume_pid
            if `isProcessRunning $flume_pid`; then
              kill -9 $flume_pid
              echo "kill flume!"
              break
            else
              break
            fi
            break
         fi
         sleep 5
      done
