#!/usr/bin/env bash

#start flume service
#flume-ng agent --conf-file hdfs_phydummy.conf --name a1 -Dflume.root.logger=INFO,console & #-Dflume.monitoring.type=ganglia -Dflume.monitoring.hosts=localhost:8649

#Rscript truncateKeyspacesTables.R
isProcessRunning() { if [ $1 > /dev/null ]; then retval=true; else retval=false; fi; echo $retval; }

firstdayofmonth=$1
daysofmonth=$2
hours=(02 08 14 20)
phytypes=('context' 'step' 'sleep' 'hrm')
ptypes=('date') #('date', 'week', 'month')

   for i in `seq 0 $daysofmonth`
   do
      cdate=$(date '+%C%y-%m-%d' -d "$firstdayofmonth+$i days")
      echo $cdate
# generate dummy an send to flume service to store data in hdfs.
      flume_pid=`/bin/ps -fu $USER| grep "flume" | grep -v "grep" | awk '{print $2}'`
      if ! `isProcessRunning $flume_pid`; then
         flume-ng agent --conf-file hdfs_phydummy.conf --name a1 & #-Dflume.root.logger=INFO,console &
      fi

      sleep 5
      if `isProcessRunning $$`; then
         Rscript send_phydummy.R "ip='172.18.161.1'" port=44448 bdate="'$cdate'" range=1:50 hours=24 &
         Rscript send_phydummy.R "ip='172.18.161.1'" port=44448 bdate="'$cdate'" range=51:100 hours=24 &
         Rscript send_phydummy.R "ip='172.18.161.1'" port=44448 bdate="'$cdate'" range=101:150 hours=24 &
         Rscript send_phydummy.R "ip='172.18.161.1'" port=44448 bdate="'$cdate'" range=151:200 hours=24 &
      fi

      while true; do
         echo $(pidof R)
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
         sleep 5
      done

      p1=`echo $cdate | cut -d - -f 1`
      p2=`echo $cdate | cut -d - -f 2`
      p3=`echo $cdate | cut -d - -f 3`

      path=$p1/$p2/$p3
      testpath=/data/physical/in/elm124614/$p1/$p2/$p3

      for hour in "${hours[@]}"
      do
         hadoop fs -test -d $testpath/$hour
         if [ $? == 0 ]; then
            echo . ./insertPhyData.sh 6 $path/$hour/*
            . ./insertPhyData.sh 6 $path/$hour
         else
            echo $testpath/$hour "not exists"
         fi
      done

      for ptype in "${ptypes[@]}"
      do
          for phytype in "${phytypes[@]}"
          do
              echo Rscript src/processCassPhyDateData.R date="'$cdate'" phytype="'$phytype'" ptype="'$ptype'"
              Rscript src/processCassPhyDateData.R date="'$cdate'" phytype="'$phytype'" ptype="'$ptype'"
          done
      done
   done
