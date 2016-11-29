#! /usr/bin/env Rscript

# reducer.R - Wordcount program in R
# script for Reducer (R-Hadoop integration)
Sys.setenv("HADOOP_CMD"="/media/Data/hadoop_ecosystem/hadoop/bin/hadoop")
Sys.setenv("HADOOP_STREAMING"="/media/Data/hadoop_ecosystem/hadoop/share/hadoop/tools/lib/hadoop-streaming-2.6.0.jar")
setwd("./src/")
source("HRVFUNS.R")

size=300 #seconds
overlap = 0 #seconds: 0 -> non-overlap;
hdfs.root <- '/HRVData'
hdfs.data <- file.path(hdfs.root, 'out')
#hdfs.out <- file.path(hdfs.root, 'out')

trimWhiteSpace <- function(line) gsub("(^ +)|( +$)", "", line)
splitIntoWords <- function(line) unlist(strsplit(line, "[[:space:]]+"))
splitLine <- function(line) {
    val <- unlist(strsplit(line, "\t"))
    list(word = val[1], count = as.double(splitIntoWords(val[2])))
}

env <- new.env(hash = TRUE)

con <- file("stdin", open = "r")
while (length(line <- readLines(con, n = 1, warn = FALSE)) > 0) {
    line <- trimWhiteSpace(line)
    split <- splitLine(line)
    word <- split$word
    count <- split$count
    if (exists(word, envir = env, inherits = FALSE)) {
        oldcount <- get(word, envir = env)
        assign(word, c(oldcount, count), envir = env)
    }
    else assign(word, count, envir = env)
}
close(con)

getData <- function (w) {
  data = list()
  data$Beat <- sort(get(w, envir = env))
  data$Subject <- w
  return(data)
}

ws = ls(env, all = TRUE)
data = lapply(ws, getData)
result = lapply(data, getSplitWindowBeats)
