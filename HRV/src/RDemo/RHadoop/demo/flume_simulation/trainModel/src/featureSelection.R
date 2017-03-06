#! /usr/bin/env Rscript

# reducer.R - Wordcount program in R
# script for Reducer (R-Hadoop integration)
#library(rhbase)

Sys.setenv("HADOOP_CMD"="/media/data/hadoop_ecosystem/hadoop/bin/hadoop")
Sys.setenv("HADOOP_STREAMING"="/media/data/hadoop_ecosystem/hadoop/share/hadoop/tools/lib/hadoop-streaming-2.6.0.jar")
require(compiler)
require(rhdfs)
require(rmr2)
require(rjson)
require(randomForest)
require(caret)
a<-enableJIT(3)

trimWhiteSpace <- function(line) gsub("(^ +)|( +$)", "", line)

#frac.per.model <- 0.02
num.models <- 200
input <- NULL #"/data/hrvdata"
output <- NULL #"/data/train_output"

args=(commandArgs(TRUE))

if (length(args) != 0){
  for (i in 1:length(args)){
     eval(parse(text=args[[i]]))
  }
}

execution = "Exe. Rscript runRfeRc.R input=\"'/data/hrvdata'\" output=\"'/data/rfe_output'\" num.models=100";
if (is.null(input))
   stop(paste("please provide input path!", execution))
if (is.null(output))
   stop(paste("please provide output path!", execution))
#if (is.null(models_output))
#   stop(paste("please provide store path of models trained!", execution))

hdfs.init()
#print(hdfs.exists(output))
if (hdfs.exists(output)) hdfs.rmr(output)
#if (hdfs.exists(models_output)) hdfs.rmr(models_output)

splitLine <- function(line) {
    val <- unlist(strsplit(line, "\t"))
    list(subject = val[1], HRV = val[2])
}

# MAP function
poisson.subsample <- function(k, input) {

  getData <- function(line) {
     split <- splitLine(line)
     hrv.data = fromJSON(split$HRV)
     df <- data.frame(t(matrix(unlist(hrv.data), length(hrv.data[[1]]))))
     names(df) <- names(hrv.data[[1]])
     df
  }

  # this function is used to generate a sample from the current block of data
  generate.sample <- function(i, groupsample) {
    # generate N Poisson variables
    #draws <- rpois(n=nrow(data), lambda=frac.per.model)
    # compute the index vector for the corresponding rows,
    # weighted by the number of Poisson draws
    #indices <- rep((1:nrow(data)), draws)
    # emit the rows; RHadoop takes care of replicating the key appropriately
    # and rbinding the data frames from different mappers together for the
    # reducer   

    keyval(i, data[which(groupsample==i), ])
  }

  data <- NULL
  for (i in 1:length(input)) {
      data <- rbind(data, getData(input[i]))
  }

  groupsample <- sample(1:num.models, nrow(data), replace=T)

  # here is where we generate the actual sampled data
  c.keyval(lapply(1:num.models, generate.sample, groupsample))
}

poisson.subsample <- cmpfun(poisson.subsample)

# REDUCE function
feature.selection <- function(k, v) {
  drops <- c("startTime","endTime")
  v <- v[, !(names(v) %in% drops)]
  v$label=factor(v$label)
  inTrain <- createDataPartition(y = v$label, p = .75, list = FALSE)
  training <- v[inTrain,]
  testing <- na.omit(v[-inTrain,])

  set.seed(400)
  control <- rfeControl(functions=rfFuncs, method="cv", number=3)
  results <- rfe(v[, !(names(v) %in% c("label"))], v[, names(v) %in% c("label")], sizes=c(11:15), rfeControl=control)
  keyval(k, list(predictors(results)))
}

backend.parameters = list(hadoop=list(D=paste('mapreduce.job.maps=', num.models, sep=""), D='mapreduce.job.reduces=16',
                                      D='mapreduce.map.java.opts=-Xmx2048m',
                                      D='mapreduce.reduce.java.opts=-Xmx3072m',
                                      D='mapreduce.map.memory.mb=2048',
                                      D='mapreduce.reduce.memory.mb=3072',
                                      D='mapreduce.child.java.opts=-Xmx3072m'
                                      ))

a <- mapreduce(input=input,
          input.format="text",
          output.format="text",
          map=poisson.subsample,
          reduce=feature.selection,
          output=output,
          backend.parameters=backend.parameters)
