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
enableJIT(3)

master = "10.0.0.5"
trimWhiteSpace <- function(line) gsub("(^ +)|( +$)", "", line)

frac.per.model <- 0.02
num.models <- 20

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
fit.trees <- function(k, v) {
  # rmr rbinds the emited values, so v is a dataframe
  # note that do.trace=T is used to produce output to stderr to keep
  # the reduce task from timing out
  drops <- c("startTime","endTime")
  v <- v[, !(names(v) %in% drops)]
  v$label=factor(v$label)
  features <- c("label", "SDNN", "TINN", "LFHF", "LFnu", "SD2", "SD12", "TP", "ApEn", "type")
  v <- v[, features]

  rf <- randomForest(formula=label ~ ., data=v, na.action=na.omit, ntree=5, do.trace=FALSE)
  # rf is a list so wrap it in another list to ensure that only
  # one object gets emitted. this is because keyval is vectorized
  keyval(k, list(forest=rf))
  #keyval(k, paste(names(v), collapse=" "))
}

a <- mapreduce(input="/data/hrvdata",
          input.format="text", #make.input.format("csv", sep = "\t"),
#          output.format="text", #make.input.format("csv", sep = "\t"),
          map=poisson.subsample,
          reduce=fit.trees,
          output="/data/output")

raw.forests <- values(from.dfs("/data/output")) #"make.input.format("csv", sep = "\t")))
forest <- do.call(combine, raw.forests)
#summary(forest)
#    split <- splitLine(lines[1])
#    hrv.data = fromJSON(split$HRV)
#    df <- data.frame(t(matrix(unlist(hrv.data), length(hrv.data[[1]]))))
#    names(df) <- names(hrv.data[[1]])
#    poisson.subsample(df)
