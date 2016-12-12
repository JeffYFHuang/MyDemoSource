#!/bin/bash
echo "Running $1 Times"
for i in `seq 1 $1`;
  do 
     echo "$i"
     Rscript hdfs_send_beats.R "ip='10.0.0.5'" port=44448 num=$2 &
 done
