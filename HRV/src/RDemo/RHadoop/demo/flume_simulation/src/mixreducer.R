#! /usr/bin/env Rscript

# reducer.R - Wordcount program in R
# script for Reducer (R-Hadoop integration)
Sys.setenv("HADOOP_CMD"="/usr/local/hadoop/bin/hadoop")
Sys.setenv("HADOOP_STREAMING"="/usr/local/hadoop/share/hadoop/tools/lib/hadoop-streaming-2.6.0.jar")
setwd("./apnea/")
source("HRVFUNS.R")
#require(RHRV)
#require(rjson)

startTime = 0
endTime = 0
size=300 #seconds
overlap = 0 #seconds: 0 -> non-overlap;
hdfs.root <- '/HRVData'
hdfs.data <- file.path(hdfs.root, 'out')
#hdfs.out <- file.path(hdfs.root, 'out')

trimWhiteSpace <- function(line) gsub("(^ +)|( +$)", "", line)

splitLine <- function(line) {
    val <- unlist(strsplit(line, "\t"))
    list(word = val[1], count = as.double(val[2]))
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

hdfs.init()
for (w in ls(env, all = TRUE)){
  subject = strsplit(w, "[.]")[[1]][1]
  HRVData = CreateHRVData()
  HRVData = SetVerbose(HRVData, FALSE)
  HRVData$Beat$Time = sort(get(w, envir = env))
  HRVData$Beat = filterHR(HRVData, minbpm = 25, maxbpm = 180)
  if (is.null(HRVData$Beat))
     next
  HRVData$BeatTimeInterval$begin = head(HRVData$Beat$Time, n = 1)
  HRVData$BeatTimeInterval$end = tail(HRVData$Beat$Time, 1)

  begindex = which(diff(HRVData$Beat$Time) > 30)
  beg = HRVData$Beat$Time[begindex]
  end = HRVData$Beat$Time[begindex + 1]

  if (length(begindex) > 0) {
     HRVData$BeatTimeInterval$begin = c(HRVData$BeatTimeInterval$begin, end)
     HRVData$BeatTimeInterval$end = c(beg, HRVData$BeatTimeInterval$end)
  }

  load("current.mod")
  features = getHRVFeatures(HRVData, startTime, endTime, size, overlap, mod=mod.fit)
  jsonData<-toJSON(features)
  #print(jsonData)
  #write json data to hdfs
  #rmr.str(fileName)
  #writeJsonToHDFS(jsonData,  hdfs.data, paste(w, ".hrv", sep=""))#folder, paste(fileName, ".hrv", sep=""))
  to.dfs("1", file.path(hdfs.data, paste(subject, ".", names(Splitting.beat[i]), ".done", sep="")))
  #kv
  #cat(paste(w, names(Splitting.beat[i]), sep=""), "\t", jsonData, "\n", sep = "")
  cat(w, "\t", jsonData, "\n", sep = "")
}
