#! /usr/bin/env Rscript

# reducer.R - Wordcount program in R
# script for Reducer (R-Hadoop integration)
#library(rhbase)

Sys.setenv("HADOOP_CMD"="/media/data/hadoop_ecosystem/hadoop/bin/hadoop")
Sys.setenv("HADOOP_STREAMING"="/media/data/hadoop_ecosystem/hadoop/share/hadoop/tools/lib/hadoop-streaming-2.6.0.jar")
require(rjson)
require(rPython)
require(compiler)
require(rmr2)
a<-enableJIT(3)

master = "172.18.161.1"
trimWhiteSpace <- function(line) gsub("(^ +)|( +$)", "", line)

#frac.per.model <- 0.02
num.models <- 50
input <- NULL #"/data/phydummy/*/*/*/*/08/*"
output <- NULL #"/data/phydummyoutput"

args=(commandArgs(TRUE))

if (length(args) != 0){
  for (i in 1:length(args)){
     eval(parse(text=args[[i]]))
  }
}

execution = "Exe. Rscript runRc.R input=\"'/data/phydummy/*/*/*/*/08/*'\" output=\"'/data/phydummyoutput'\"";
if (is.null(input))
   stop(paste("please provide input path!", execution))
if (is.null(output))
   stop(paste("please provide output path!", execution))

if (dfs.exists(output))
   dfs.rmr(output)

# MAP function
poisson.subsample <- function(k, input) {

  filepath <- current.input()
  keyspace <- "elmtest"
  if (nchar(filepath) > 0) {
     path <- unlist(strsplit(filepath, split="/"))
     keyspace <- path[6] 
  }

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
fit.trees <- function(k, v) {
  # rmr rbinds the emited values, so v is a dataframe
  # note that do.trace=T is used to produce output to stderr to keep
  # the reduce task from timing out
  drops <- c("startTime","endTime")
  v <- v[, !(names(v) %in% drops)]
  #features <- c("label", "SDNN", "TINN", "LFHF", "LFnu", "SD2", "SD12", "TP", "ApEn", "type")
  if (feature_prob > 0) 
     v <- v[, c("label", features)]
  v$label=factor(v$label)
  inTrain <- createDataPartition(y = v$label, p = .1, list = FALSE)
  training <- v[inTrain,]
  testing <- na.omit(v[-inTrain,])

  set.seed(400)
  ctrl <- trainControl(method="repeatedcv", number=10, repeats = 3)
#  mod.fit <- train(label ~ ., data = training, method = "rf", trControl = ctrl, ntree=5, na.action=na.omit, trace=FALSE)
#  mod.fit <- train(label ~ ., data = training, method = "knn", trControl = ctrl, na.action=na.omit)
  mod.fit <- train (label ~ ., data = training, method = "rpart", trControl = ctrl, tuneLength=20, na.action=na.omit)
#  rf <- randomForest(formula=label ~ ., data=v, na.action=na.omit, ntree=5, do.trace=FALSE)
  # rf is a list so wrap it in another list to ensure that only
  # one object gets emitted. this is because keyval is vectorized
  #keyval(k, list(forest=rf))
  #keyval(k, paste(names(v), collapse=" "))
  p <- predict(mod.fit$finalModel, newdata = testing[which(names(testing)!="label")], type="class")
  #Get the confusion matrix to see accuracy value and other parameter values
  #confusionMatrix(Predict, testing$label)
#  to.dfs(testing, output = testDataPath)
  keyval(k, list(list(mod=mod.fit$finalModel, p=p, t=testing$label)))
}

backend.parameters = list(hadoop=list(D=paste('mapreduce.job.maps=', num.models, sep=""), D='mapreduce.job.reduces=16',
                                      D='mapreduce.map.java.opts=-Xmx2048m',
                                      D='mapreduce.reduce.java.opts=-Xmx3072m',
                                      D='mapreduce.map.memory.mb=2048',
                                      D='mapreduce.reduce.memory.mb=3072',
                                      D='mapreduce.child.java.opts=-Xmx3072m'
                                      ))

a <- mapreduce(input=input,
          input.format="text", #make.input.format("csv", sep = "\t"),
#          output.format="text", #make.input.format("csv", sep = "\t"),
          map=poisson.subsample,
          reduce=fit.trees,
          output=output, backend.parameters=backend.parameters)
