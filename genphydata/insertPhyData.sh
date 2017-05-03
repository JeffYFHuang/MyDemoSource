#!/usr/bin/env bash

input=/data/phydummy/*/$2
echo $input

p1=`echo $2 | cut -d \/ -f 1`
p2=`echo $2 | cut -d \/ -f 2`
p3=`echo $2 | cut -d \/ -f 3`
p4=`echo $2 | cut -d \/ -f 4`
p5=`echo $2 | cut -d \/ -f 5`

output=$p2.$p3.$p4.$p5
#echo $output
output=/data/phydummyoutput/$output
echo $output

hadoop fs -rm -r $output

hadoop jar $HADOOP_HOME/share/hadoop/tools/lib/hadoop-streaming-2.6.0.jar -D mapreduce.job.maps=$1 -files src/ -mapper src/processPhyMapper.R -reducer src/processPhyReducer.R -input $input -output $output
