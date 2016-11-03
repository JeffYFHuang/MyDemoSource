#! /usr/bin/env Rscript

# mapper.R - Wordcount program in R
# script for Mapper (R-Hadoop integration)
#! /usr/bin/env Rscript

# reducer.R - Wordcount program in R
# script for Reducer (R-Hadoop integration)
Sys.setenv("HADOOP_CMD"="/usr/local/hadoop/bin/hadoop")
Sys.setenv("HADOOP_STREAMING"="/usr/local/hadoop/share/hadoop/tools/lib/hadoop-streaming-2.6.0.jar")
setwd("./src/")
source("HRVFUNS.R")

require(caret)
load(url("http://master:50070/webhdfs/v1/models/randomforest.mod?op=OPEN"))

trimWhiteSpace <- function(line) gsub("(^ +)|( +$)", "", line)
splitIntoWords <- function(line) unlist(strsplit(line, "[[:space:]]+"))

splitLine <- function(line) {
    val <- unlist(strsplit(line, "\t"))
    list(word = val[1], beatLine = val[2])
}

con <- file("stdin", open = "r")
while (length(line <- readLines(con, n = 1, warn = FALSE)) > 0) {
    line <- trimWhiteSpace(line)
    split <- splitLine(line)
    word <- split$word
    beats <- na.omit(as.double(splitIntoWords(split$beatLine)))
    HRV = calculateHRV(beats)
    pos = charmatch(strsplit(word, "[.]")[[1]][2], c("beatsInEpisodes", "beatsOutEpisodes"))

    if (!is.null(mod.fit) && !any(is.na(HRV))) {
      HRV$label = predict(mod.fit, HRV)
      if(HRV$label == "Y") HRV$label = "1"
      else if(HRV$label == "N") HRV$label = "0"
    } else {
      HRV$label = NA
    }

    if (!is.na(pos)) {
       if (pos == 1) {
          HRV$label = 1
       } else if (pos == 2){
          HRV$label = 0
       }
    }

    ## **** can be done as cat(paste(words, "\t1\n", sep=""), sep="")
    cat(strsplit(word, "[.]")[[1]][1], "\t", toJSON(HRV), "\n", sep="")
    #cat(toJSON(HRV), "\n", sep="") # for spark dataframe
}
close(con)
