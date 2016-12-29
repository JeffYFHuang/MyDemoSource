#!/bin/bash

input=$1
output=$2
hadoop fs -rm -r $output
Rscript runRc.R input="'$input'" output="'$output'"
