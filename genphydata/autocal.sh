#!/usr/bin/env bash

#start flume service
#flume-ng agent --conf-file hdfs_phydummy.conf --name a1 -Dflume.root.logger=INFO,console & #-Dflume.monitoring.type=ganglia -Dflume.monitoring.hosts=localhost:8649

#Rscript truncateKeyspacesTables.R
isProcessRunning() { if [ $1 > /dev/null ]; then retval=true; else retval=false; fi; echo $retval; }

firstdayofmonth=$1
daysofmonth=$2
hours=(02 08 14 20)
phytypes=('context' 'step' 'hrm')
ptypes=('date') # 'month')

   for i in `seq 0 $daysofmonth`
   do
      cdate=$(date '+%C%y-%m-%d' -d "$firstdayofmonth+$i days")
      echo $cdate

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

   for i in `seq 0 $daysofmonth`
   do
       cdate=$(date '+%C%y-%m-%d' -d "$firstdayofmonth+$i days")
       echo $cdate

       echo Rscript src/processCassPhyDateData.R date="'$cdate'" phytype="'sleep'" ptype="'date'"
       Rscript src/processCassPhyDateData.R date="'$cdate'" phytype="'sleep'" ptype="'date'"
   done

   for phytype in "${phytypes[@]}"
   do
       echo Rscript src/processCassPhyDateData.R date="'$firstdayofmonth'" phytype="'$phytype'" ptype="'$ptype'"
       Rscript src/processCassPhyDateData.R date="'$firstdayofmonth'" phytype="'$phytype'" ptype="'month'"
   done
