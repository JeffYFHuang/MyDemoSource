#! /usr/bin/env Rscript

# reducer.R - Wordcount program in R
# script for Reducer (R-Hadoop integration)
#library(rhbase)
library(rjson)
require(randomForest)
require(rmr2)

master = "10.0.0.5"
trimWhiteSpace <- function(line) gsub("(^ +)|( +$)", "", line)
num.models = 20

splitLine <- function(line) {
    val <- unlist(strsplit(line, "\t"))
    list(subject = val[1], HRV = val[2])
}

# MAP function
  getData <- function(line) {
     split <- splitLine(line)
     hrv.data = fromJSON(split$HRV)
     df <- data.frame(t(matrix(unlist(hrv.data), length(hrv.data[[1]]))))
     names(df) <- names(hrv.data[[1]])
     #draws <- rpois(n=nrow(df), lambda=frac.per.model)
     #indices <- rep((1:nrow(df)), draws)
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
    x <- data[which(groupsample==i), ]
    x <- keyval(i, x)
    #rf <- randomForest(formula=label~., data=data[which(groupsample==i), ], na.action=na.roughfix, ntree=2, do.trace=FALSE)
    #summary(rf)
    #cat(i, paste(names(data), collapse=" "), "ttttt\n", sep="")
    #cat(i, paste(data[which(groupsample==i), ], collapse=" "), "\n", sep="")
  }

con <- file("stdin", open = "r")
#line <- readLines(con, n = 1, warn = FALSE)
#data <- getData(line)
data=NULL
count <- 0
while (length(line <- readLines(con, n = 1, warn = FALSE)) > 0) {
  data <- rbind(data, getData(line))
}

close(con)

  cat("data loaded!\n")
  drops <- c("startTime","endTime")
  data <- data[, !(names(data) %in% drops)]
  data$label=factor(data$label)

  groupsample <- sample(1:num.models, nrow(data), replace=T)
  c.keyval(lapply(1:num.models, generate.sample, groupsample))
