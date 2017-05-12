#!/usr/bin/env bash

hadoop fs -rmr /tmp/restore
hadoop jar $HADOOP_HOME/share/hadoop/tools/lib/hadoop-streaming-2.6.0.jar -D mapreduce.job.maps=$1 -files src/ -mapper src/processPhyReducer.R -input $2 -output /tmp/restore
hadoop fs -rmr /tmp/restore

firstdayofmonth=$3
daysofmonth=$4
phytypes=('context' 'step' 'sleep' 'hrm')
ptypes=('date' 'week' 'month')

for i in `seq 0 $daysofmonth`
do
   cdate=$(date '+%C%y-%m-%d' -d "$firstdayofmonth+$i days")
   echo $cdate

   p1=`echo $cdate | cut -d - -f 1`
   p2=`echo $cdate | cut -d - -f 2`
   p3=`echo $cdate | cut -d - -f 3`

   path=$p1/$p2/$p3

   for phytype in "${phytypes[@]}"
   do
      for ptype in "${ptypes[@]}"
      do
          echo Rscript src/processCassPhyDateData.R date="'$cdate'" phytype="'$phytype'" ptype="'$ptype'"
          Rscript src/processCassPhyDateData.R date="'$cdate'" phytype="'$phytype'" ptype="'$ptype'"
      done
   done
done
