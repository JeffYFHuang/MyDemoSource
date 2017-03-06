#! /usr/bin/env Rscript

# reducer.R - Wordcount program in R
# script for Reducer (R-Hadoop integration)
#library(rhbase)

Sys.setenv("HADOOP_CMD"="/media/data/hadoop_ecosystem/hadoop/bin/hadoop")
Sys.setenv("HADOOP_STREAMING"="/media/data/hadoop_ecosystem/hadoop/share/hadoop/tools/lib/hadoop-streaming-2.6.0.jar")
require(compiler)
require(rmr2)
require(rhdfs)
a<-enableJIT(3)

trimWhiteSpace <- function(line) gsub("(^ +)|( +$)", "", line)

num.models <- 200
input <- NULL #"/data/hrvdata"
output <- NULL #"/data/train_output"

args=(commandArgs(TRUE))

if (length(args) != 0){
  for (i in 1:length(args)){
     eval(parse(text=args[[i]]))
  }
}

execution = "Exe. Rscript runFCRc.R input=\"'/data/rfe_output'\" output=\"'/data/feature_output'\" num.models=200";
if (is.null(input))
   stop(paste("please provide input path!", execution))
if (is.null(output))
   stop(paste("please provide output path!", execution))
if (is.null(num.models))
   stop(paste("please provide numbers of models trained!", execution))

hdfs.init()
#print(hdfs.exists(output))
if (hdfs.exists(output)) hdfs.rmr(output)
#if (hdfs.exists(models_output)) hdfs.rmr(models_output)

splitLine <- function(line) {
    val <- unlist(strsplit(line, "\t"))
    list(subject = val[1], features = val[2])
}

# MAP function
feature.map <- function(k, input) {

  getfeatures <- function(line) {
     split <- splitLine(line)
     features = eval(parse(text=split$features))
     keyval(features, 1)
  }

  # here is where we generate the actual sampled data
  c.keyval(lapply(as.list(input), getfeatures))
}

feature.map <- cmpfun(feature.map)

# REDUCE function
feature.reduce <- function(k, counts) {
  keyval(k, sum(counts)/num.models)
}

#rmr.options(
#  backend = "hadoop",
  #hdfs.tempdir = "tmp", #file.path("/home", system("whoami", TRUE),"tmp-rmr2", basename(tempdir())),
#  backend.parameters = list(
       # D = "mapreduce.map.java.opts=-Xmx1024M",
       # D = "mapreduce.reduce.java.opts=-Xmx1024M",
       # D = "mapreduce.map.memory.mb=-Xmx1024M",
       # D = "mapreduce.reduce.memory.mb=-Xmx1024M",
#        D = "mapreduce.job.maps=6",
#        D = "mapreduce.job.reduces=3"))

a <- mapreduce(input=input,
          input.format="text",
       #   output.format="text",
          map=feature.map,
          reduce=feature.reduce,
          output=output, backend.parameters=list(hadoop=list(D='mapred.reduce.tasks=1',
          D='mapred.map.tasks=10')))
